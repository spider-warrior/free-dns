package cn.t.freedns.core.data;


import java.util.List;

/**
 * @author yj
 * @since 2020-01-01 10:45
 **/
public class Request {

    //head
    private Head head;
    //query list
    private List<Query> queryList;

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

    @Override
    public String toString() {
        return "Request{" +
                "header=" + head +
                ", queryList=" + queryList +
                '}';
    }
}
