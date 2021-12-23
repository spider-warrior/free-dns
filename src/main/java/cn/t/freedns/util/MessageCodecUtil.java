package cn.t.freedns.util;


import cn.t.freedns.ForbidServiceException;
import cn.t.freedns.core.data.Head;
import cn.t.freedns.core.data.Record;
import cn.t.freedns.core.constants.RecordType;
import cn.t.freedns.core.data.Query;
import cn.t.freedns.core.data.Request;
import cn.t.freedns.core.data.Response;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * @author yj
 * @since 2020-01-01 10:33
 **/
public class MessageCodecUtil {

    public static Request decodeRequest(byte[] messageBytes) {
        ByteBuffer messageBuffer = ByteBuffer.wrap(messageBytes);
        Head head = decoderHeader(messageBuffer);
        //request检查
        requestCheck(head.getFlag());
        Request request = new Request();
        request.setHead(head);
        List<Query> queryList = decodeQueries(messageBuffer, head.getQueryCount());
        request.setQueryList(queryList);
        return request;
    }

    public static Response decodeResponse(byte[] messageBytes) {
        ByteBuffer messageBuffer = ByteBuffer.wrap(messageBytes);
        Head head = decoderHeader(messageBuffer);
        Response response = new Response();
        response.setHead(head);
        List<Query> queryList = decodeQueries(messageBuffer, head.getQueryCount());
        response.setQueryList(queryList);
        List<Record> recordList = decodeRecord(messageBuffer, head.getAnswerCount());
        response.setRecordList(recordList);
        return response;
    }

    public static byte[] encodeRequest(Request request) {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        Head head = request.getHead();
        List<Query> queryList = request.getQueryList();
        //1.transaction id
        buffer.putShort(head.getTransID());
        //2.flag
        buffer.putShort(head.getFlag());
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
        Head head = response.getHead();
        //1.transaction id
        buffer.putShort(head.getTransID());
        //2.flag
        buffer.putShort(head.getFlag());
        //3.query count
        buffer.putShort(head.getQueryCount());
        //4.answer count
        List<Record> recordList = response.getRecordList();
        buffer.putShort((short)((recordList== null || recordList.size() == 0) ? 0 : recordList.size()));
        //authority server count
        buffer.putShort(head.getAuthoritativeNameServerCount());
        //additional related RRs(Resource Record)
        buffer.putShort(head.getAdditionalRecordsCount());
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
    private static Head decoderHeader(ByteBuffer messageBuffer) {
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
        Head head = new Head();
        head.setTransID(id);
        head.setFlag(flag);
        head.setQueryCount(queryCount);
        head.setAnswerCount(answerCount);
        head.setAuthoritativeNameServerCount(authoritativeNameServerCount);
        head.setAdditionalRecordsCount(additionalRecordsCount);
        return head;
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
            Map<String, Integer> domainOffsetMap = queryList.stream().collect(HashMap::new, (hashMap, query) -> hashMap.put(query.getDomain(), query.getOffset()), Map::putAll);
            for (Record record : recordList) {
                encodeRecord(record, domainOffsetMap.get(record.getDomain()), buffer, domainOffsetMap);
            }
        }
    }

    private static void encodeRecord(Record record, Integer offset, ByteBuffer buffer, Map<String, Integer> domainOffsetMap) {
        if(offset == null) {
            //domain
            encodeDomain(buffer, record.getDomain());
        } else {
            //pointer marker
            buffer.put((byte)0xC0);
            //offset
            buffer.put(offset.byteValue());
        }
        //type
        buffer.putShort(record.getRecordType());
        //class
        buffer.putShort(record.getRecordClass());
        //ttl
        buffer.putInt(record.getTtl());
        //length
        buffer.putShort((short)record.getData().length);
        int domainOffsetIndex = -1;
        if(record.getRecordType() == RecordType.CNAM.value) {
            domainOffsetIndex = buffer.position();
        }
        //data
        buffer.put(record.getData());
        if(domainOffsetIndex > -1) {
            int position = buffer.position();
            buffer.position(domainOffsetIndex);
            String domain = decodeDomain(buffer);
            domainOffsetMap.put(domain, domainOffsetIndex);
            buffer.position(position);
        }
    }

