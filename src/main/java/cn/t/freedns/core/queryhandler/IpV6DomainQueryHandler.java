package cn.t.freedns.core.queryhandler;

import cn.t.freedns.core.MessageContext;
import cn.t.freedns.core.RequestProcessTracer;
import cn.t.freedns.core.data.Query;
import cn.t.freedns.core.data.Record;
import cn.t.freedns.core.constants.RecordClass;
import cn.t.freedns.core.constants.RecordType;

import java.util.Collections;
import java.util.List;

/**
 * IpV6DomainRequestHandler
 *
 * @author <a href="mailto:spider-warrior@liby.ltd">研发部-蜘蛛大侠</a>
 * @version V1.0
 * @since 2021-12-20 11:09
 **/
public class IpV6DomainQueryHandler implements QueryHandler {

    @Override
    public boolean support(Query query) {
        //class: internet && type: AAAA
        return query != null
                && RecordType.AAAA == RecordType.getRecordType(query.getType())
                && RecordClass.IN == RecordClass.getRecordClass(query.getClazz());
    }

    @Override
    public List<Record> handler(Query query, MessageContext messageContext, RequestProcessTracer requestProcessTracer) {
        return Collections.emptyList();
    }
}