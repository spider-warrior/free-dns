package cn.t.freedns.threadpool;

/**
 * ThreadPoolConstants
 *
 * @author <a href="mailto:yangjian@liby.ltd">研发部-杨建</a>
 * @version V1.0
 * @since 2021-12-21 19:51
 **/
public class ThreadPoolConstants {

    public static final String CPU_INTENSIVE_THREAD_GROUP = "CPU密集型线程组";
    public static final String IO_INTENSIVE_THREAD_GROUP = "IO密集型线程组";

    public static final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors();
    public static final int IO_CORE_POOL_SIZE = CORE_POOL_SIZE * 2;
    public static final int IO_MAX_POOL_SIZE = IO_CORE_POOL_SIZE * 2;

    public static final String THREAD_MONITOR_LOG_NAME = "ThreadMonitor";
    public static final String THREAD_POOL_MONITOR_LOG_NAME = "ThreadPoolMonitor";

}