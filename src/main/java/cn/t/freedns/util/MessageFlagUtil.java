package cn.t.freedns.util;

/**
 * @author yj
 * @since 2020-01-01 12:47
 **/
public class MessageFlagUtil {

    //query
    private static final int QR_BIT_MASK =      0b1000000000000000;
    //operation code
    private static final int OP_CODE_BIT_MASK = 0b0111100000000000;
    //authority server
    private static final int AS_MASK     =      0b0000010000000000;
    //truncated
    private static final int TC_BIT_MASK =      0b0000001000000000;
    //recursion desired(在请求中设置，直接复制到响应中，要求主机递归查询)
    private static final int RD_BIT_MASK =      0b0000000100000000;
    //recursion available(在响应中设置，表示是否支持递归查询)
    private static final int RA_BIT_MASK =      0b0000000010000000;
    //当前主机为域名的权威主机(在响应中有效)
    private static final int AA_BIT_MASK =      0b0000000000100000;
    //reply code mask
    private static final int R_CODE_BIT_MASK =  0b0000000000001111;

    /**
     * 是否为查询
     * @param flag 标志
     * @return true|false
     */
    public static boolean isQuery(short flag) {
        return (QR_BIT_MASK & flag) != QR_BIT_MASK;
    }

    /**
     * 是否为查询
     * @param flag 标志
     * @return true|false
     */
    public static boolean isResponse(short flag) {
        return (QR_BIT_MASK & flag) == QR_BIT_MASK;
    }

    /**
     * 是否为正向查询
     * @param flag 标志
     * @return true|false
     */
    public static boolean isStandardQuery(short flag) {
        return (OP_CODE_BIT_MASK & flag) == 0;
    }

    /**
     * 是否为权威主机
     * @param flag 标
     * @return true|false
     */
    public static boolean isAuthoritativeServer(short flag) {
        return (AS_MASK & flag) == AS_MASK;
    }

    /**
     * 查询是否truncated
     * @param flag 标志
     * @return true|false
     */
    public static boolean isQueryTruncated(short flag) {
        return (TC_BIT_MASK & flag) == TC_BIT_MASK;
    }

    /**
     * 是否建议递归查询
     * @param flag 标志
     * @return true|false
     */
    public static boolean isRecursionDesired(short flag) {
        return (RD_BIT_MASK & flag) == RD_BIT_MASK;
    }

    /**
     * 是否进行了递归查询
     * @param flag 标志
     * @return true|false
     */
    public static boolean isRecursionAvailable(short flag) {
        return (RA_BIT_MASK & flag) == RA_BIT_MASK;
    }

    /**
     * 是否为权威主机应答
     * @param flag 标
     * @return true|false
     */
    public static boolean isAuthoritativeAnswer(short flag) {
        return (AA_BIT_MASK & flag) == AA_BIT_MASK;
    }

    /**
     * 是否响应成功
     * @param flag 标志
     * @return true|false
     */
    public static boolean isResponseSuccess(short flag) {
        return (R_CODE_BIT_MASK & flag) == 0;
    }

    /**
     * 是否格式错误
     * @param flag 标志
     * @return true|false
     */
    public static boolean isFormatError(short flag) {
        return (R_CODE_BIT_MASK & flag) == 1;
    }

    /**
     * 是否服务器错误
     * @param flag 标志
     * @return true|false
     */
    public static boolean isServerError(short flag) {
        return (R_CODE_BIT_MASK & flag) == 2;
    }

    /**
     * 是否请求的域名不存在
     * @param flag 标志
     * @return true|false
     */
    public static boolean isDomainNotExist(short flag) {
        return (R_CODE_BIT_MASK & flag) == 3;
    }

    /**
     * 是否域名服务器不支持请求的类型
     * @param flag 标志
     * @return true|false
     */
    public static boolean isServerNotSupportedQueryType(short flag) {
        return (R_CODE_BIT_MASK & flag) == 4;
    }

    /**
     * 是否域名服务器因为策略的原因拒绝执行请求的操作(例如域名服务器不会为特定的请求者返回查询结果，或者域名服务器不会为特定的请求返回特定的数据)
     * @param flag 标志
     * @return true|false
     */
    public static boolean isStrategyForbid(short flag) {
        return (R_CODE_BIT_MASK & flag) == 5;
    }

    /**
     * 标记为响应
     * @param flag 标志
     * @return 标志
     */
    public static short markResponse(short flag) {
        return (short)(flag | QR_BIT_MASK);
    }

    /**
     * 标记为权威主机
     * @param flag 标志
     * @return 标志
     */
    public static short markAuthorityServer(short flag) {
        return (short)(flag | AS_MASK);
    }

    /**
     * 标记为递归查询
     * @param flag 标志
     * @return 标志
     */
    public static short markRecursionDesired(short flag) {
        return (short)(flag | RD_BIT_MASK);
    }

    /**
     * 标记为支持递归查询
     * @param flag 标志
     * @return 标志
     */
    public static short markRecursionAvailable(short flag) {
        return (short)(flag | RA_BIT_MASK);
    }

    /**
     * 标记为权威主机
     * @param flag 标志
     * @return 标志
     */
    public static short markAuthorityAnswer(short flag) {
        return (short)(flag | AA_BIT_MASK);
    }

}
