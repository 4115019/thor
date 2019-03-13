package com.thor.spider.downloader.impl.async;

import com.alibaba.fastjson.JSONObject;
import com.thor.spider.downloader.Page;
import com.thor.spider.downloader.Request;
import com.thor.spider.downloader.Site;
import com.thor.spider.downloader.Task;
import com.thor.spider.downloader.api.Downloader;
import com.thor.spider.downloader.api.DownloaderHandler;
import com.thor.spider.downloader.proxy.HttpProxy;
import com.thor.spider.downloader.utils.HttpConstant;
import com.thor.spider.exception.BaseException;
import com.thor.spider.exception.BusinessException;
import com.thor.spider.exception.WebBasicCodeEnum;
import io.netty.channel.ConnectTimeoutException;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.JdkSslContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.asynchttpclient.*;
import org.asynchttpclient.netty.ssl.JsseSslEngineFactory;
import org.asynchttpclient.proxy.ProxyServer;

import javax.net.ssl.*;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

/**
 * Created by huangpin on 17/7/27.
 */
@Slf4j
public class AsyncHttpClientDownloader implements Downloader {
    private volatile AsyncHttpClient asyncHttpClient = null;

    public final static AsyncHttpClientDownloader instance = new AsyncHttpClientDownloader();

    @Override
    public Page download(Request request, Site site, Task task) {
        throw new UnsupportedOperationException("asynchttpclient not supported sync download");
    }

    @Override
    public Page download(Request request, Site site) {
        throw new UnsupportedOperationException("asynchttpclient not supported sync download");
    }

    @Override
    public void setThread(int threadNum) {
        throw new UnsupportedOperationException("asynchttpclient not supported set thread num");
    }

    @Override
    public Future<Page> download(Request request, Site site, DownloaderHandler handler) {
        Set<Integer> acceptStatCode;

        acceptStatCode = site.getAcceptStatCode();

        log.info("async downloading page {}, timeout {}", request.getUrl(), site.getTimeOut());

        return getClient().prepareRequest(buildRequest(request, site)).execute(new AsyncCompletionHandler<Page>() {
            @Override
            public State onStatusReceived(HttpResponseStatus status) throws Exception {
                handler.handleReceivedStatus(status.getStatusCode());

                State state = super.onStatusReceived(status);
                if (!statusAccept(acceptStatCode, status.getStatusCode())){
                    return State.ABORT;
                }
                return state;
            }

            @Override
            public Page onCompleted(Response response) throws Exception {
                log.info("success downloading page {}", request.getUrl());

                //deal response header
                addResponseCookie(response.getCookies(), site.getCookieStore());

                //deal response page
                Page page = new Page();
                page.setRequest(request);
                page.setStatusCode(response.getStatusCode());
                page.setRawText(response.getResponseBody(Charset.forName(getCharset(site))));
                handler.handlePage(page);

                return page;
            }

            @Override
            public void onThrowable(Throwable t) {
                log.info("exception downloading page {}", request.getUrl(), t);
                if (t instanceof BaseException) {
                    handler.handleThrowable((BaseException) t);
                }else if (t instanceof ConnectTimeoutException || t instanceof TimeoutException){
                    handler.handleThrowable(new BusinessException(WebBasicCodeEnum.RESPONSE_TIME_OUT, t));
                }else {
                    handler.handleThrowable(new BusinessException(WebBasicCodeEnum.DOWNLOAD_ERROR, t));
                }
            }
        });
    }




    private AsyncHttpClient getClient(){
        if (asyncHttpClient == null){
            synchronized (this){
                if (asyncHttpClient == null){
                    asyncHttpClient = createClient();
                }
            }
        }
        return asyncHttpClient;
    }


    private AsyncHttpClient createClient(){
        DefaultAsyncHttpClientConfig.Builder configBuilder = new DefaultAsyncHttpClientConfig.Builder();

        X509TrustManager tm = new X509ExtendedTrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException {

            }

            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) throws CertificateException {

            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0,
                                           String arg1) throws CertificateException {
                // bypass
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0,
                                           String arg1) {
                // bypass
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        // Set SSLv3 for CMC connection
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSLv3");
            sslContext.init(null, new TrustManager[]{tm}, null);

            configBuilder.setSslContext(new JdkSslContext(sslContext, true, ClientAuth.NONE));
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            log.error("AsyncHttpClient初始化异常", "发生内部异常", e);
        }

        configBuilder.setSslEngineFactory(new JsseSslEngineFactory(sslContext));

        configBuilder.addRequestFilter(new SimpleAsyncHttpClientFilter.SimpleAsyncHttpClientRequestFilter());

        configBuilder.addResponseFilter(new SimpleAsyncHttpClientFilter.SimpleAsyncHttpClientResponseFilter());

