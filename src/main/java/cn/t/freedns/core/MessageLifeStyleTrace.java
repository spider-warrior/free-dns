package cn.t.freedns.core;

import java.util.ArrayList;
import java.util.List;

/**
 * MessageLifeStyleTrace
 *
 * @author <a href="mailto:yangjian@ifenxi.com">研发部-杨建</a>
 * @version V1.0
 * @since 2021-12-22 10:03
 **/
public class MessageLifeStyleTrace {

    private long traceId;
    private final List<String> domainList = new ArrayList<>();
    private long receiveTime;
    private long cpuIntensiveThreadStartTime;
    private long cpuIntensiveThreadEndTime;
    private long ioIntensiveThreadStartTime;
    private long ioIntensiveThreadEndTime;

    public long getTraceId() {
        return traceId;
    }

    public void setTraceId(long traceId) {
        this.traceId = traceId;
    }

    public void addDomain(String domain) {
        this.domainList.add(domain);
    }

    public List<String> getDomainList() {
        return domainList;
    }

    public long getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(long receiveTime) {
        this.receiveTime = receiveTime;
    }

    public long getCpuIntensiveThreadStartTime() {
        return cpuIntensiveThreadStartTime;
    }

    public void setCpuIntensiveThreadStartTime(long cpuIntensiveThreadStartTime) {
        this.cpuIntensiveThreadStartTime = cpuIntensiveThreadStartTime;
    }

    public long getCpuIntensiveThreadEndTime() {
        return cpuIntensiveThreadEndTime;
    }

    public void setCpuIntensiveThreadEndTime(long cpuIntensiveThreadEndTime) {
        this.cpuIntensiveThreadEndTime = cpuIntensiveThreadEndTime;
    }

    public long getIoIntensiveThreadStartTime() {
        return ioIntensiveThreadStartTime;
    }

    public void setIoIntensiveThreadStartTime(long ioIntensiveThreadStartTime) {
        this.ioIntensiveThreadStartTime = ioIntensiveThreadStartTime;
    }

    public long getIoIntensiveThreadEndTime() {
        return ioIntensiveThreadEndTime;
    }

    public void setIoIntensiveThreadEndTime(long ioIntensiveThreadEndTime) {
        this.ioIntensiveThreadEndTime = ioIntensiveThreadEndTime;
    }

    public String debugDuration() {
        return "debug job: " + traceId + ", domainList: " + domainList
                + "\r\nreceiveTime                 -> cpuIntensiveThreadStartTime: " + (cpuIntensiveThreadStartTime - receiveTime)
                + "\r\ncpuIntensiveThreadStartTime ->   cpuIntensiveThreadEndTime: " + (cpuIntensiveThreadEndTime - cpuIntensiveThreadStartTime)
                + "\r\ncpuIntensiveThreadEndTime   ->  ioIntensiveThreadStartTime: " + (ioIntensiveThreadStartTime - cpuIntensiveThreadEndTime)
                + "\r\nioIntensiveThreadStartTime  ->    ioIntensiveThreadEndTime: " + (ioIntensiveThreadEndTime - ioIntensiveThreadStartTime);
    }

    @Override
    public String toString() {
        return "MessageLifeStyleTrace{" +
                "traceId=" + traceId +
                ", domainList=" + domainList +
                ", receiveTime=" + receiveTime +
                ", cpuIntensiveThreadStartTime=" + cpuIntensiveThreadStartTime +
                ", cpuIntensiveThreadEndTime=" + cpuIntensiveThreadEndTime +
                ", ioIntensiveThreadStartTime=" + ioIntensiveThreadStartTime +
                ", ioIntensiveThreadEndTime=" + ioIntensiveThreadEndTime +
                '}';
    }
}