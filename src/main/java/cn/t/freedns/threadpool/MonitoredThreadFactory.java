package cn.t.freedns.threadpool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 创建被监控的线程
 *
 * 生成线程池所用的线程，只是改写了线程池默认的线程工厂，传入线程池名称，便于问题追踪
 * @author <a href="mailto:jian.yang@liby.ltd">野生程序员-杨建</a>
 * @version V1.0
 * @since 2020-02-24 22:24
 **/
public class MonitoredThreadFactory implements ThreadFactory {
    private static final Map<String, AtomicInteger> poolNameCounterMap = new ConcurrentHashMap<>();
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final String namePrefix;

    /**
     * 初始化线程工厂
     *
     * @param poolName 线程池名称
     */
    public MonitoredThreadFactory(String poolName, ThreadGroup group) {
        this.group = group;
        AtomicInteger poolCounter = poolNameCounterMap.computeIfAbsent(poolName, k -> new AtomicInteger(1));
        namePrefix = poolName + "-" + poolCounter.getAndIncrement() + "-";
    }

    @Override
    public Thread newThread(Runnable runnable) {
        Thread t = new Thread(group, runnable, namePrefix + threadNumber.getAndIncrement(), 0);
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }
}
