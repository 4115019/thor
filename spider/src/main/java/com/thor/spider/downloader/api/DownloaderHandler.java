package com.thor.spider.downloader.api;

import com.thor.spider.downloader.Page;
import com.thor.spider.exception.BaseException;
import com.thor.spider.exception.BusinessException;

/**
 * Created by huangpin on 17/8/2.
 */
public interface DownloaderHandler {
    /**
     * 处理响应码
     *
     * @param statusCode 响应码
     */
    void handleReceivedStatus(int statusCode);

    /**
     * 处理回调page
     *
     * @param page page
     */
    void handlePage(Page page);

    /**
     * 处理异常
     *
     * @param e 异常
     */
    void handleThrowable(BaseException e);
}
