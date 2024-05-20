package cn.t.freedns.core.queryhandler.ipv4.ipv4domainqueryhandler;

import cn.t.freedns.ForbidServiceException;
import cn.t.freedns.core.MessageContext;
import cn.t.freedns.core.RequestProcessTracer;
import cn.t.freedns.core.constants.RecordClass;
import cn.t.freedns.core.constants.RecordType;
import cn.t.freedns.core.data.Query;
import cn.t.freedns.core.data.Record;
import cn.t.freedns.core.queryhandler.ipv4.IpV4DomainQueryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

public class LocalNodeIpV4DomainQueryHandler implements IpV4DomainQueryHandler {

    private static final Logger logger = LoggerFactory.getLogger(LocalNodeIpV4DomainQueryHandler.class);

    @Override
    public List<Record> handle(Query query, MessageContext messageContext, RequestProcessTracer requestProcessTracer) {
        //trace [io thread local node search start time]
        requestProcessTracer.setIoIntensiveThreadLocalNodeSearchStartTime(System.currentTimeMillis());
        try {
            return doTryLocalNodeResourceRecords(query.getDomain());
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
}
