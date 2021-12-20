package cn.t.freedns.core.request;

import cn.t.freedns.core.data.Record;

import java.util.List;

public interface RequestHandler {
    boolean support(Query query);
    List<Record> handler(Query query);
}
