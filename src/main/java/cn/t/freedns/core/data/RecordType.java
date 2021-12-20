package cn.t.freedns.core.data;

public enum RecordType {
    A((short)1, "A", "由域名获得IPv4地址"),
    NS((short)2, "NS", "查询域名服务器"),
    CNAM((short)5, "CNAM", "查询规范名称"),
    SOA((short)6, "SOA", "开始授权"),
    WKS((short)11, "WKS", "熟知服务"),
    PTR((short)12, "PTR", "把IP地址转换成域名"),
    HINF((short)13, "HINF", "主机信息"),
    MX((short)15, "MX", "邮件交换"),
    AAAA((short)28, "AAAA", "由域名获得IPv6地址"),
    AXFR((short)252, "AXFR", "传送整个区的请求"),
    ANY((short)255, "ANY", "对所有记录的请求")
    ;
    public final short value;
    public final String shortName;
    public final String desc;

    RecordType(short value, String shortName, String desc) {
        this.value = value;
        this.shortName = shortName;
        this.desc = desc;
    }

    public static RecordType getRecordType(short value) {
        for(RecordType recordType : values()) {
            if(recordType.value == value) {
                return recordType;
            }
        }
        return null;
    }
}
