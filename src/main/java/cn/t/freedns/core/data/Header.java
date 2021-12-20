package cn.t.freedns.core.data;

/**
 * @author yj
 * @since 2019-12-31 20:56
 *
 * 递归：客户端只发一次请求，要求对方给出最终结果。
 * 迭代：客户端发出一次请求，对方如果没有授权回答，它就会返回一个能解答这个查询的其它名称服务器列表，客户端会再向返回的列表中发出请求，直到找到最终负责所查域名的名称服务器，从它得到最终结果。
 * 授权回答：向dns服务器查询一个域名，刚好这个域名是本服务器负责，返回的结果就是授权回答。
 * 从递归和迭代查询可以看出：
 * 客户端-本地dns服务端：这部分属于递归查询。（定义）
 * 本地dns服务端---外网：这部分属于迭代查询。
 * 递归查询时，返回的结果只有两种:查询成功或查询失败.
 * 迭代查询，又称作重指引,返回的是最佳的查询点或者主机地址.
 *
 * flag:
 * QR(1比特）：查询/响应的标志位，1为响应报文，0为查询报文。
 * opcode（4比特）：定义查询或响应的类型（若为0则表示是标准的，若为1则是反向的，若为2则是服务器状态请求）。
 * AA（1比特）：该域名本当前dns服务所负责，是DNS查找链底部的服务器，不需要询问其他dns服务器。
 * TC（1比特）：截断标志位。1表示响应已超过512字节并已被截断。
 * RD（1比特）：这个比特位被请求设置，应答的时候使用的相同的值返回。如果设置了RD，就建议域名服务器进行递归解析，递归查询的支持是可选的。
 * RA（1比特）：这个比特位在应答中设置或取消，用来代表服务器是否支持递归查询。
 * zero（3比特）：保留字段。
 * rcode（4比特）：返回码，表示响应的差错状态，通常为0和3。
 * 0 成功的响应
 * 1 格式错误--域名服务器无法解析请求，因为请求消息格式错误
 * 2 服务器错误--域名服务器因为内部错误无法解析该请求
 * 3 名字错误-- 只在权威域名服务器的响应消息中有效，标示请求中请求的域不存在
 * 4 域名服务器不支持请求的类型
 * 5 域名服务器因为策略的原因拒绝执行请求的操作。例如域名服务器不会为特定的请求者返回查询结果，或者域名服务器不会为特定的请求返回特定的数据
 *
 * ===============================================================================================================================
 *
 * TC（截断标志位）
 * 如何突破DNS报文的512字节限制
 * @link https://blog.csdn.net/yeyiqun/article/details/99310372
 * 根据协议标准文档RFC1035，当封装的DNS响应的长度超过512字节时，协议应采用TCP传输，而不是UDP。（这份RFC文档产生于三十年前）
 * RFC 6891这份标准文档，对DNS进行了扩展，描述了超过512字节的DNS的情况，即EDNS0。详细情况可参考RFC 6891这份文档。
 *
 * DNS协议从UDP切换到TCP的过程如下：
 * 1、客户端向服务器发起UDP DNS请求；
 * 2、如果服务器发现DNS响应数据超过512字节，则返回UDP DNS响应中置truncated位，告知客户端改用TCP进行重新请求；
 * 3、客户端向服务器发起TCP DNS请求；
 * 4、服务器返回TCP DNS响应。
 **/

public class Header {

    /* 会话标识（2字节）*/
    private short transID;
    /* Flags（2字节）*/
    private short flag;
    /* query domain count（2字节）*/
    private short queryCount;
    /* answer count（2字节）*/
    private short answerCount;
    /* the number of name server resource records in the authority records section（2字节）*/
    private short authoritativeNameServerCount;
    /* the number of resource records in the additional records section（2字节）*/
    private short additionalRecordsCount;

    public short getTransID() {
        return transID;
    }

    public void setTransID(short transID) {
        this.transID = transID;
    }

    public short getFlag() {
        return flag;
    }

    public void setFlag(short flag) {
        this.flag = flag;
    }

    public short getQueryCount() {
        return queryCount;
    }

    public void setQueryCount(short queryCount) {
        this.queryCount = queryCount;
    }

    public short getAnswerCount() {
        return answerCount;
    }

    public void setAnswerCount(short answerCount) {
        this.answerCount = answerCount;
    }

    public short getAuthoritativeNameServerCount() {
        return authoritativeNameServerCount;
    }

    public void setAuthoritativeNameServerCount(short authoritativeNameServerCount) {
        this.authoritativeNameServerCount = authoritativeNameServerCount;
    }

    public short getAdditionalRecordsCount() {
        return additionalRecordsCount;
    }

    public void setAdditionalRecordsCount(short additionalRecordsCount) {
        this.additionalRecordsCount = additionalRecordsCount;
    }

    @Override
    public String toString() {
        return "Header{" +
                "transID=" + transID +
                ", flag=" + flag +
                ", queryCount=" + queryCount +
                ", answerCount=" + answerCount +
                ", authoritativeNameServerCount=" + authoritativeNameServerCount +
                ", additionalRecordsCount=" + additionalRecordsCount +
                '}';
    }
}
