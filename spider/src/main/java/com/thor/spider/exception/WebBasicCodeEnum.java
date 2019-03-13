package com.thor.spider.exception;

import lombok.Getter;

/**
 * Created by huangpin on 16/12/19.
 */
//@AllArgsConstructor
public enum WebBasicCodeEnum implements IErrorCode {
    ILLEGAL_ARGUMENT_ERROR("请求参数不合法,请核对参数", false),
    ARGUMENT_PARSE_ERROR("参数解析异常"),
    NOT_SUPPORTED_METHOD_ERROR("不支持的请求方式", false),
    PERMISSION_ERROR("权限不足"),
    DOWNLOAD_ERROR("下载异常"),
    USER_EXISTS_ERROR("用户已存在"),
    RESPONSE_TIME_OUT("下载超时"),
    USERNAME_OR_PASSWORD_ERROR("用户名或密码错误"),
    PASSWORD_LIMIT_ERROR("密码错误已经输错%s次,连续输错%s次账号将被锁定"),
    PASSWORD_LIMIT_MESSAGE_ERROR("密码错误次数过多,账号已被锁定"),
    UPLOAD_ERROR("上传文件异常"),
    WEB_SITE_LIMIT("网站限制"),
    PROXY_NOT_AVAILABLE("代理获取失败"),
    RESULT_NULL("返回结果为空"),
    OCR_RESULT_NULL("OCR识别异常，传入图片路径参数不正确"),
    ALREADY_AT_PROTECTION_MODE("进入保护模式"),
    ;

    @Getter
    private final String message;

    @Getter
    private final boolean isUnknownException;

    WebBasicCodeEnum(String message){
        this(message, true);
    }

    WebBasicCodeEnum(String message, boolean isUnknownException){
        this.message = message;
        this.isUnknownException = isUnknownException;
    }

    @Override
    public String getCode() {
        return getCode(WebBasicCodeStartCode,ordinal());
    }
}
