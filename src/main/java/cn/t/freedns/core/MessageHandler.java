package cn.t.freedns.core;

import cn.t.freedns.core.data.Request;
import cn.t.freedns.threadpool.MonitoredThreadFactory;
import cn.t.freedns.threadpool.MonitoredThreadPool;
import cn.t.freedns.threadpool.ThreadPoolConstants;
import cn.t.freedns.threadpool.ThreadPoolMonitor;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
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

    private final RequestHandler requestHandler = new RequestHandler();
    public void handle(Request request, MessageContext context, RequestProcessTracer requestProcessTracer) {
        ioIntensiveThreadPoolExecutor.submit(() -> requestHandler.handle(request, context, requestProcessTracer));
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