package com.thor.spider.downloader.api;

import com.thor.spider.downloader.Page;
import com.thor.spider.downloader.Request;
import com.thor.spider.downloader.Site;
import com.thor.spider.downloader.Task;

import java.util.concurrent.Future;

/**
 * Downloader is the part that downloads web pages and store in Page object. <br>
 * Downloader has {@link #setThread(int)} method because downloader is always the bottleneck of a crawler,
 * there are always some mechanisms such as pooling in downloader, and pool size is related to thread numbers.
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
public interface Downloader {

    /**
     * Downloads web pages and store in Page object.
     *
     * @param request
     * @param task
     * @return page
     */
    Page download(Request request, Site site, Task task);

    /**
     * download by site
     * @param request request
     * @param site site
     * @return
     */
    Page download(Request request, Site site);

    /**
     * Tell the downloader how many threads the spider used.
     * @param threadNum number of threads
     */
    void setThread(int threadNum);

    /**
     * 异步下载
     * @param request 请求
     * @param site 网站
     * @param handler 处理请求
     */
    Future<Page> download(Request request, Site site, DownloaderHandler handler);
}
