package cn.t.freedns.core.queryhandler;

import cn.t.freedns.core.MessageContext;
import cn.t.freedns.core.data.Query;
import cn.t.freedns.core.data.Record;

import java.util.List;

public interface QueryHandler {
    boolean support(Query query);
    List<Record> handler(MessageContext messageContext, Query query);
}
