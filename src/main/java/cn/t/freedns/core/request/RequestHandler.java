package cn.t.freedns.core.request;

import cn.t.freedns.core.data.Record;

public interface RequestHandler {
    boolean support(Query query);
    Record handler(Query query);
}
