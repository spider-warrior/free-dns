package cn.t.freedns.core;

import cn.t.freedns.core.data.Request;
import cn.t.freedns.util.MessageCodecUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * MessageHandler
 *
 * @author <a href="mailto:spider-warrior@liby.ltd">研发部-蜘蛛大侠</a>
 * @version V1.0
 * @since 2021-12-17 15:14
 **/
public class MessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final ThreadGroup threadGroup = new ThreadGroup("dns消息处理线程组");
    private final ThreadPoolExecutor handleRequestThreadPoolExecutor = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors(),
            60,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(20),
            runnable -> new Thread(threadGroup, runnable, "dns-request-handle" + threadNumber.getAndIncrement(), 0)
            );

    private final RequestHandler requestHandler = new RequestHandler();
    public void handle(byte[] msg, MessageContext context) {
        Request request = MessageCodecUtil.decodeRequest(msg);
        handleRequestThreadPoolExecutor.submit(() -> requestHandler.handle(context, request));
    }

}