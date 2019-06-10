package com.china.mobile;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author huangpin
 * @date 2019-04-11
 */
public class HttpUtil {

    public static String post(String url, byte[] postData, String cookie) {
        URL u;
        HttpURLConnection con = null;
        InputStream inputStream;
        String result = null;
        //尝试发送请求
        try {
            u = new URL(url);
            con = (HttpURLConnection) u.openConnection();
            con.setConnectTimeout(3000);
            con.setReadTimeout(3000);
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setUseCaches(false);
            con.addRequestProperty("Accept", "*/*");
            con.addRequestProperty("Accept-Language", "zh-CN");
            con.addRequestProperty("Referer", "http://zhengzhou.crm.ha.cmcc:37100/webframe/shdesktopui/WebAppFrameSetNew.jsp#");
            con.addRequestProperty("Content-Type", "multipart/form-data");
            con.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729)");
            con.addRequestProperty("Host", "zhengzhou.crm.ha.cmcc:37100");
            con.addRequestProperty("Pragma", "no-cache");
            con.addRequestProperty("Cookie", cookie);
            OutputStream outStream = con.getOutputStream();
            outStream.write(postData);
            outStream.flush();
            outStream.close();
            //读取返回内容
            inputStream = con.getInputStream();
            result = IOUtils.toString(inputStream, "gbk");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
            return result;
        }
    }
}