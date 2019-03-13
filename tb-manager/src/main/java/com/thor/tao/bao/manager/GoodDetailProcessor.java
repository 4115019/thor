package com.thor.tao.bao.manager;

import com.thor.spider.core.Spider;
import com.thor.spider.core.processor.PageProcessor;
import com.thor.spider.downloader.Page;
import com.thor.spider.downloader.ResultItems;
import com.thor.spider.downloader.Site;

/**
 * @author huangpin
 * @date 2019-03-11
 */
public class GoodDetailProcessor implements PageProcessor {
    private Site site = Site.me()
            .setRetryTimes(3).setSleepTime(1000)
            .setUseGzip(true);

    @Override
    public void process(Page page) {
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider spider = Spider.create(new GoodDetailProcessor()).thread(2);
        String urlTemplate = "http://www.baidu.com";
        ResultItems resultItems = spider.<ResultItems>get(urlTemplate);
    }
}
