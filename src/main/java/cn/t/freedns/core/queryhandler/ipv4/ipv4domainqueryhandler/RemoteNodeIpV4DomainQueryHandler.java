package cn.t.freedns.core.queryhandler.ipv4.ipv4domainqueryhandler;

import cn.t.freedns.core.MessageContext;
import cn.t.freedns.core.RequestProcessTracer;
import cn.t.freedns.core.data.*;
import cn.t.freedns.core.queryhandler.ipv4.IpV4DomainQueryHandler;
import cn.t.freedns.util.MessageCodecUtil;
import cn.t.freedns.util.MessageFlagUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Collections;
import java.util.List;

public class RemoteNodeIpV4DomainQueryHandler implements IpV4DomainQueryHandler {

    private static final Logger logger = LoggerFactory.getLogger(RemoteNodeIpV4DomainQueryHandler.class);

    private final InetAddress remoteNodeAddress;

    @Override
    public List<Record> handle(Query query, MessageContext messageContext, RequestProcessTracer requestProcessTracer) {
        //trace [io thread thirty party search start time]
        requestProcessTracer.setIoIntensiveThreadThirtyPartySearchStartTime(System.currentTimeMillis());
        logger.info("use thirty party resolver(114.114.114.114) for domain: {}, traceId: {}", query.getDomain(), requestProcessTracer.getTraceId());
        try {
            return doTryThirtyPartyNodeResourceRecords(query.getType(), query.getClazz(), query.getDomain());
        } finally {
            //trace [io thread thirty party search end time]
            requestProcessTracer.setIoIntensiveThreadThirtyPartySearchEndTime(System.currentTimeMillis());
        }
    }

    //三方解析器
    private List<Record> doTryThirtyPartyNodeResourceRecords(short type, short clazz, String domain) {
        try(DatagramSocket internetSocket = new DatagramSocket()) {
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

    public RemoteNodeIpV4DomainQueryHandler(InetAddress remoteNodeAddress) {
        this.remoteNodeAddress = remoteNodeAddress;
    }
}