    private static void encodeQueryList(List<Query> queryList, ByteBuffer buffer) {
        for (Query query : queryList) {
            encodeQuery(query, buffer);
        }
    }

    private static void encodeQuery(Query query, ByteBuffer buffer) {
        //domain
        encodeDomain(buffer, query.getDomain());
        //type
        buffer.putShort(query.getType());
        //class
        buffer.putShort(query.getClazz());
    }

    private static List<Record> decodeRecord(ByteBuffer messageBuffer, int answerCount) {
        if(answerCount < 0) {
            throw new IllegalArgumentException("answerCount小于0");
        } else if(answerCount == 0) {
            return Collections.emptyList();
        } else {
            List<Record> recordList = new ArrayList<>();
            while (answerCount-- > 0) {
                String domain;
                //因为域名字符的限制(最大为63)所以byte字节的高两位始终为00，所以使用高两位使用11表示使用偏移量来表示对应的域名,10和01两种状态被保留
                //前面内容都是定长，所以偏移量一定是从12开始算起
                int length = messageBuffer.get();
                if(length == (byte)0xC0) {
                    int offset = messageBuffer.get();
                    if(offset < 0) {
                        offset &= 0b11111111;
                    }
                    int index = messageBuffer.position();
                    messageBuffer.position(offset);
                    domain = decodeDomain(messageBuffer);
                    messageBuffer.position(index);
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

    private static String decodeDomain(ByteBuffer messageBuffer) {
        byte length;
        StringBuilder builder = new StringBuilder();
        while (true) {
            length = messageBuffer.get();
            if(length == 0) {
                break;
            } else if(length == (byte)0xC0) {
                int offset = messageBuffer.get();
                int position = messageBuffer.position();
                messageBuffer.position(offset);
                builder.append(decodeDomain(messageBuffer));
                messageBuffer.position(position);
                break;
            } else {
                String part = decodePartDomain(messageBuffer, length);
                builder.append(part).append(".");
            }
        }
        if(builder.charAt(builder.length() - 1) == '.') {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }

    private static String decodePartDomain(ByteBuffer messageBuffer, int length) {
        byte[] partDomain = new byte[length];
        messageBuffer.get(partDomain);
        return new String(partDomain);
    }

    private static void encodeDomain(ByteBuffer buffer, String domain) {
        String[] elements = domain.split("\\.");
        for(String ele: elements) {
            //长度
            buffer.put((byte)ele.length());
            //value
            buffer.put(ele.getBytes());
        }
        buffer.put((byte)0);
    }

    private static void requestCheck(short flag) {
        if(!MessageFlagUtil.isQuery(flag)) {
            throw new ForbidServiceException("不是查询");
        }
        if(!MessageFlagUtil.isStandardQuery(flag)) {
            throw new ForbidServiceException("不是正向查询");
        }
        if(MessageFlagUtil.isQueryTruncated(flag)) {
            throw new ForbidServiceException("截断请求");
        }
    }

    private static void responseCheck(short flag) {
        if(!MessageFlagUtil.isResponse(flag)) {
            throw new ForbidServiceException("不是响应");
        }
        if(!MessageFlagUtil.isAuthoritativeServer(flag)) {
            throw new ForbidServiceException("不是权威主机");
        }
        if(MessageFlagUtil.isQueryTruncated(flag)) {
            throw new ForbidServiceException("截断响应");
        }
        if(!MessageFlagUtil.isRecursionDesired(flag)) {
            throw new ForbidServiceException("不是递归查询");
        }
        if(!MessageFlagUtil.isRecursionAvailable(flag)) {
            throw new ForbidServiceException("递归查询不可用");
        }
        if(!MessageFlagUtil.isAuthoritativeAnswer(flag)) {
            throw new ForbidServiceException("不是权威应答");
        }
    }
}
