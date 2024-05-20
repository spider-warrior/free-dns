package cn.t.freedns.core.queryhandler.ipv4;


import cn.t.freedns.core.MessageContext;
import cn.t.freedns.core.RequestProcessTracer;
import cn.t.freedns.core.constants.RecordClass;
import cn.t.freedns.core.constants.RecordType;
import cn.t.freedns.core.data.Query;
import cn.t.freedns.core.data.Record;
import cn.t.freedns.core.queryhandler.QueryHandler;
import cn.t.freedns.core.queryhandler.ipv4.ipv4domainqueryhandler.LocalNodeIpV4DomainQueryHandler;
import cn.t.freedns.core.queryhandler.ipv4.ipv4domainqueryhandler.LocalRepositoryIpV4DomainQueryHandler;
import cn.t.freedns.core.queryhandler.ipv4.ipv4domainqueryhandler.RemoteNodeIpV4DomainQueryHandler;
import cn.t.freedns.repository.MemoryResourceRecordRepositoryImpl;
import cn.t.freedns.repository.ResourceRecordRepository;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yj
 * @since 2020-01-01 11:37
 **/
public class IpV4DomainQueryHandlerAdapter implements QueryHandler {

    private final List<IpV4DomainQueryHandler> ipV4DomainQueryHandlerList = new ArrayList<>();

    @Override
    public boolean support(Query query) {
        //class: internet && type: A
        return query != null
                && RecordType.A == RecordType.getRecordType(query.getType())
                && RecordClass.IN == RecordClass.getRecordClass(query.getClazz());
    }

    @Override
    public List<Record> handle(Query query, MessageContext messageContext, RequestProcessTracer requestProcessTracer) {
        List<Record> recordList = ipV4DomainQueryHandlerList.get(0).handle(query, messageContext, requestProcessTracer);
        return recordList == null || recordList.size() == 0 ? ipV4DomainQueryHandlerList.get(1).handle(query, messageContext, requestProcessTracer) : recordList;
    }

    public IpV4DomainQueryHandlerAdapter() {
        ipV4DomainQueryHandlerList.add(new LocalRepositoryIpV4DomainQueryHandler(new MemoryResourceRecordRepositoryImpl()));
        ipV4DomainQueryHandlerList.add(new LocalNodeIpV4DomainQueryHandler());
    }

    public IpV4DomainQueryHandlerAdapter(String remoteNodeIp) throws UnknownHostException {
        ResourceRecordRepository resourceRecordRepository = new MemoryResourceRecordRepositoryImpl();
        ipV4DomainQueryHandlerList.add(new LocalRepositoryIpV4DomainQueryHandler(resourceRecordRepository));
        ipV4DomainQueryHandlerList.add(new RemoteNodeIpV4DomainQueryHandler(InetAddress.getByName(remoteNodeIp)));
    }

}
