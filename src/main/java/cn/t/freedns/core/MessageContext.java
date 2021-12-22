package cn.t.freedns.core;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * @author <a href="mailto:spider-warrior@liby.ltd">研发部-蜘蛛大侠</a>
 * @version V1.0
 * @since 2021-12-17 15:28
 **/
public class MessageContext {
    private final RequestProcessTracer requestProcessTracer = new RequestProcessTracer();

    private DatagramSocket serverSocket;
    private InetAddress remoteInetAddress;
    private int remotePort;

    public void write(byte[] data) throws IOException {
        if(data != null && data.length > 0) {
            serverSocket.send(new DatagramPacket(data, data.length, remoteInetAddress, remotePort));
        }
    }

    public DatagramSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(DatagramSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public InetAddress getRemoteInetAddress() {
        return remoteInetAddress;
    }

    public void setRemoteInetAddress(InetAddress remoteInetAddress) {
        this.remoteInetAddress = remoteInetAddress;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    public RequestProcessTracer getRequestProcessTracer() {
        return requestProcessTracer;
    }

    /* 静态代理方法 */
    public void setTraceId(long traceId) {
        this.requestProcessTracer.setTraceId(traceId);
    }

    public void addDomain(String domain) {
        this.requestProcessTracer.addDomain(domain);
    }

    public void setReceiveTime(long receiveTime) {
        this.requestProcessTracer.setReceiveTime(receiveTime);
    }

    public void setCpuIntensiveThreadStartTime(long cpuIntensiveThreadStartTime) {
        this.requestProcessTracer.setCpuIntensiveThreadStartTime(cpuIntensiveThreadStartTime);
    }

    public void setCpuIntensiveThreadEndTime(long cpuIntensiveThreadEndTime) {
        this.requestProcessTracer.setCpuIntensiveThreadEndTime(cpuIntensiveThreadEndTime);
    }

    public void setIoIntensiveThreadStartTime(long ioIntensiveThreadStartTime) {
        this.requestProcessTracer.setIoIntensiveThreadStartTime(ioIntensiveThreadStartTime);
    }

    public void setIoIntensiveThreadEndTime(long iouIntensiveThreadEndTime) {
        this.requestProcessTracer.setIoIntensiveThreadEndTime(iouIntensiveThreadEndTime);
    }

    public void setIoIntensiveThreadLocalConfigSearchStartTime(long ioIntensiveThreadLocalConfigSearchStartTime) {
        this.requestProcessTracer.setIoIntensiveThreadLocalConfigSearchStartTime(ioIntensiveThreadLocalConfigSearchStartTime);
    }

    public void setIoIntensiveThreadLocalConfigSearchEndTime(long ioIntensiveThreadLocalConfigSearchEndTime) {
        this.requestProcessTracer.setIoIntensiveThreadLocalConfigSearchEndTime(ioIntensiveThreadLocalConfigSearchEndTime);
    }

    public void setIoIntensiveThreadLocalNodeSearchStartTime(long ioIntensiveThreadLocalNodeSearchStartTime) {
        this.requestProcessTracer.setIoIntensiveThreadLocalNodeSearchStartTime(ioIntensiveThreadLocalNodeSearchStartTime);
    }

    public void setIoIntensiveThreadLocalNodeSearchEndTime(long ioIntensiveThreadLocalNodeSearchEndTime) {
        this.requestProcessTracer.setIoIntensiveThreadLocalNodeSearchEndTime(ioIntensiveThreadLocalNodeSearchEndTime);
    }

    public void setIoIntensiveThreadThirtyPartySearchStartTime(long ioIntensiveThreadThirtyPartySearchStartTime) {
        this.requestProcessTracer.setIoIntensiveThreadThirtyPartySearchStartTime(ioIntensiveThreadThirtyPartySearchStartTime);
    }

    public void setIoIntensiveThreadThirtyPartySearchEndTime(long ioIntensiveThreadThirtyPartySearchEndTime) {
        this.requestProcessTracer.setIoIntensiveThreadThirtyPartySearchEndTime(ioIntensiveThreadThirtyPartySearchEndTime);
    }
    /* ************************************************************************************* */
}