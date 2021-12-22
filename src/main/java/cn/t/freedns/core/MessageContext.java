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
    private final MessageLifeStyleTrace messageLifeStyleTrace = new MessageLifeStyleTrace();

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

    public MessageLifeStyleTrace getMessageLifeStyleTrace() {
        return messageLifeStyleTrace;
    }

    /* 静态代理方法 */
    public void setTraceId(long traceId) {
        this.messageLifeStyleTrace.setTraceId(traceId);
    }

    public void addDomain(String domain) {
        this.messageLifeStyleTrace.addDomain(domain);
    }

    public void setReceiveTime(long receiveTime) {
        this.messageLifeStyleTrace.setReceiveTime(receiveTime);
    }

    public void setCpuIntensiveThreadStartTime(long cpuIntensiveThreadStartTime) {
        this.messageLifeStyleTrace.setCpuIntensiveThreadStartTime(cpuIntensiveThreadStartTime);
    }

    public void setCpuIntensiveThreadEndTime(long cpuIntensiveThreadEndTime) {
        this.messageLifeStyleTrace.setCpuIntensiveThreadEndTime(cpuIntensiveThreadEndTime);
    }

    public void setIoIntensiveThreadStartTime(long ioIntensiveThreadStartTime) {
        this.messageLifeStyleTrace.setIoIntensiveThreadStartTime(ioIntensiveThreadStartTime);
    }

    public void setIoIntensiveThreadEndTime(long iouIntensiveThreadEndTime) {
        this.messageLifeStyleTrace.setIoIntensiveThreadEndTime(iouIntensiveThreadEndTime);
    }
    /* ************************************************************************************* */
}