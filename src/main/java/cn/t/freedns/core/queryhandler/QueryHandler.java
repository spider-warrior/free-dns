package cn.t.freedns.core.queryhandler;

import cn.t.freedns.core.MessageContext;
import cn.t.freedns.core.RequestProcessTracer;
import cn.t.freedns.core.data.Query;
import cn.t.freedns.core.data.Record;

import java.util.List;

public interface QueryHandler {
    boolean support(Query query);
    List<Record> handle(Query query, MessageContext messageContext, RequestProcessTracer requestProcessTracer);
}
