package cn.t.freedns.core;

import java.util.ArrayList;
import java.util.List;

/**
 * MessageLifeStyleTrace
 *
 * @author <a href="mailto:yangjian@liby.ltd">研发部-杨建</a>
 * @version V1.0
 * @since 2021-12-22 10:03
 **/
public class RequestProcessTracer {

    private long traceId;
    private final List<String> domainList = new ArrayList<>();
    private long receiveTime;
    private long cpuIntensiveThreadStartTime;
    private long cpuIntensiveThreadEndTime;
    private long ioIntensiveThreadStartTime;
    private long ioIntensiveThreadEndTime;
    private long ioIntensiveThreadLocalConfigSearchStartTime;
    private long ioIntensiveThreadLocalConfigSearchEndTime;
    private long ioIntensiveThreadLocalNodeSearchStartTime;
    private long ioIntensiveThreadLocalNodeSearchEndTime;
    private long ioIntensiveThreadThirtyPartySearchStartTime;
    private long ioIntensiveThreadThirtyPartySearchEndTime;


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

    public long getIoIntensiveThreadLocalConfigSearchStartTime() {
        return ioIntensiveThreadLocalConfigSearchStartTime;
    }

    public void setIoIntensiveThreadLocalConfigSearchStartTime(long ioIntensiveThreadLocalConfigSearchStartTime) {
        this.ioIntensiveThreadLocalConfigSearchStartTime = ioIntensiveThreadLocalConfigSearchStartTime;
    }

    public long getIoIntensiveThreadLocalConfigSearchEndTime() {
        return ioIntensiveThreadLocalConfigSearchEndTime;
    }

    public void setIoIntensiveThreadLocalConfigSearchEndTime(long ioIntensiveThreadLocalConfigSearchEndTime) {
        this.ioIntensiveThreadLocalConfigSearchEndTime = ioIntensiveThreadLocalConfigSearchEndTime;
    }

    public long getIoIntensiveThreadLocalNodeSearchStartTime() {
        return ioIntensiveThreadLocalNodeSearchStartTime;
    }

    public void setIoIntensiveThreadLocalNodeSearchStartTime(long ioIntensiveThreadLocalNodeSearchStartTime) {
        this.ioIntensiveThreadLocalNodeSearchStartTime = ioIntensiveThreadLocalNodeSearchStartTime;
    }

    public long getIoIntensiveThreadLocalNodeSearchEndTime() {
        return ioIntensiveThreadLocalNodeSearchEndTime;
    }

    public void setIoIntensiveThreadLocalNodeSearchEndTime(long ioIntensiveThreadLocalNodeSearchEndTime) {
        this.ioIntensiveThreadLocalNodeSearchEndTime = ioIntensiveThreadLocalNodeSearchEndTime;
    }

    public long getIoIntensiveThreadThirtyPartySearchStartTime() {
        return ioIntensiveThreadThirtyPartySearchStartTime;
    }

    public void setIoIntensiveThreadThirtyPartySearchStartTime(long ioIntensiveThreadThirtyPartySearchStartTime) {
        this.ioIntensiveThreadThirtyPartySearchStartTime = ioIntensiveThreadThirtyPartySearchStartTime;
    }

    public long getIoIntensiveThreadThirtyPartySearchEndTime() {
        return ioIntensiveThreadThirtyPartySearchEndTime;
    }

    public void setIoIntensiveThreadThirtyPartySearchEndTime(long ioIntensiveThreadThirtyPartySearchEndTime) {
        this.ioIntensiveThreadThirtyPartySearchEndTime = ioIntensiveThreadThirtyPartySearchEndTime;
    }

    public String debugDuration() {
        return "traceId: " + traceId + ", domainList: " + domainList
                + "\r\nreceiveTime                 ->   cpuIntensiveThreadStartTime: " + (cpuIntensiveThreadStartTime - receiveTime)
                + "\r\ncpuIntensiveThreadStartTime ->   cpuIntensiveThreadEndTime  : " + (cpuIntensiveThreadEndTime - cpuIntensiveThreadStartTime)
                + "\r\ncpuIntensiveThreadEndTime   ->   ioIntensiveThreadStartTime : " + (ioIntensiveThreadStartTime - cpuIntensiveThreadEndTime)
                + "\r\nioIntensiveThreadStartTime  ->   ioIntensiveThreadEndTime   : " + (ioIntensiveThreadEndTime - ioIntensiveThreadStartTime)
                + "\r\n    -|ioIntensiveThreadLocalConfigSearchStartTime  ->    ioIntensiveThreadLocalConfigSearchEndTime: " + (ioIntensiveThreadLocalConfigSearchEndTime - ioIntensiveThreadLocalConfigSearchStartTime)
                + "\r\n    -|ioIntensiveThreadLocalNodeSearchStartTime    ->    ioIntensiveThreadLocalNodeSearchEndTime  : " + (ioIntensiveThreadLocalNodeSearchEndTime - ioIntensiveThreadLocalNodeSearchStartTime)
                + "\r\n    -|ioIntensiveThreadThirtyPartySearchStartTime  ->    ioIntensiveThreadThirtyPartySearchEndTime: " + (ioIntensiveThreadThirtyPartySearchEndTime - ioIntensiveThreadThirtyPartySearchStartTime);
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
                ", ioIntensiveThreadLocalConfigSearchStartTime=" + ioIntensiveThreadLocalConfigSearchStartTime +
                ", ioIntensiveThreadLocalConfigSearchEndTime=" + ioIntensiveThreadLocalConfigSearchEndTime +
                ", ioIntensiveThreadLocalNodeSearchStartTime=" + ioIntensiveThreadLocalNodeSearchStartTime +
                ", ioIntensiveThreadLocalNodeSearchEndTime=" + ioIntensiveThreadLocalNodeSearchEndTime +
                ", ioIntensiveThreadThirtyPartySearchStartTime=" + ioIntensiveThreadThirtyPartySearchStartTime +
                ", ioIntensiveThreadThirtyPartySearchEndTime=" + ioIntensiveThreadThirtyPartySearchEndTime +
                '}';
    }
}