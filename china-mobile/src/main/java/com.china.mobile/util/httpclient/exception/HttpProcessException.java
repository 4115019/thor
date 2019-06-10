package com.china.mobile.util.httpclient.exception;

/**
 * @author huangpin
 * @date 2019-01-21
 */
public class HttpProcessException extends Exception {
    public HttpProcessException(Exception e) {
        super(e);
    }

    /**
     * @param msg 消息
     */
    public HttpProcessException(String msg) {
        super(msg);
    }

    /**
     * @param message 异常消息
     * @param e       异常
     */
    public HttpProcessException(String message, Exception e) {
        super(message, e);
    }
}
