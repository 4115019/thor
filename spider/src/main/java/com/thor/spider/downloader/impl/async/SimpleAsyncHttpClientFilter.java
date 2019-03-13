package com.thor.spider.downloader.impl.async;

import com.thor.spider.downloader.utils.HttpLogHelper;
import com.thor.spider.downloader.utils.HttpMethod;
import io.netty.handler.codec.http.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.asynchttpclient.Param;
import org.asynchttpclient.Request;
import org.asynchttpclient.filter.FilterContext;
import org.asynchttpclient.filter.FilterException;
import org.asynchttpclient.filter.RequestFilter;
import org.asynchttpclient.filter.ResponseFilter;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Created by huangpin on 17/8/3.
 * asyncHttpClient 请求响应日志过滤器
 */
@Slf4j
public class SimpleAsyncHttpClientFilter{

    //请求日志打印类
    public static class SimpleAsyncHttpClientRequestFilter implements RequestFilter {

        @Override
        public <T> FilterContext<T> filter(FilterContext<T> filterContext) throws FilterException {

            String requestLog = new LogBuilder<T>() {
                @Override
                public String buildLog(HttpMethod.HTTP_METHOD httpMethod, String url, String localIp, Request request,
                                       FilterContext<T> filterContext,
                                       final String requestHeaders, final String queryString, final String requestBody) {
                    return HttpLogHelper.logRequestOnClient(requestHeaders, httpMethod, url, localIp, queryString, requestBody);
                }
            }.getLog(filterContext);

            log.info(requestLog);

            return filterContext;
        }
    }

    //响应日志打印类, 无法打印响应数据和响应时间
    public static class SimpleAsyncHttpClientResponseFilter implements ResponseFilter{

        @Override
        public <T> FilterContext<T> filter(FilterContext<T> filterContext) throws FilterException {

            String responseLog = new LogBuilder<T>() {
                @Override
                public String buildLog(HttpMethod.HTTP_METHOD httpMethod, String url, String localIp, Request request,
                                       FilterContext<T> filterContext,
                                       final String requestHeaders, final String queryString, final String requestBody) {
                    return HttpLogHelper.logResponseOnClient(getHeader(filterContext.getResponseHeaders().getHeaders()),
                            requestHeaders , httpMethod, url, localIp, queryString, requestBody,
                            String.valueOf(filterContext.getResponseStatus().getStatusCode()), 0, 0);
                }
            }.getLog(filterContext);

            log.info(responseLog);

            return filterContext;
        }
    }


    public static abstract class LogBuilder<T>{

        public String getLog(FilterContext<T> filterContext){
            Request request = filterContext.getRequest();

            final HttpMethod.HTTP_METHOD httpMethod = HttpMethod.HTTP_METHOD.valueOf("HTTP_" + request.getMethod().toUpperCase());

            final String url = request.getUrl();

            InetAddress ip = null;
            try {
                ip = InetAddress.getLocalHost();
            } catch (UnknownHostException ignore) {
            }

            return buildLog(httpMethod, url, ip != null ? ip.getHostAddress() : "unknown", request, filterContext,
                    getHeader(request.getHeaders()), getQueryString(request.getQueryParams()), getRequestBody(request));
        }

        protected abstract String buildLog(final HttpMethod.HTTP_METHOD httpMethod, final String url,
                                        final String localIp, final Request request, final FilterContext<T> filterContext,
                                           final String requestHeaders, final String queryString, final String requestBody);

    }


    private static String getHeader(HttpHeaders headers){
        StringBuilder header = new StringBuilder("[");
        if (headers != null && !headers.isEmpty()){
            for(String name : headers.names()){
                header.append(name).append(": ").append(headers.get(name)).append(", ");
            }
        }
        return header.append("]").toString();
    }

    private static String getQueryString(List<Param> queryParams){
        if (CollectionUtils.isEmpty(queryParams)){
            return "[EMPTY]";
        }else {
            return listParamToStr(queryParams);
        }
    }

    private static String getRequestBody(Request request){
        if (StringUtils.isNotEmpty(request.getStringData())){
            return request.getStringData();
        }

        if (CollectionUtils.isNotEmpty(request.getFormParams())){
            return listParamToStr(request.getFormParams());
        }

        if (request.getByteData() != null && request.getByteData().length > 0){
            try {
                return IOUtils.toString(request.getByteData(), request.getCharset().name());
            } catch (IOException ignored) {}
        }

        return "[EMPTY]";
    }

    private static String listParamToStr(List<Param> params){
        StringBuilder header = new StringBuilder();
        for(Param param : params){
            header.append(param.getName()).append("=").append(param.getValue()).append("&");
        }
        return header.substring(0, header.length() - 1);
    }
}
