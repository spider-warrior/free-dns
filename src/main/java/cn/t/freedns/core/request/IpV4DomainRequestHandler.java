package cn.t.freedns.core.request;


import cn.t.freedns.ForbidServiceException;
import cn.t.freedns.util.DnsMessageCodecUtil;
import cn.t.freedns.core.data.Header;
import cn.t.freedns.core.data.Record;
import cn.t.freedns.core.data.RecordClass;
import cn.t.freedns.core.data.RecordType;
import cn.t.freedns.core.response.Response;
import cn.t.freedns.repository.IpMappingRepository;
import cn.t.freedns.repository.MemoryIpMappingRepositoryImpl;
import cn.t.freedns.util.FlagUtil;
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
public class IpV4DomainRequestHandler implements RequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(IpV4DomainRequestHandler.class);

    private final IpMappingRepository ipMappingRepository = new MemoryIpMappingRepositoryImpl();

    @Override
    public boolean support(Query query) {
        //class: internet && type: A
        return query != null
                && RecordType.A == RecordType.getRecordType(query.getType())
                && RecordClass.IN == RecordClass.getRecordClass(query.getClazz());
    }

    @Override
    public List<Record> handler(Query query) {
        String domain = query.getDomain();
        Record record = new Record();
        record.setDomain(domain);
        //读取配置域名
        String ip = ipMappingRepository.getIpv4ByDomainName(domain);
        if(ip != null && ip.length() > 0) {
            logger.info("===================================== domain : {} use local dns config, response ip: {} =====================================", domain, ip);
            String[] ipElements = ip.split("\\.");
            byte[] ipBytes = new byte[ipElements.length];
            for (int i = 0; i < ipElements.length; i++) {
                ipBytes[i] = (byte)Short.parseShort(ipElements[i]);
            }
            record.setRecordType(RecordType.A.value);
            record.setRecordClass(RecordClass.IN.value);
            record.setTtl(600);
            record.setData(ipBytes);
            return Collections.singletonList(record);
        } else {
            logger.info("domain: {} is not config in file, use local resolver", domain);
            //加载
            try {
                if(true) {
                    throw new UnknownHostException("on purpose");
                }
                InetAddress address = InetAddress.getAllByName(domain)[0];
                logger.info("domain: {} resolved by local resolver, address: {}", domain, address);

                if(address instanceof Inet4Address) {
                    Inet4Address inet4Address = (Inet4Address)address;
                    record.setRecordType(RecordType.A.value);
                    record.setRecordClass(RecordClass.IN.value);
                    record.setTtl(600);
                    record.setData(inet4Address.getAddress());
                    return Collections.singletonList(record);
                } else {
                    throw new ForbidServiceException("不支持的地址类型");
                }
            } catch (UnknownHostException e) {
                logger.info("domain: {} cannot be resolved by local resolver, use 114.114.114.114", domain);
                try {
                    Header header = new Header();
                    header.setTransID((short)System.currentTimeMillis());
                    short flag = 0;
                    flag = FlagUtil.markRecursionDesired(flag);
                    header.setFlag(flag);
                    header.setQueryCount((short)1);
                    header.setAnswerCount((short)0);
                    header.setAuthoritativeNameServerCount((short)0);
                    header.setAdditionalRecordsCount((short)0);
                    Request request = new Request();
                    request.setHeader(header);
                    Query outerQuery = new Query();
                    outerQuery.setDomain(domain);
                    outerQuery.setType(query.getType());
                    outerQuery.setClazz(query.getClazz());
                    request.setQueryList(Collections.singletonList(outerQuery));
                    byte[] domainRequestBytes = DnsMessageCodecUtil.encodeRequest(request);
                    DatagramSocket internetSocket = new DatagramSocket();
                    DatagramPacket internetSendPacket = new DatagramPacket(domainRequestBytes, domainRequestBytes.length, InetAddress.getByName("114.114.114.114"), 53);
                    internetSocket.send(internetSendPacket);
                    byte[] receivedData = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(receivedData, receivedData.length);
                    internetSocket.receive(packet);
                    byte[] responseBytes = new byte[packet.getLength()];
                    System.arraycopy(packet.getData(), 0, responseBytes, 0, responseBytes.length);
                    Response response = DnsMessageCodecUtil.decodeResponse(responseBytes);
                    return response.getRecordList();
                } catch (IOException ioe) {
                    throw new RuntimeException(ioe);
                }
            }
        }
    }
}
