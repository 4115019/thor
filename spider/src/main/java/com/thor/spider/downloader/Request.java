package com.thor.spider.downloader;

import com.thor.spider.downloader.utils.HttpMethod;
import com.thor.spider.downloader.utils.Properties;
import com.thor.spider.parser.utils.Experimental;
import lombok.Getter;

import java.io.Serializable;

/**
 * Object contains url to crawl.<br>
 * It contains some additional information.<br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
public class Request implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String CYCLE_TRIED_TIMES = "_cycle_tried_times";
    public static final String RETRY_TIMES = "retry_times";
    public static final String STATUS_CODE = "statusCode";
    public static final String PROXY = "proxy";
    public static final String TASK = "task";

    private String url;

    private String method = HttpMethod.HTTP_METHOD.HTTP_GET.getString();

    /**
     * Priority of the request.<br>
     * The bigger will be processed earlier. <br>
     */
    private long priority;

    @Getter
    protected Properties extras = new Properties();

    @Deprecated
    public Request() {
    }

    @Deprecated
    public Request(String url) {
        this.url = url;
    }

    public static Request post(String url) {
        Request request = new Request(url);
        request.setMethod(HttpMethod.HTTP_METHOD.HTTP_POST);
        return request;
    }

    public static Request put(String url) {
        Request request = new Request(url);
        request.setMethod(HttpMethod.HTTP_METHOD.HTTP_PUT);
        return request;
    }

    public static Request get(String url) {
        Request request = new Request(url);
        request.setMethod(HttpMethod.HTTP_METHOD.HTTP_GET);
        return request;
    }

    public long getPriority() {
        return priority;
    }

    /**
     * Set the priority of request for sorting.<br>
     * Need a scheduler supporting priority.<br>
     *
     * @param priority
     * @return this
     */
    @Experimental
    public Request setPriority(long priority) {
        this.priority = priority;
        return this;
    }

    public Object getExtra(String key) {
        if (extras == null) {
            return null;
        }
        return extras.get(key);
    }

    public Request putExtra(String key, Object value) {
        if (extras == null) {
            extras = new Properties();
        }
        extras.put(key, value);
        return this;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Request request = (Request) o;

        if (!url.equals(request.url)) return false;

        return true;
    }

    public <T> T getTask() {
        return getExtras() != null && getExtras().containsKey(TASK) ? (T) getExtras().get(TASK) : null;
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }

    public void setExtras(Properties extras) {
        this.extras = extras;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * The http method of the request. Get for default.
     *
     * @return httpMethod
     * @see us.codecraft.webmagic.utils.HttpConstant.Method
     * @since 0.5.0
     */
    public String getMethod() {
        return method;
    }

    public void setMethod(HttpMethod.HTTP_METHOD method) {
        this.method = method.getString();
    }

    @Override
    public String toString() {
        return "Request{" +
                "url='" + url + '\'' +
                ", method='" + method + '\'' +
                ", extras=" + extras +
                ", priority=" + priority +
                '}';
    }
}
