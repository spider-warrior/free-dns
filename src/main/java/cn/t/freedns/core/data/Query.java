package cn.t.freedns.core.data;

/**
 * @author <a href="mailto:spider-warrior@liby.ltd">研发部-蜘蛛大侠</a>
 * @version V1.0
 * @since 2021-12-20 15:46
 **/
public class Query {
    //offset
    private int offset;
    //域名
    private String domain;
    //类型
    private short type;
    //分类
    private short clazz;

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public short getType() {
        return type;
    }

    public void setType(short type) {
        this.type = type;
    }

    public short getClazz() {
        return clazz;
    }

    public void setClazz(short clazz) {
        this.clazz = clazz;
    }

    @Override
    public String toString() {
        return "Query{" +
                "offset=" + offset +
                ", domain='" + domain + '\'' +
                ", type=" + type +
                ", clazz=" + clazz +
                '}';
    }
}