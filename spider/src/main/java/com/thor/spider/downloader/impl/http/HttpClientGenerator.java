package com.thor.spider.downloader.impl.http;

import com.thor.spider.downloader.Site;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.HttpContext;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;


/**
 * @author code4crafter@gmail.com <br>
 * @since 0.4.0
 */
@Slf4j
public class HttpClientGenerator {

    private PoolingHttpClientConnectionManager connectionManager;

    public HttpClientGenerator(){
        this("SSLv3");
    }

    public HttpClientGenerator(final String sslProtocol) {

//        SSLContext sslContext = null;
//        try {
//
//            sslContext = new SSLContextBuilder().loadTrustMaterial(null,
//                    (X509Certificate[] chain, String authType) -> true).build();
//
//        }catch (Exception e){}
//
//        Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory>create()
//                .register("http", PlainConnectionSocketFactory.INSTANCE)
////                .register("https", SSLConnectionSocketFactory.getSocketFactory())
//                .register("https", new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.getDefaultHostnameVerifier()))
//                .build();
//        connectionManager = new PoolingHttpClientConnectionManager(reg);
        // Trust all cert not only self-signed
        try{
            X509TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] arg0,
                                               String arg1) throws CertificateException {
                    // bypass
                }

                public void checkServerTrusted(X509Certificate[] arg0,
                                               String arg1) throws CertificateException {
                    // bypass
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };

            // Set SSLv3 for CMC connection
            SSLContext sslContext = SSLContext.getInstance(sslProtocol);
            sslContext.init(null, new TrustManager[]{tm}, null);

            // Build ssl context
            //SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(new TrustSelfSignedStrategy()).build();

            // Pain socket factory for http
    //            ConnectionSocketFactory plainsf = new PlainConnectionSocketFactory();

            Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE))
                    .build();
            connectionManager = new PoolingHttpClientConnectionManager(reg);
            connectionManager.setDefaultMaxPerRoute(1000);
            connectionManager.setDefaultSocketConfig(SocketConfig.custom().setTcpNoDelay(true).setSoKeepAlive(true).setSoTimeout(30000).build());
            setPoolSize(200);
        } catch (KeyManagementException| NoSuchAlgorithmException e) {
            log.error("HttpClient初始化异常", "发生内部异常", e);
        }
    }

    public HttpClientGenerator setPoolSize(int poolSize) {
        connectionManager.setMaxTotal(poolSize);
        return this;
    }

    public CloseableHttpClient getClient(Site site) {
        return generateClient(site);
    }

    private CloseableHttpClient generateClient(Site site) {
        HttpClientBuilder httpClientBuilder = HttpClients.custom().setConnectionManager(connectionManager);
        if (site != null){
            if (site.isUseHttpRequestLog()){
                httpClientBuilder.addInterceptorFirst(SimpleHttpRequestInterceptor.getInstance());
            }
            if (site.isUseHttpResponseLog()){
                httpClientBuilder.addInterceptorLast(SimpleHttpResponseInterceptor.getInstance());
            }
        }

        if (site != null && site.getUserAgent() != null) {
            httpClientBuilder.setUserAgent(site.getUserAgent());
        }
//        else {
//            httpClientBuilder.setUserAgent("");
//        }
        if (site == null || site.isUseGzip()) {
            httpClientBuilder.addInterceptorFirst(new HttpRequestInterceptor() {

                public void process(
                        final HttpRequest request,
                        final HttpContext context) throws HttpException, IOException {
                    if (!request.containsHeader("Accept-Encoding")) {
                        request.addHeader("Accept-Encoding", "gzip");
                    }

                }
            });
        }

        SocketConfig socketConfig = SocketConfig.custom().setSoKeepAlive(true).setTcpNoDelay(true).setSoTimeout(site != null ? site.getTimeOut() : 10000).build();
        httpClientBuilder.setDefaultSocketConfig(socketConfig);
        if (site != null && site.getRetryTimes() > 0) {
            httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(site.getRetryTimes(), true));
        }
        generateCookie(httpClientBuilder, site);
        setProxy(httpClientBuilder, site);
        return httpClientBuilder.build();
    }

    private void setProxy(HttpClientBuilder httpClientBuilder, Site site) {
        if (site != null && site.getHttpProxy() != null && site.getHttpProxy().validate()) {
            HttpHost httpHost = new HttpHost(site.getHttpProxy().getIp(), site.getHttpProxy().getPort(), site.getHttpProxy().getScheme());
            DefaultProxyRoutePlanner defaultProxyRoutePlanner = new DefaultProxyRoutePlanner(httpHost);
            CredentialsProvider defaultCredentialsProvider = new BasicCredentialsProvider();
            if (StringUtils.isNotEmpty(site.getHttpProxy().getUsername())) {
                defaultCredentialsProvider.setCredentials(
                        new AuthScope(site.getHttpProxy().getIp(), site.getHttpProxy().getPort()),
                        new UsernamePasswordCredentials(site.getHttpProxy().getUsername(), site.getHttpProxy().getPassword())
                );
            }
            httpClientBuilder.setRoutePlanner(defaultProxyRoutePlanner);
            httpClientBuilder.setDefaultCredentialsProvider(defaultCredentialsProvider);
        }
    }

    private void generateCookie(HttpClientBuilder httpClientBuilder, Site site) {
        if (site.getCookieStore() == null) {
            CookieStore cookieStore = new BasicCookieStore();
            for (Map.Entry<String, String> cookieEntry : site.getCookies().entrySet()) {
                BasicClientCookie cookie = new BasicClientCookie(cookieEntry.getKey(), cookieEntry.getValue());
                cookie.setDomain(site.getDomain());
                cookieStore.addCookie(cookie);
            }
            for (Map.Entry<String, Map<String, String>> domainEntry : site.getAllCookies().entrySet()) {
                for (Map.Entry<String, String> cookieEntry : domainEntry.getValue().entrySet()) {
                    BasicClientCookie cookie = new BasicClientCookie(cookieEntry.getKey(), cookieEntry.getValue());
                    cookie.setDomain(domainEntry.getKey());
                    cookieStore.addCookie(cookie);
                }
            }
            httpClientBuilder.setDefaultCookieStore(cookieStore);
        } else {
            httpClientBuilder.setDefaultCookieStore(site.getCookieStore());
        }
    }

}
