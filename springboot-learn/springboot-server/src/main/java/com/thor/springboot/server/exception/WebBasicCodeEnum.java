package com.thor.springboot.server.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by huangpin on 16/12/19.
 */
@AllArgsConstructor
public enum WebBasicCodeEnum {
    ILLEGAL_ARGUMENT_ERROR("请求参数不合法,请核对参数"),
    ARGUMENT_PARSE_ERROR("参数解析异常"),
    ;

    @Getter
    private final String message;
}
