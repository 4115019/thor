package com.thor.spider.core;

import com.thor.spider.downloader.Request;

/**
 * Listener of Spider on page processing. Used for monitor and such on.
 *
 * @author code4crafer@gmail.com
 * @since 0.5.0
 */
public interface SpiderListener {

    public void onSuccess(Request request);

    public void onError(Request request);
}
