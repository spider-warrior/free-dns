package cn.t.freedns.core;


import cn.t.freedns.ForbidServiceException;
import cn.t.freedns.core.constants.RecordClass;
import cn.t.freedns.core.constants.RecordType;
import cn.t.freedns.core.data.*;
import cn.t.freedns.core.queryhandler.IpV4DomainQueryHandler;
import cn.t.freedns.core.queryhandler.IpV6DomainQueryHandler;
import cn.t.freedns.core.queryhandler.QueryHandler;
import cn.t.freedns.util.MessageFlagUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yj
 * @since 2020-01-01 10:43
 **/
public class RequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private final List<QueryHandler> queryHandlerList = new ArrayList<>();
    public Response handle(Request request, MessageContext messageContext, RequestProcessTracer requestProcessTracer) {
        List<Query> queryList = request.getQueryList();
        List<Record> recordList = new ArrayList<>(queryList.size());
        for (Query query : queryList) {
            QueryHandler queryHandler = selectMessageHandler(query);
            if(queryHandler != null) {
                //trace [domain]
                requestProcessTracer.addDomain(query.getDomain() + "(" + RecordType.getRecordType(query.getType()) + "," + RecordClass.getRecordClass(query.getClazz()) + ")");
                List<Record> partRecordList = queryHandler.handle(query, messageContext, requestProcessTracer);
                if(partRecordList == null || partRecordList.size() == 0) {
                    logger.warn("域名: [{}]未查询到匹配记录, type: {}, recordClass: {}", query.getDomain(), query.getType(), query.getClazz());
                } else {
                    recordList.addAll(partRecordList);
                }
            } else {
                throw new ForbidServiceException("不能处理的请求类型");
            }
        }
        Response response = new Response();
        Head responseHead = responseHeader(request, recordList);
        response.setHead(responseHead);
        response.setQueryList(queryList);
        response.setRecordList(recordList);
        return response;
    }
    private QueryHandler selectMessageHandler(Query query) {
        for(QueryHandler queryHandler : queryHandlerList) {
            if(queryHandler.support(query)) {
                return queryHandler;
            }
        }
        return null;
    }

    public static Head responseHeader(Request request, List<Record> recordList) {
        Head head = request.getHead();
        short flag = head.getFlag();
        flag = MessageFlagUtil.markResponse(flag);
        //如果客户端设置建议递归查询
        if(MessageFlagUtil.isRecursionDesired(head.getFlag())) {
            flag = MessageFlagUtil.markRecursionAvailable(flag);
        }
        flag = MessageFlagUtil.markAuthorityServer(flag);
        head.setFlag(flag);
        head.setAnswerCount((recordList == null || recordList.size() == 0) ? (short)0 : (short)recordList.size());
        return head;
    }

    public RequestHandler() {
        queryHandlerList.add(new IpV4DomainQueryHandler());
        queryHandlerList.add(new IpV6DomainQueryHandler());
    }
}
