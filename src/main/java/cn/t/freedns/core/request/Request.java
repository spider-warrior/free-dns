package cn.t.freedns.core.request;


import cn.t.freedns.core.data.Header;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author yj
 * @since 2020-01-01 10:45
 **/
public class Request {

    //head
    private Header header;
    //query list
    private List<Query> queryList;

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public List<Query> getQueryList() {
        return queryList;
    }

    public void setQueryList(List<Query> queryList) {
        this.queryList = queryList;
    }

    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        //1.transaction id
        buffer.putShort(header.getTransID());
        //2.flag
        buffer.putShort(header.getFlag());
        //3.question count(固定写1)
        buffer.putShort((short) 1);
        //4.answer count
        buffer.putShort((short) 0);
        //5.authoritative count(固定写0)
        buffer.putShort((short)0);
        //6.additional count(固定写0)
        buffer.putShort((short)0);
        if(queryList != null) {
            for (Query query : queryList) {
                String domain = query.getDomain();
                //1.query domain
                String[] elements = domain.split("\\.");
                for(String ele: elements) {
                    //长度
                    buffer.put((byte)ele.length());
                    //value
                    buffer.put(ele.getBytes());
                }
                //结束补0
                buffer.put((byte)0);
                //2.type
                buffer.putShort(query.getType());
                //3.class
                buffer.putShort(query.getClazz());
            }
        }
        buffer.flip();
        int len = buffer.limit() - buffer.position();
        byte[] bytes = new byte[len];
        buffer.get(bytes);
        return bytes;
    }

    @Override
    public String toString() {
        return "Request{" +
                "header=" + header +
                ", queryList=" + queryList +
                '}';
    }
}
