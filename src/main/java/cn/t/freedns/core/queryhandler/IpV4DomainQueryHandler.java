package cn.t.freedns.core.queryhandler;


import cn.t.freedns.ForbidServiceException;
import cn.t.freedns.core.MessageContext;
import cn.t.freedns.core.RequestProcessTracer;
import cn.t.freedns.core.constants.RecordClass;
import cn.t.freedns.core.constants.RecordType;
import cn.t.freedns.core.data.*;
import cn.t.freedns.repository.MemoryResourceRecordRepositoryImpl;
import cn.t.freedns.repository.ResourceRecordRepository;
import cn.t.freedns.util.MessageCodecUtil;
import cn.t.freedns.util.MessageFlagUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.Collections;
import java.util.List;

/**
 * @author yj
 * @since 2020-01-01 11:37
 **/
public class IpV4DomainQueryHandler implements QueryHandler {

    private static final Logger logger = LoggerFactory.getLogger(IpV4DomainQueryHandler.class);

    private final ResourceRecordRepository resourceRecordRepository = new MemoryResourceRecordRepositoryImpl();

    @Override
    public boolean support(Query query) {
        //class: internet && type: A
        return query != null
                && RecordType.A == RecordType.getRecordType(query.getType())
                && RecordClass.IN == RecordClass.getRecordClass(query.getClazz());
    }

    @Override
    public List<Record> handle(Query query, MessageContext messageContext, RequestProcessTracer requestProcessTracer) {
        List<Record> recordList = tryLocalConfigResourceRecords(query.getDomain(), requestProcessTracer);
        if(recordList != null) {
            return recordList;
        }
//        recordList = tryLocalNodeResourceRecords(query.getDomain(), requestProcessTracer);
//        if(!CollectionUtil.isEmpty(recordList)) {
//            domainRecordListMap.put(query.getDomain(), recordList);
//            return recordList;
//        }
        recordList = tryThirtyPartyNodeResourceRecords(query.getType(), query.getClazz(), query.getDomain(), requestProcessTracer);
        resourceRecordRepository.saveIpv4RecordList(query.getDomain(), recordList);
        return recordList;
    }

    private List<Record> tryLocalConfigResourceRecords(String domain, RequestProcessTracer requestProcessTracer) {
        //trace [io thread local config search start time]
        requestProcessTracer.setIoIntensiveThreadLocalConfigSearchStartTime(System.currentTimeMillis());
        try {
            return doTryLocalConfigResourceRecords(domain);
        } finally {
            //trace [io thread local config search end time]
            requestProcessTracer.setIoIntensiveThreadLocalConfigSearchEndTime(System.currentTimeMillis());
        }
    }

    //本地配置
    private List<Record> doTryLocalConfigResourceRecords(String domain) {
        return resourceRecordRepository.selectIpv4RecordListByDomainName(domain);
    }

    private List<Record> tryLocalNodeResourceRecords(String domain, RequestProcessTracer requestProcessTracer) {
        //trace [io thread local node search start time]
        requestProcessTracer.setIoIntensiveThreadLocalNodeSearchStartTime(System.currentTimeMillis());
        try {
            return doTryLocalNodeResourceRecords(domain);
        } finally {
            //trace [io thread local node search end time]
            requestProcessTracer.setIoIntensiveThreadLocalNodeSearchEndTime(System.currentTimeMillis());
        }
    }

    //本地主机解析器
    private List<Record> doTryLocalNodeResourceRecords(String domain) {
        try {
            InetAddress address = InetAddress.getByName(domain);
            logger.info("domain: {} resolved by local resolver, address: {}", domain, address);
            if(address instanceof Inet4Address) {
                Record record = new Record();
                record.setDomain(domain);
                Inet4Address inet4Address = (Inet4Address)address;
                record.setRecordType(RecordType.A.value);
                record.setRecordClass(RecordClass.IN.value);
                record.setTtl(600);
                record.setData(inet4Address.getAddress());
                return Collections.singletonList(record);
            } else {
                throw new ForbidServiceException("不支持的地址类型");
            }
        } catch (UnknownHostException ignore) {
            return Collections.emptyList();
        }
    }

    private List<Record> tryThirtyPartyNodeResourceRecords(short type, short clazz, String domain, RequestProcessTracer requestProcessTracer) {
        //trace [io thread thirty party search start time]
        requestProcessTracer.setIoIntensiveThreadThirtyPartySearchStartTime(System.currentTimeMillis());
        try {
            return doTryThirtyPartyNodeResourceRecords(type, clazz, domain);
        } finally {
            //trace [io thread thirty party search end time]
            requestProcessTracer.setIoIntensiveThreadThirtyPartySearchEndTime(System.currentTimeMillis());
        }
    }

    //三方解析器
    private List<Record> doTryThirtyPartyNodeResourceRecords(short type, short clazz, String domain) {
        logger.info("domain: {} cannot be resolved by local resolver, use 114.114.114.114", domain);
        try {
            Head head = new Head();
            head.setTransID((short)System.currentTimeMillis());
            short flag = 0;
            flag = MessageFlagUtil.markRecursionDesired(flag);
            head.setFlag(flag);
            head.setQueryCount((short)1);
            head.setAnswerCount((short)0);
            head.setAuthoritativeNameServerCount((short)0);
            head.setAdditionalRecordsCount((short)0);
            Request request = new Request();
            request.setHead(head);
            Query outerQuery = new Query();
            outerQuery.setDomain(domain);
            outerQuery.setType(type);
            outerQuery.setClazz(clazz);
            request.setQueryList(Collections.singletonList(outerQuery));
            byte[] domainRequestBytes = MessageCodecUtil.encodeRequest(request);
            DatagramSocket internetSocket = new DatagramSocket();
            DatagramPacket internetSendPacket = new DatagramPacket(domainRequestBytes, domainRequestBytes.length, InetAddress.getByName("114.114.114.114"), 53);
            internetSocket.send(internetSendPacket);
            internetSocket.send(internetSendPacket);
            byte[] receivedData = new byte[1024];
            DatagramPacket packet = new DatagramPacket(receivedData, receivedData.length);
            internetSocket.receive(packet);
            byte[] responseBytes = new byte[packet.getLength()];
            System.arraycopy(packet.getData(), 0, responseBytes, 0, responseBytes.length);
            Response response = MessageCodecUtil.decodeResponse(responseBytes);
            return response.getRecordList();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }
}
