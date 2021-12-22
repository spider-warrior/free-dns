package cn.t.freedns.core.queryhandler;


import cn.t.freedns.ForbidServiceException;
import cn.t.freedns.core.MessageContext;
import cn.t.freedns.core.constants.RecordClass;
import cn.t.freedns.core.constants.RecordType;
import cn.t.freedns.core.data.*;
import cn.t.freedns.repository.IpMappingRepository;
import cn.t.freedns.repository.MemoryIpMappingRepositoryImpl;
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

    private final IpMappingRepository ipMappingRepository = new MemoryIpMappingRepositoryImpl();

    @Override
    public boolean support(Query query) {
        //class: internet && type: A
        return query != null
                && RecordType.A == RecordType.getRecordType(query.getType())
                && RecordClass.IN == RecordClass.getRecordClass(query.getClazz());
    }

    @Override
    public List<Record> handler(MessageContext messageContext, Query query) {
        //trace [io thread local config search start time]
        messageContext.setIoIntensiveThreadLocalConfigSearchStartTime(System.currentTimeMillis());
        List<Record> recordList = tryLocalConfigResourceRecords(query.getDomain());
        //trace [io thread local config search end time]
        messageContext.setIoIntensiveThreadLocalConfigSearchEndTime(System.currentTimeMillis());
        if(recordList.isEmpty()) {
            try {
                //trace [io thread local node search start time]
                messageContext.setIoIntensiveThreadLocalNodeSearchStartTime(System.currentTimeMillis());
                recordList = tryLocalNodeResourceRecords(query.getDomain());
                //trace [io thread local node search end time]
                messageContext.setIoIntensiveThreadLocalNodeSearchEndTime(System.currentTimeMillis());
            } catch (UnknownHostException e) {
                long now = System.currentTimeMillis();
                //trace [io thread local node search end time]
                messageContext.setIoIntensiveThreadLocalNodeSearchEndTime(now);
                //trace [io thread thirty party search start time]
                messageContext.setIoIntensiveThreadThirtyPartySearchStartTime(now);
                recordList = tryThirtyPartyNodeResourceRecords(query.getType(), query.getClazz(), query.getDomain());
                //trace [io thread thirty party search end time]
                messageContext.setIoIntensiveThreadThirtyPartySearchEndTime(System.currentTimeMillis());
            }
//            recordList = tryThirtyPartyNodeResourceRecords(query.getType(), query.getClazz(), query.getDomain());
        }
        return recordList;
    }

    //本地配置
    private List<Record> tryLocalConfigResourceRecords(String domain) {
        String ip = ipMappingRepository.getIpv4ByDomainName(domain);
        if(ip == null) {
            return Collections.emptyList();
        }
        logger.info("===================================== domain: {} use local config, response ip: {} =====================================", domain, ip);
        String[] ipElements = ip.split("\\.");
        byte[] ipBytes = new byte[ipElements.length];
        for (int i = 0; i < ipElements.length; i++) {
            ipBytes[i] = (byte)Short.parseShort(ipElements[i]);
        }
        Record record = new Record();
        record.setDomain(domain);
        record.setRecordType(RecordType.A.value);
        record.setRecordClass(RecordClass.IN.value);
        record.setTtl(600);
        record.setData(ipBytes);
        return Collections.singletonList(record);
    }

    //本地主机解析器
    private List<Record> tryLocalNodeResourceRecords(String domain) throws UnknownHostException {
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
    }

    //三方解析器
    private List<Record> tryThirtyPartyNodeResourceRecords(short type, short clazz, String domain) {
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
