package cn.t.freedns.core.data;

import java.util.Arrays;

/**
 * @author yj
 * @since 2020-01-01 15:05
 **/
public class Record {

    /**
     * 域名
     * */
    private String domain;

    /**
     * 记录类型
     * */
    private short recordType;

    /**
     * 记录class
     * */
    private short recordClass;

    /**
     * time to live(秒)
     * */
    private int ttl;

    /**
     * 值
     * */
    private byte[] data;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public short getRecordType() {
        return recordType;
    }

    public void setRecordType(short recordType) {
        this.recordType = recordType;
    }

    public short getRecordClass() {
        return recordClass;
    }

    public void setRecordClass(short recordClass) {
        this.recordClass = recordClass;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Record{" +
                "domain='" + domain + '\'' +
                ", recordType=" + recordType +
                ", recordClass=" + recordClass +
                ", ttl=" + ttl +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
