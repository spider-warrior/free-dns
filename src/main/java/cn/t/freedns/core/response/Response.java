package cn.t.freedns.core.response;


import cn.t.freedns.core.data.Header;
import cn.t.freedns.core.data.Record;
import cn.t.freedns.core.request.Query;

import java.util.List;

/**
 * @author yj
 * @since 2020-01-01 15:01
 **/
public class Response {

    //head
    private Header header;
    //query list
    private List<Query> queryList;
    //record list
    private List<Record> recordList;

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

    public List<Record> getRecordList() {
        return recordList;
    }

    public void setRecordList(List<Record> recordList) {
        this.recordList = recordList;
    }

    @Override
    public String toString() {
        return "Response{" +
                "header=" + header +
                ", queryList=" + queryList +
                ", recordList=" + recordList +
                '}';
    }
}
