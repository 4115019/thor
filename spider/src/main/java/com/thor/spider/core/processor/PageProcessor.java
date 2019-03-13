package com.thor.spider.core.processor;

import com.thor.spider.downloader.Page;
import com.thor.spider.downloader.Site;

import java.text.ParseException;

/**
 * Interface to be implemented to customize a crawler.<br>
 * <br>
 * In PageProcessor, you can customize:
 * <p/>
 * start urls and other settings in {@link Site}<br>
 * how the urls to fetch are detected               <br>
 * how the data are extracted and stored             <br>
 *
 * @author code4crafter@gmail.com <br>
 * @see Site
 * @see Page
 * @since 0.1.0
 */
public interface PageProcessor {

    /**
     * process the page, extract urls to fetch, extract the data and store
     *
     * @param page
     */
    void process(Page page) throws ParseException;


    /**
     * get the site settings
     *
     * @return site
     * @see Site
     */
    Site getSite();
}
