package cn.t.freedns.core.data;


import cn.t.freedns.core.data.Head;
import cn.t.freedns.core.data.Record;
import cn.t.freedns.core.data.Query;

import java.util.List;

/**
 * @author yj
 * @since 2020-01-01 15:01
 **/
public class Response {

    //head
    private Head head;
    //query list
    private List<Query> queryList;
    //record list
    private List<Record> recordList;

    public Head getHead() {
        return head;
    }

    public void setHead(Head head) {
        this.head = head;
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
                "header=" + head +
                ", queryList=" + queryList +
                ", recordList=" + recordList +
                '}';
    }
}
