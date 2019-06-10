package com.china.mobile;

import com.china.mobile.util.httpclient.HttpClientUtil;
import com.china.mobile.util.httpclient.builder.HCB;
import com.china.mobile.util.httpclient.common.HttpConfig;
import com.china.mobile.util.httpclient.common.HttpHeader;
import com.china.mobile.util.httpclient.exception.HttpProcessException;
import org.apache.http.Header;

/**
 * @author huangpin
 * @date 2019-05-24
 */
public class TestOrigin {
    public static void main(String[] args) throws HttpProcessException {
        String url = "http://10.43.144.135:8080/search?key=5818d350-6e56-4a72-b401-782fd3556318&radius=1000";
        Header[] headers 	= HttpHeader.custom()
                .other("origin", "www.sohu.com")
                .other("Origin", "www.sohu.com")
                .build();
        String html = HttpClientUtil.get(HttpConfig.custom().headers(headers).url(url).client(HCB.custom().build()));
        System.out.println(html);
    }
}
