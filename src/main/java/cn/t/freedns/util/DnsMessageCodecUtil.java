package cn.t.freedns.util;


import cn.t.freedns.ForbidServiceException;
import cn.t.freedns.core.data.Header;
import cn.t.freedns.core.data.Record;
import cn.t.freedns.core.request.Query;
import cn.t.freedns.core.request.Request;
import cn.t.freedns.core.response.Response;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * @author yj
 * @since 2020-01-01 10:33
 **/
public class DnsMessageCodecUtil {

    public static Request decodeRequest(byte[] messageBytes) {
        ByteBuffer messageBuffer = ByteBuffer.wrap(messageBytes);
        Header header = decoderHeader(messageBuffer);
        //request检查
        requestCheck(header.getFlag());
        Request request = new Request();
        request.setHeader(header);
        List<Query> queryList = decodeQueries(messageBuffer, header.getQueryCount());
        request.setQueryList(queryList);
        return request;
    }

    public static Response decodeResponse(byte[] messageBytes) {
        ByteBuffer messageBuffer = ByteBuffer.wrap(messageBytes);
        Header header = decoderHeader(messageBuffer);
        Response response = new Response();
        response.setHeader(header);
        List<Query> queryList = decodeQueries(messageBuffer, header.getQueryCount());
        response.setQueryList(queryList);
        List<Record> recordList = decodeRecord(messageBuffer, header.getAnswerCount(), queryList);
        response.setRecordList(recordList);
        return response;
    }

    public static byte[] encodeRequest(Request request) {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        Header header = request.getHeader();
        List<Query> queryList = request.getQueryList();
        //1.transaction id
        buffer.putShort(header.getTransID());
        //2.flag
        buffer.putShort(header.getFlag());
        //3.question count
        buffer.putShort((short)queryList.size());
        //4.answer RRs
        buffer.putShort((short) 0);
        //5.authority RRs
        buffer.putShort((short)0);
        //6.additional RRs
        buffer.putShort((short)0);
        //7.query list
        encodeQueryList(queryList, buffer);
        buffer.flip();
        int len = buffer.limit() - buffer.position();
        byte[] bytes = new byte[len];
        buffer.get(bytes);
        return bytes;
    }

    public static byte[] encodeResponse(Response response) {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        Header header = response.getHeader();
        //1.transaction id
        buffer.putShort(header.getTransID());
        //2.flag
        buffer.putShort(header.getFlag());
        //3.query count
        buffer.putShort((short) header.getQueryCount());
        //4.answer count
        List<Record> recordList = response.getRecordList();
        buffer.putShort((short)((recordList== null || recordList.size() == 0) ? 0 : recordList.size()));
        //authority server count
        buffer.putShort(header.getAuthoritativeNameServerCount());
        //additional related RRs(Resource Record)
        buffer.putShort(header.getAdditionalRecordsCount());
        //query list
        encodeQueryList(response.getQueryList(), buffer);
        //record list
        encodeRecordList(recordList, response.getQueryList(), buffer);
        buffer.flip();
        int len = buffer.limit() - buffer.position();
        byte[] bytes = new byte[len];
        buffer.get(bytes);
        return bytes;
    }

    //解析头部
    private static Header decoderHeader(ByteBuffer messageBuffer) {
        //报文Id
        short id = messageBuffer.getShort();
        //报文标志
        short flag = messageBuffer.getShort();
        //查询问题区域的数量
        short queryCount = messageBuffer.getShort();
        //回答区域的数量
        short answerCount = messageBuffer.getShort();
        //授权区域的数量
        short authoritativeNameServerCount = messageBuffer.getShort();
        //附加区域的数量
        short additionalRecordsCount = messageBuffer.getShort();
        //header
        Header header = new Header();
        header.setTransID(id);
        header.setFlag(flag);
        header.setQueryCount(queryCount);
        header.setAnswerCount(answerCount);
        header.setAuthoritativeNameServerCount(authoritativeNameServerCount);
        header.setAdditionalRecordsCount(additionalRecordsCount);
        return header;
    }

    //解析请求
    private static List<Query> decodeQueries(ByteBuffer messageBuffer, short queryCount) {
        if(queryCount < 0) {
            throw new IllegalArgumentException("queryCount小于0");
        } else if(queryCount == 0) {
            return Collections.emptyList();
        } else {
            List<Query> queryList = new ArrayList<>(queryCount);
            while (queryCount-- > 0) {
                int position = messageBuffer.position();
                String domain = decodeDomain(messageBuffer);
                short type = messageBuffer.getShort();
                short clazz = messageBuffer.getShort();
                Query query = new Query();
                query.setOffset(position);
                query.setDomain(domain);
                query.setType(type);
                query.setClazz(clazz);
                queryList.add(query);
            }
            return queryList;
        }
    }

