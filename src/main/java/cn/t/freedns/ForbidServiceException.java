package cn.t.freedns;

/**
 * 拒绝服务异常
 * @author yj
 * @since 2020-01-01 13:31
 **/
public class ForbidServiceException extends RuntimeException {
    public ForbidServiceException(String message) {
        super(message);
    }
}
