package cn.t.freedns.core.queryhandler.ipv4;

import cn.t.freedns.core.MessageContext;
import cn.t.freedns.core.RequestProcessTracer;
import cn.t.freedns.core.data.Query;
import cn.t.freedns.core.data.Record;

import java.util.List;

public interface IpV4DomainQueryHandler {
    List<Record> handle(Query query, MessageContext messageContext, RequestProcessTracer requestProcessTracer);
}
