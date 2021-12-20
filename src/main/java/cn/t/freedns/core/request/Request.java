package cn.t.freedns.core.request;


import cn.t.freedns.core.data.Header;

import java.util.List;

/**
 * @author yj
 * @since 2020-01-01 10:45
 **/
public class Request {

    //head
    private Header header;
    //query list
    private List<Query> queryList;

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public List<Query> getQueryList() {
        return queryList;
    }

    public void setQueryList(List<Query> queryList) {
        this.queryList = queryList;
    }

    @Override
    public String toString() {
        return "Request{" +
                "header=" + header +
                ", queryList=" + queryList +
                '}';
    }
}