    private static void encodeRecordList(List<Record> recordList, List<Query> queryList, ByteBuffer buffer) {
        if(recordList != null && recordList.size() > 0) {
            Map<String, Query> domainOffsetMap = queryList.stream().collect(HashMap::new, (hashMap, query) -> hashMap.put(query.getDomain(), query), Map::putAll);
            for (Record record : recordList) {
                encodeRecord(record, domainOffsetMap.get(record.getDomain()), buffer);
            }
        }
    }

    private static void encodeRecord(Record record, Query query, ByteBuffer buffer) {
        //pointer marker
        buffer.put((byte)0xC0);
        //offset
        buffer.put((byte)query.getOffset());
        //type
        buffer.putShort(record.getRecordType());
        //class
        buffer.putShort(record.getRecordClass());
        //ttl
        buffer.putInt(record.getTtl());
        //length
        buffer.putShort((short)record.getData().length);
        //data
        buffer.put(record.getData());
    }

    private static void encodeQueryList(List<Query> queryList, ByteBuffer buffer) {
        for (Query query : queryList) {
            encodeQuery(query, buffer);
        }
    }

    private static void encodeQuery(Query query, ByteBuffer buffer) {
        String domain = query.getDomain();
        String[] elements = domain.split("\\.");
        for(String ele: elements) {
            //长度
            buffer.put((byte)ele.length());
            //value
            buffer.put(ele.getBytes());
        }
        buffer.put((byte)0);
        //type
        buffer.putShort(query.getType());
        //class
        buffer.putShort(query.getClazz());
    }

    private static List<Record> decodeRecord(ByteBuffer messageBuffer, int answerCount, List<Query> queryList) {
        if(answerCount < 0) {
            throw new IllegalArgumentException("answerCount小于0");
        } else if(answerCount == 0) {
            return Collections.emptyList();
        } else {
            Map<Integer, String> domainOffsetMap = queryList.stream().collect(HashMap::new, (hashMap, query) -> hashMap.put(query.getOffset(), query.getDomain()), Map::putAll);
            List<Record> recordList = new ArrayList<>();
            while (answerCount-- > 0) {
                String domain;
                //因为域名字符的限制(最大为63)所以byte字节的高两位始终为00，所以使用高两位使用11表示使用偏移量来表示对应的域名,10和01两种状态被保留
                //前面内容都是定长，所以偏移量一定是从12开始算起
                byte length = messageBuffer.get();
                if(length == (byte)0xC0) {
                    int offset = messageBuffer.get();
                    domain = domainOffsetMap.get(offset);
                } else {
                    domain = decodeDomain(messageBuffer);
                }
                Record record = new Record();
                record.setDomain(domain);
                record.setRecordType(messageBuffer.getShort());
                record.setRecordClass(messageBuffer.getShort());
                record.setTtl(messageBuffer.getInt());
                short dataLength = messageBuffer.getShort();
                byte[] data = new byte[dataLength];
                messageBuffer.get(data);
                record.setData(data);
                recordList.add(record);
            }
            return recordList;
        }
    }

    private static String decodeString(ByteBuffer messageBuffer, int length) {
        byte[] partDomain = new byte[length];
        messageBuffer.get(partDomain);
        return new String(partDomain);
    }

    private static String decodeDomain(ByteBuffer messageBuffer) {
        byte length;
        StringBuilder builder = new StringBuilder();
        while ((length = messageBuffer.get()) > 0) {
            byte[] partDomain = new byte[length];
            messageBuffer.get(partDomain);
            builder.append(new String(partDomain)).append(".");
        }
        if(builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }

    private static void requestCheck(short flag) {
        if(!FlagUtil.isQuery(flag)) {
            throw new ForbidServiceException("不是查询");
        }
        if(!FlagUtil.isStandardQuery(flag)) {
            throw new ForbidServiceException("不是正向查询");
        }
        if(FlagUtil.isQueryTruncated(flag)) {
            throw new ForbidServiceException("截断请求");
        }
    }

    private static void responseCheck(short flag) {
        if(!FlagUtil.isResponse(flag)) {
            throw new ForbidServiceException("不是响应");
        }
        if(!FlagUtil.isAuthoritativeServer(flag)) {
            throw new ForbidServiceException("不是权威主机");
        }
        if(FlagUtil.isQueryTruncated(flag)) {
            throw new ForbidServiceException("截断响应");
        }
        if(!FlagUtil.isRecursionDesired(flag)) {
            throw new ForbidServiceException("不是递归查询");
        }
        if(!FlagUtil.isRecursionAvailable(flag)) {
            throw new ForbidServiceException("递归查询不可用");
        }
        if(!FlagUtil.isAuthoritativeAnswer(flag)) {
            throw new ForbidServiceException("不是权威应答");
        }
    }
}