        return new DefaultAsyncHttpClient(configBuilder.build());
    }

    private void addResponseCookie(List<org.asynchttpclient.cookie.Cookie> cookies, CookieStore cookieStore){
        if (cookieStore != null && CollectionUtils.isNotEmpty(cookies)){
            for(org.asynchttpclient.cookie.Cookie cookie : cookies){
                BasicClientCookie basicClientCookie = new BasicClientCookie(cookie.getName(), cookie.getValue());
                basicClientCookie.setDomain(cookie.getDomain());
                basicClientCookie.setPath(cookie.getPath());
                basicClientCookie.setExpiryDate(new Date(System.currentTimeMillis() + cookie.getMaxAge()));
                basicClientCookie.setSecure(cookie.isSecure());
                cookieStore.addCookie(basicClientCookie);
            }
        }
    }

    private boolean statusAccept(Set<Integer> acceptStatCode, int statusCode) {
        return acceptStatCode.contains(statusCode);
    }

    private org.asynchttpclient.Request buildRequest(Request request, Site site){
        RequestBuilder requestBuilder = new RequestBuilder(request.getMethod());

        requestBuilder.setUrl(request.getUrl());

        requestBuilder.setRequestTimeout(site.getTimeOut());

        requestBuilder.setFollowRedirect(true);

        //set charset
        requestBuilder.setCharset(Charset.forName(getCharset(site)));

        //set proxy
        HttpProxy httpProxy = site.getHttpProxy();
        if (httpProxy != null && httpProxy.validate()){
            log.info("async downloading page {}, 使用代理 proxy = {}", request.getUrl(), JSONObject.toJSONString(httpProxy));
            request.putExtra(Request.PROXY, httpProxy);
            site.getHttpProxy().limit();
            requestBuilder.setProxyServer(buildProxyServer(httpProxy));
        }

        //set header
        addHeader(requestBuilder, site);

        //add post body
        if (HttpConstant.Method.POST.equalsIgnoreCase(request.getMethod())){
            addPostBody(requestBuilder, site);
        }

        return requestBuilder.build();
    }

    private void addPostBody(RequestBuilder requestBuilder, Site site){
        NameValuePair[] nameValuePairs = site.getNameValuePair();
        if (nameValuePairs != null && nameValuePairs.length > 0){
            for(NameValuePair nameValuePair : nameValuePairs){
                requestBuilder.addFormParam(nameValuePair.getName(), nameValuePair.getValue());
            }
            setContentType(requestBuilder, ContentType.APPLICATION_FORM_URLENCODED.getMimeType());
        }else {
            String body = site.getBody() == null ? "" : site.getBody();
            if (site.isUseBinary()){
                try {
                    if (StringUtils.isNotEmpty(body)) {
                        requestBuilder.setBody(body.getBytes(getCharset(site)));
                    }else {
                        requestBuilder.setBody(site.getBodyBytes());
                    }
                } catch (UnsupportedEncodingException e) {
                    log.error("UnsupportedEncodingException", e);
                }
            }else {
                requestBuilder.setBody(body);
            }
        }
    }

    private void addHeader(RequestBuilder requestBuilder, Site site){
        if (site.getHeaders() != null){
            for(String name : site.getHeaders().keySet()){
                requestBuilder.addHeader(name, site.getHeaders().get(name));
            }
        }

        if (StringUtils.isNotEmpty(site.getUserAgent())) {
            requestBuilder.setHeader("User-Agent", site.getUserAgent());
        }

        addCookie(requestBuilder, site);

        setContentType(requestBuilder, site.getContentType());
    }

    private void addCookie(RequestBuilder requestBuilder, Site site){
        StringBuilder cookieStr = new StringBuilder();
        if (site.getCookieStore() != null){
            for(Cookie cookie : site.getCookieStore().getCookies()){
                cookieStr.append(cookie.getName()).append("=").append(cookie.getValue()).append("; ");
            }
        }else if (site.getCookies() != null && site.getCookies().size() > 0){
            for(String name : site.getCookies().keySet()){
                cookieStr.append(name).append("=").append(site.getCookies().get(name)).append("; ");
            }
        }

        if (cookieStr.length() > 0) {
            requestBuilder.addHeader("Cookie", cookieStr.toString());
        }
    }

    private void setContentType(RequestBuilder requestBuilder, String contentType){
        if (StringUtils.isNotEmpty(contentType)) {
            requestBuilder.setHeader("Content-Type", contentType);
        }
    }

    private ProxyServer buildProxyServer(HttpProxy httpProxy){
        ProxyServer.Builder builder = new ProxyServer.Builder(httpProxy.getIp(), httpProxy.getPort());
        if (httpProxy.needRealm()){
            Realm.Builder realmBuilder = new Realm.Builder(httpProxy.getUsername(), httpProxy.getPassword());
            realmBuilder.setScheme(Realm.AuthScheme.BASIC);
            builder.setRealm(realmBuilder.build());
        }
        return builder.build();
    }

    private String getCharset(Site site){
        return StringUtils.isEmpty(site.getCharset()) ? "UTF-8": site.getCharset();
    }
}
