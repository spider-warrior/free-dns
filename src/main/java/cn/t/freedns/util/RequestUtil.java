package cn.t.freedns.util;

import cn.t.freedns.core.data.Header;
import cn.t.freedns.core.data.Record;
import cn.t.freedns.core.request.Request;

import java.util.List;

/**
 * @author <a href="mailto:spider-warrior@liby.ltd">研发部-蜘蛛大侠</a>
 * @version V1.0
 * @since 2021-12-20 11:25
 **/
public class RequestUtil {

    public static Header responseHeader(Request request, List<Record> recordList) {
        Header header = request.getHeader();
        short flag = header.getFlag();
        flag = FlagUtil.markResponse(flag);
        //如果客户端设置建议递归查询
        if(FlagUtil.isRecursionDesired(header.getFlag())) {
            flag = FlagUtil.markRecursionAvailable(flag);
        }
        flag = FlagUtil.markAuthorityServer(flag);
        header.setFlag(flag);
        header.setAnswerCount((recordList == null || recordList.size() == 0) ? (short)0 : (short)recordList.size());
        return header;
    }

}