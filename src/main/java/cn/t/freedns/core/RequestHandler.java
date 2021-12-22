package cn.t.freedns.core;


import cn.t.freedns.core.data.*;
import cn.t.freedns.core.queryhandler.IpV4DomainQueryHandler;
import cn.t.freedns.core.queryhandler.IpV6DomainQueryHandler;
import cn.t.freedns.core.queryhandler.QueryHandler;
import cn.t.freedns.util.MessageCodecUtil;
import cn.t.freedns.util.MessageFlagUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yj
 * @since 2020-01-01 10:43
 **/
public class RequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private final List<QueryHandler> queryHandlerList = new ArrayList<>();
    public void handle(Request request, MessageContext messageContext, RequestProcessTracer requestProcessTracer) {
        //trace [io thread start time]
        requestProcessTracer.setIoIntensiveThreadStartTime(System.currentTimeMillis());
        List<Query> queryList = request.getQueryList();
        List<Record> recordList = new ArrayList<>(queryList.size());
        for (Query query : queryList) {
            QueryHandler queryHandler = selectMessageHandler(query);
            if(queryHandler != null) {
                //trace [domain]
                requestProcessTracer.addDomain(query.getDomain());
                List<Record> partRecordList = queryHandler.handle(query, messageContext, requestProcessTracer);
                if(partRecordList != null) {
                    recordList.addAll(partRecordList);
                }
            } else {
                logger.error("未能处理的消息: {}", request);
            }
        }
        Response response = new Response();
        Head responseHead = responseHeader(request, recordList);
        response.setHead(responseHead);
        response.setQueryList(queryList);
        response.setRecordList(recordList);
        try {
            messageContext.write(MessageCodecUtil.encodeResponse(response));
        } catch (IOException e) {
            logger.error("响应客户端失败", e);
        }
        //trace [io thread end time]
        requestProcessTracer.setIoIntensiveThreadEndTime(System.currentTimeMillis());
        System.out.println(requestProcessTracer.debugDuration());
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
