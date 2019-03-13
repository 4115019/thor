package com.thor.spider.downloader.impl.http;

import com.alibaba.fastjson.JSONObject;
import com.thor.spider.downloader.Page;
import com.thor.spider.downloader.Request;
import com.thor.spider.downloader.Site;
import com.thor.spider.downloader.Task;
import com.thor.spider.downloader.api.AbstractDownloader;
import com.thor.spider.downloader.api.DownloaderHandler;
import com.thor.spider.downloader.proxy.HttpProxy;
import com.thor.spider.downloader.utils.HttpConstant;
import com.thor.spider.downloader.utils.UrlUtils;
import com.thor.spider.exception.BusinessException;
import com.thor.spider.exception.WebBasicCodeEnum;
import com.thor.spider.parser.selector.PlainText;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.Future;


/**
 * The http downloader based on HttpClient.
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
public class HttpClientDownloader extends AbstractDownloader {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final Map<String, CloseableHttpClient> httpClients = new HashMap<String, CloseableHttpClient>();

    private HttpClientGenerator httpClientGenerator = new HttpClientGenerator();
    private String msg;

    private CloseableHttpClient getHttpClient(Site site) {
        if (site == null) {
            return httpClientGenerator.getClient(null);
        }
        if ((site.getHttpProxy() != null && site.getHttpProxy().validate())) {
            return httpClientGenerator.getClient(site);
        } else {
            String domain = site.getDomain();
            CloseableHttpClient httpClient = httpClients.get(domain);
            if (httpClient == null) {
                synchronized (this) {
                    httpClient = httpClients.get(domain);
                    if (httpClient == null) {
                        httpClient = httpClientGenerator.getClient(site);
                        httpClients.put(domain, httpClient);
                    }
                }
            }
            return httpClient;
        }
    }

    @Override
    public Page download(Request request, Site site, Task task) {
        return download(request, site);
    }

    @Override
    public Page download(Request request, Site site) {
        return download(request, site, (request1, charset, httpResponse) -> {
            String content = getContent(charset, httpResponse);
            Page page = new Page();
            page.setRawText(content);
            page.setUrl(new PlainText(request1.getUrl()));
            page.setRequest(request1);
            page.setStatusCode(httpResponse.getStatusLine().getStatusCode());
            return page;
        });
    }

    public <T> T download(Request request, Site site, IHandleResponse<T> handle) {
        Set<Integer> acceptStatCode;
        String charset = site.getCharset();
        Map<String, String> headers = site.getHeaders();
        acceptStatCode = site.getAcceptStatCode();
        logger.info("downloading page {}, timeout {}", request.getUrl(), site.getTimeOut());
        if (site.getHttpProxy() != null && site.getHttpProxy().validate()) {
            logger.info("downloading page {}, 使用代理 proxy = {}", request.getUrl(), JSONObject.toJSONString(site.getHttpProxy()));
            request.putExtra(Request.PROXY, site.getHttpProxy());
            site.getHttpProxy().limit();
        }
        CloseableHttpResponse httpResponse = null;
        int statusCode = 0;
        try {
            HttpUriRequest httpUriRequest = getHttpUriRequest(request, site, headers);
            httpResponse = getHttpClient(site).execute(httpUriRequest);
            statusCode = httpResponse.getStatusLine().getStatusCode();
            request.putExtra(Request.STATUS_CODE, statusCode);
            if (statusAccept(acceptStatCode, statusCode)) {
                handleCookie(site, httpResponse);
                T page = handle.handleResponse(request, charset, httpResponse);
                onSuccess(request);
                return page;
            } else {
                logger.warn(msg);
                return null;
            }
        } catch (SocketTimeoutException | ConnectTimeoutException | UnknownHostException | HttpHostConnectException e) {
            logger.error("下载超时 {}", request.getUrl(), e);
            throw new BusinessException(WebBasicCodeEnum.RESPONSE_TIME_OUT, e);
        } catch (Exception e) {
            logger.error("下载出现未知异常 {}", request.getUrl(), e);
//            if (site.getCycleRetryTimes() > 0) {
//                return addToCycleRetry(request, site);
//            }
            onError(request);
            throw new BusinessException(WebBasicCodeEnum.DOWNLOAD_ERROR, e);
        } finally {
            request.putExtra(Request.STATUS_CODE, statusCode);
            try {
                if (httpResponse != null) {
                    //ensure the connection is released back to pool
                    EntityUtils.consume(httpResponse.getEntity());
                }
            } catch (IOException e) {
                logger.warn("close response fail", e);
            }
        }
    }

    private void handleCookie(Site site, CloseableHttpResponse httpResponse) {
        Header[] cookieHeaders = httpResponse.getHeaders("set-cookie");
        if (cookieHeaders != null) {
            for (Header cookieHeader : cookieHeaders) {
                String[] subCookies = cookieHeader.getValue().replace(" ", "").split(";");
                for (String subCookie : subCookies) {
                    String[] split = subCookie.split("=");
                    if (split.length == 2) {
                        BasicClientCookie basicClientCookie = new BasicClientCookie(split[0], split[1]);
                        basicClientCookie.setDomain(site.getDomain());
                        site.getCookieStore().addCookie(basicClientCookie);
                    }
                }
            }
        }
    }

    @Override
    public void setThread(int thread) {
        httpClientGenerator.setPoolSize(thread);
    }

    @Override
    public Future<Page> download(Request request, Site site, DownloaderHandler handler) {
        throw new UnsupportedOperationException("httpclient not supported async download");
    }

    protected boolean statusAccept(Set<Integer> acceptStatCode, int statusCode) {
        return acceptStatCode.contains(statusCode);
    }

    protected HttpUriRequest getHttpUriRequest(Request request, Site site, Map<String, String> headers) {
        RequestBuilder requestBuilder = selectRequestMethod(request, site).setUri(request.getUrl());
        if (headers != null) {
            for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
                requestBuilder.addHeader(headerEntry.getKey(), headerEntry.getValue());
            }
        }
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom()
                .setConnectionRequestTimeout(site.getConnectionRequestTimeout())
                .setSocketTimeout(site.getTimeOut())
                .setConnectTimeout(site.getConnectTimeout())
                .setCookieSpec(CookieSpecs.BEST_MATCH);
        if (site.getHttpProxyPool() != null && site.getHttpProxyPool().isEnable()) {
            HttpHost host = site.getHttpProxyFromPool();
            requestConfigBuilder.setProxy(host);
            request.putExtra(Request.PROXY, new HttpProxy(host));
        } else if (site.getHttpHost() != null) {
            HttpHost host = site.getHttpHost();
            requestConfigBuilder.setProxy(host);
            request.putExtra(Request.PROXY, new HttpProxy(host));
        }
        requestBuilder.setConfig(requestConfigBuilder.build());
        return requestBuilder.build();
    }

    protected RequestBuilder selectRequestMethod(Request request, Site site) {
        String method = request.getMethod();
        if (method == null || method.equalsIgnoreCase(HttpConstant.Method.GET)) {
            //default get
            return RequestBuilder.get();
        } else if (method.equalsIgnoreCase(HttpConstant.Method.POST)) {
            RequestBuilder requestBuilder = RequestBuilder.post();
            NameValuePair[] nameValuePair = site.getNameValuePair();
            if (nameValuePair != null && nameValuePair.length > 0) {
                List<NameValuePair> nameValuePairList = new ArrayList<>();
                for (NameValuePair nameValuePair1 : site.getNameValuePair()) {
                    nameValuePairList.add(nameValuePair1);
                }
                try {
                    requestBuilder.setEntity(new UrlEncodedFormEntity(nameValuePairList, site.getCharset()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            if (StringUtils.isNotEmpty(site.getContentType())) {
                if (StringUtils.isNotEmpty(site.getBody())) {
                    try {
                        if (site.isUseBinary()) {
                            requestBuilder.setEntity(new ByteArrayEntity(site.getBody().getBytes(site.getCharset()), ContentType.create(site.getContentType())));
                        } else {
                            requestBuilder.setEntity(new StringEntity(site.getBody(), site.getContentType(), site.getCharset()));
                        }
                    } catch (UnsupportedEncodingException e) {
                        logger.error("不支持此类型", e);
                    }
                } else if (null != site.getBodyBytes()) {
                    requestBuilder.setEntity(new ByteArrayEntity(site.getBodyBytes(), ContentType.create(site.getContentType())));
                }
            } else {
                if (StringUtils.isNotEmpty(site.getBody())) {
                    requestBuilder.setEntity(new StringEntity(site.getBody(), site.getCharset()));
                }
            }
            return requestBuilder;
        } else if (method.equalsIgnoreCase(HttpConstant.Method.HEAD)) {
            return RequestBuilder.head();
        } else if (method.equalsIgnoreCase(HttpConstant.Method.PUT)) {
            return RequestBuilder.put();
        } else if (method.equalsIgnoreCase(HttpConstant.Method.DELETE)) {
            return RequestBuilder.delete();
        } else if (method.equalsIgnoreCase(HttpConstant.Method.TRACE)) {
            return RequestBuilder.trace();
        }
        throw new IllegalArgumentException("Illegal HTTP Method " + method);
    }

    protected String getContent(String charset, HttpResponse httpResponse) throws IOException {
        if (charset == null) {
            byte[] contentBytes = IOUtils.toByteArray(httpResponse.getEntity().getContent());
            String htmlCharset = getHtmlCharset(httpResponse, contentBytes);
            if (htmlCharset != null) {
                return new String(contentBytes, htmlCharset);
            } else {
                logger.warn("Charset autodetect failed, use {} as charset. Please specify charset in Site.setCharset()", Charset.defaultCharset());
                return new String(contentBytes);
            }
        } else {
            return IOUtils.toString(httpResponse.getEntity().getContent(), charset);
        }
    }

    protected String getHtmlCharset(HttpResponse httpResponse, byte[] contentBytes) throws IOException {
        String charset;
        // charset
        // 1、encoding in http header Content-Type
        String value = httpResponse.getEntity().getContentType() == null ? "" : httpResponse.getEntity().getContentType().getValue();
        charset = UrlUtils.getCharset(value);
        if (StringUtils.isNotBlank(charset)) {
            logger.debug("Auto get charset: {}", charset);
            return charset;
        }
        // use default charset to decode first time
        Charset defaultCharset = Charset.defaultCharset();
        String content = new String(contentBytes, defaultCharset.name());
        // 2、charset in meta
        if (StringUtils.isNotEmpty(content)) {
            Document document = Jsoup.parse(content);
            Elements links = document.select("meta");
            for (Element link : links) {
                // 2.1、html4.01 <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
                String metaContent = link.attr("content");
                String metaCharset = link.attr("charset");
                if (metaContent.indexOf("charset") != -1) {
                    metaContent = metaContent.substring(metaContent.indexOf("charset"), metaContent.length());
                    charset = metaContent.split("=")[1];
                    break;
                }
                // 2.2、html5 <meta charset="UTF-8" />
                else if (StringUtils.isNotEmpty(metaCharset)) {
                    charset = metaCharset;
                    break;
                }
            }
        }
        logger.debug("Auto get charset: {}", charset);
        // 3、 use tools as cpdetector for content decode
        return charset;
    }

    public interface IHandleResponse<T> {
        T handleResponse(Request request, String charset, HttpResponse httpResponse) throws Exception;
    }
}
