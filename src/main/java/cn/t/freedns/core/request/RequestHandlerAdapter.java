package cn.t.freedns.core.request;


import cn.t.freedns.core.MessageContext;
import cn.t.freedns.core.data.Header;
import cn.t.freedns.core.data.Record;
import cn.t.freedns.core.response.Response;
import cn.t.freedns.util.DnsMessageCodecUtil;
import cn.t.freedns.util.RequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yj
 * @since 2020-01-01 10:43
 **/
public class RequestHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandlerAdapter.class);

    private final List<RequestHandler> requestHandlerList = new ArrayList<>();
    public void handle(MessageContext messageContext, Request request) {
        List<Query> queryList = request.getQueryList();
        List<Record> recordList = new ArrayList<>(queryList.size());
        for (Query query : queryList) {
            RequestHandler requestHandler = selectMessageHandler(query);
            if(requestHandler != null) {
                List<Record> partRecordList = requestHandler.handler(query);
                if(partRecordList != null) {
                    recordList.addAll(partRecordList);
                }
            } else {
                logger.error("未能处理的消息: {}", request);
            }
        }
        Response response = new Response();
        Header header = RequestUtil.responseHeader(request, recordList);
        response.setHeader(header);
        response.setQueryList(queryList);
        response.setRecordList(recordList);
        messageContext.write(DnsMessageCodecUtil.encodeResponse(response));
    }
    private RequestHandler selectMessageHandler(Query query) {
        for(RequestHandler requestHandler : requestHandlerList) {
            if(requestHandler.support(query)) {
                return requestHandler;
            }
        }
        return null;
    }

    public RequestHandlerAdapter() {
        requestHandlerList.add(new IpV4DomainRequestHandler());
        requestHandlerList.add(new IpV6DomainRequestHandler());
    }
}
