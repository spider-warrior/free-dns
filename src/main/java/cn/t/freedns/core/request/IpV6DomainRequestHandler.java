package cn.t.freedns.core.request;

import cn.t.freedns.core.data.Record;
import cn.t.freedns.core.data.RecordClass;
import cn.t.freedns.core.data.RecordType;

/**
 * IpV6DomainRequestHandler
 *
 * @author <a href="mailto:spider-warrior@liby.ltd">研发部-蜘蛛大侠</a>
 * @version V1.0
 * @since 2021-12-20 11:09
 **/
public class IpV6DomainRequestHandler implements RequestHandler {

    @Override
    public boolean support(Query query) {
        //class: internet && type: AAAA
        return query != null
                && RecordType.AAAA == RecordType.getRecordType(query.getType())
                && RecordClass.IN == RecordClass.getRecordClass(query.getClazz());
    }

    @Override
    public Record handler(Query query) {
        return null;
    }
}