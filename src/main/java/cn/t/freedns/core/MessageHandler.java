package cn.t.freedns.core;

import cn.t.freedns.core.data.Request;
import cn.t.freedns.threadpool.MonitoredThreadFactory;
import cn.t.freedns.threadpool.MonitoredThreadPool;
import cn.t.freedns.threadpool.ThreadPoolConstants;
import cn.t.freedns.threadpool.ThreadPoolMonitor;
import cn.t.freedns.util.MessageCodecUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * MessageHandler
 *
 * @author <a href="mailto:spider-warrior@liby.ltd">研发部-蜘蛛大侠</a>
 * @version V1.0
 * @since 2021-12-17 15:14
 **/
public class MessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    private final ThreadGroup ioIntensiveThreadGroup = new ThreadGroup(ThreadPoolConstants.IO_INTENSIVE_THREAD_GROUP);
    private final ThreadPoolExecutor ioIntensiveThreadPoolExecutor = new MonitoredThreadPool(
            ThreadPoolConstants.IO_CORE_POOL_SIZE,
            ThreadPoolConstants.IO_CORE_POOL_SIZE,
            60,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(ThreadPoolConstants.IO_MAX_POOL_SIZE),
            new MonitoredThreadFactory(ThreadPoolConstants.IO_INTENSIVE_THREAD_GROUP, ioIntensiveThreadGroup),
            ThreadPoolConstants.IO_INTENSIVE_THREAD_GROUP
            );

    private final Set<String> uniqueRequestIdSet = new ConcurrentSkipListSet<>();
    private final RequestHandler requestHandler = new RequestHandler();

    public void handle(Request request, MessageContext context, RequestProcessTracer requestProcessTracer) {
        String uniqueRequestId = uniqueRequestId(context.getRemoteInetAddress(), context.getRemotePort(), request.getHead().getTransID());
        boolean success = uniqueRequestIdSet.add(uniqueRequestId);
        if(success) {
            try {
                ioIntensiveThreadPoolExecutor.submit(() -> {
                    //trace [io thread start time]
                    requestProcessTracer.setIoIntensiveThreadStartTime(System.currentTimeMillis());
                    try {
                        context.write(MessageCodecUtil.encodeResponse(requestHandler.handle(request, context, requestProcessTracer)));
                    } catch (Exception e) {
                        logger.error("响应客户端失败", e);
                    } finally {
                        uniqueRequestIdSet.remove(uniqueRequestId);
                    }
                    //trace [io thread end time]
                    requestProcessTracer.setIoIntensiveThreadEndTime(System.currentTimeMillis());
                    logger.info("请求耗时: {}", requestProcessTracer.debugDuration());
                });
            } catch (Exception e) {
                uniqueRequestIdSet.remove(uniqueRequestId);
                logger.error("io任务提交失败", e);
            }
        } else {
            logger.info("拦截请求, remoteInetAddress: {}, remotePort: {}, transId: {}", context.getRemoteInetAddress(), context.getRemotePort(), request.getHead().getTransID());
        }
    }

    private String uniqueRequestId(InetAddress remoteInetAddress, int remotePort, short transId) {
        return remoteInetAddress.getHostAddress() + ":" + remotePort + "(" + transId + ")";
    }

    public MessageHandler() {
        ThreadPoolMonitor threadPoolMonitor = new ThreadPoolMonitor(ThreadPoolConstants.IO_INTENSIVE_THREAD_GROUP, ioIntensiveThreadPoolExecutor);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                threadPoolMonitor.run();
            }
        }, 3000, 1000);
    }
}