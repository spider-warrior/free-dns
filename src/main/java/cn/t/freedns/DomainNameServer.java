package cn.t.freedns;

import cn.t.freedns.core.MessageContext;
import cn.t.freedns.core.MessageHandler;
import cn.t.freedns.core.RequestProcessTracer;
import cn.t.freedns.core.data.Request;
import cn.t.freedns.threadpool.MonitoredThreadFactory;
import cn.t.freedns.threadpool.MonitoredThreadPool;
import cn.t.freedns.threadpool.ThreadPoolConstants;
import cn.t.freedns.threadpool.ThreadPoolMonitor;
import cn.t.freedns.util.MessageCodecUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:spider-warrior@liby.ltd">研发部-蜘蛛大侠</a>
 * @version V1.0
 * @since 2021-12-17 14:51
 **/
public class DomainNameServer {

    private static final ThreadGroup cpuIntensiveThreadGroup = new ThreadGroup(ThreadPoolConstants.CPU_INTENSIVE_THREAD_GROUP);
    private static final ThreadPoolExecutor cpuIntensiveThreadPoolExecutor = new MonitoredThreadPool(
            ThreadPoolConstants.CORE_POOL_SIZE,
            ThreadPoolConstants.CORE_POOL_SIZE,
            Long.MAX_VALUE,
            TimeUnit.NANOSECONDS,
            new ArrayBlockingQueue<>(20),
            new MonitoredThreadFactory(ThreadPoolConstants.CPU_INTENSIVE_THREAD_GROUP, cpuIntensiveThreadGroup),
            ThreadPoolConstants.CPU_INTENSIVE_THREAD_GROUP
    );

    private static final MessageHandler messageHandler = new MessageHandler();

    public static void main(String[] args) throws IOException {
        startThreadPoolMonitor();
        loadSpecificDnsProperty();
        final byte[] buffer = new byte[1024];
        DatagramSocket socket = new DatagramSocket(53);
        while (true) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            MessageContext messageContext = new MessageContext();
            RequestProcessTracer requestProcessTracer = new RequestProcessTracer();
            // trace [id]
            requestProcessTracer.setTraceId(new Random().nextLong());
            socket.receive(packet);
            //trace [receive time]
            requestProcessTracer.setReceiveTime(System.currentTimeMillis());
            byte[] messageBytes = new byte[packet.getLength()];
            System.arraycopy(packet.getData(), 0, messageBytes, 0, packet.getLength());
            cpuIntensiveThreadPoolExecutor.submit(() -> {
                //trace [cpu thread start time]
                requestProcessTracer.setCpuIntensiveThreadStartTime(System.currentTimeMillis());
                messageContext.setServerSocket(socket);
                messageContext.setRemoteInetAddress(packet.getAddress());
                messageContext.setRemotePort(packet.getPort());
                Request request = MessageCodecUtil.decodeRequest(messageBytes);
                messageHandler.handle(request, messageContext, requestProcessTracer);
                //trace [cpu thread end time]
                requestProcessTracer.setCpuIntensiveThreadEndTime(System.currentTimeMillis());
            });
        }
    }
    private static void loadSpecificDnsProperty() {
        //sun.net.spi.nameservice.provider.<n>=<default|dns,sun|...> 用于设置域名服务提供者
        //default的时候调用系统自带的DNS
        //dns,sun的时候，会调用sun.net.spi.nameservice.nameservers=<server1_ipaddr,server2_ipaddr ...>指定的DNS来解析
        System.setProperty("sun.net.spi.nameservice.provider.1", "dns,sun");
        System.setProperty("sun.net.spi.nameservice.nameservers", "192.168.1.1");
    }

    private static void startThreadPoolMonitor() {
        ThreadPoolMonitor threadPoolMonitor = new ThreadPoolMonitor(ThreadPoolConstants.CPU_INTENSIVE_THREAD_GROUP, cpuIntensiveThreadPoolExecutor);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                threadPoolMonitor.run();
            }
        }, 3000, 1000);
    }
}