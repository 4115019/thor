//package com.thor.spider.downloader.impl.html;
//
//import com.gargoylesoftware.htmlunit.WebClient;
//import com.gargoylesoftware.htmlunit.html.HtmlPage;
//import com.thor.spider.downloader.Page;
//import com.thor.spider.downloader.Request;
//import com.thor.spider.downloader.Site;
//import com.thor.spider.downloader.Task;
//import com.thor.spider.downloader.api.Downloader;
//import com.thor.spider.downloader.api.DownloaderHandler;
//import com.thor.spider.parser.selector.PlainText;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
//
//import java.io.IOException;
//import java.util.concurrent.Future;
//
///**
// * Created by zangtiancai on 16-11-4.
// */
//@Slf4j
//public class HtmlUnitDownloader implements Downloader {
//
//    private static GenericKeyedObjectPool pool = ComponentPool.getHtmlComp();
//
//    @Override
//    public Page download(Request request, Site site, Task task) {
//        return download(request,site);
//    }
//
//    @Override
//    public Page download(Request request, Site site) {
//        WebClient webClient= null;
//
//        try {
//            webClient = (WebClient)pool.borrowObject("webClient");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        log.info("start download page {}", request.getUrl());
//        Page page =  new Page();
//        webClient.getOptions().setJavaScriptEnabled(true);
//        webClient.getCookieManager().setCookiesEnabled(true);
//        HtmlPage pageRow= null;
//        try {
//            pageRow = webClient.getPage(request.getUrl());
//            page.setRawText(pageRow.asXml());
//            page.setUrl(new PlainText(request.getUrl()));
//            page.setRequest(request);
//            page.setStatusCode(pageRow.getWebResponse().getStatusCode());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally {
//            pool.returnObject("webClient",webClient);
//        }
//
//        log.info("downloader by HtmlUnitDownloader {}", page.getRawText());
//        return page;
//    }
//
//    @Override
//    public void setThread(int threadNum) {
//
//    }
//
//    @Override
//    public Future<Page> download(Request request, Site site, DownloaderHandler handler) {
//        throw new UnsupportedOperationException("HtmlUnitDownloader not supported async download");
//    }
//}
