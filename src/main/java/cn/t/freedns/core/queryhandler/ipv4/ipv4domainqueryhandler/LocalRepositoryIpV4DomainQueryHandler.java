package cn.t.freedns.core.queryhandler.ipv4.ipv4domainqueryhandler;

import cn.t.freedns.core.MessageContext;
import cn.t.freedns.core.RequestProcessTracer;
import cn.t.freedns.core.data.Query;
import cn.t.freedns.core.data.Record;
import cn.t.freedns.core.queryhandler.ipv4.IpV4DomainQueryHandler;
import cn.t.freedns.repository.ResourceRecordRepository;

import java.util.List;

public class LocalRepositoryIpV4DomainQueryHandler implements IpV4DomainQueryHandler {

    private final ResourceRecordRepository resourceRecordRepository;

    @Override
    public List<Record> handle(Query query, MessageContext messageContext, RequestProcessTracer requestProcessTracer) {
        //trace [io thread local config search start time]
        requestProcessTracer.setIoIntensiveThreadLocalConfigSearchStartTime(System.currentTimeMillis());
        try {
            return doTryLocalConfigResourceRecords(query.getDomain());
        } finally {
            //trace [io thread local config search end time]
            requestProcessTracer.setIoIntensiveThreadLocalConfigSearchEndTime(System.currentTimeMillis());
        }
    }

    private List<Record> doTryLocalConfigResourceRecords(String domain) {
        return resourceRecordRepository.selectIpv4RecordListByDomainName(domain);
    }

    public LocalRepositoryIpV4DomainQueryHandler(ResourceRecordRepository resourceRecordRepository) {
        this.resourceRecordRepository = resourceRecordRepository;
    }
}
