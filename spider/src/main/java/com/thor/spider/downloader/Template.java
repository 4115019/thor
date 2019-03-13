package com.thor.spider.downloader;

import java.io.Serializable;

/**
 * Created by huangpin on 16/9/21.
 * 结果模板
 */
public interface Template extends Serializable{
    /**
     * 设置错误码
     * @param code 错误码
     */
    void setCode(String code);

    /**
     * 设置错误消息
     * @param message 错误消息
     */
    void setMessage(String message);
}
