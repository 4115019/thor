package com.thor.springboot.server.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.thor.springboot.server.exception.WebBasicCodeEnum;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.springframework.http.HttpMethod;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * @author huangpin
 * @date 2018-12-10
 */
public class HttpUtil {

    public static <T> T getModelFromRequest(HttpServletRequest request, Class<T> type) throws Exception {
        T result = null;

        try {
            String requestBody = null;
            if (request.getMethod().equalsIgnoreCase(HttpMethod.POST.name())) {
                if (request.getContentType().startsWith(ContentType.APPLICATION_FORM_URLENCODED.getMimeType())) {
                    Map<String, String[]> parameterMap = request.getParameterMap();
                    JSONObject json = new JSONObject();
                    for (String key : parameterMap.keySet()) {
                        String[] value = parameterMap.get(key);
                        if (value != null && value.length > 0) {
                            json.put(key, parameterMap.get(key)[0]);
                        }
                    }
                    requestBody = json.toJSONString();
                } else {
                    requestBody = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
                    if (StringUtils.isNotEmpty(requestBody) && !requestBody.startsWith("{")) {
                        requestBody = getRequestBody(requestBody);
                    }
                }
            } else if (request.getMethod().equalsIgnoreCase(HttpMethod.GET.name())) {
                requestBody = getRequestBody(URLDecoder.decode(request.getQueryString(), request.getCharacterEncoding()));
            }

            result = JSON.parseObject(requestBody, type);
        } catch (Exception e) {
            throw e;
        }

        ValidUtil.validate(result);

        return result;
    }

    public static String getRequestBody(String requestBody) throws Exception {
        if (StringUtils.isEmpty(requestBody)) {
            throw new Exception("解析异常");
        }
        if (RegexpUtil.getMatcher(requestBody, "(req|param)=\\{(.+:.+)+\\}").find()) {
            return requestBody.replaceFirst("(req|param)=", "");
        }

        JSONObject requestBodyJSON = new JSONObject();
        Matcher matcher = RegexpUtil.getMatcher(requestBody, "(([a-zA-Z0-9_]+)=([^&]+))&?+");
        while (matcher.find()) {
            String key = matcher.group(2);
            String value = matcher.group(3);
            if (value.startsWith("[") && value.endsWith("]")) {
                requestBodyJSON.put(key, JSONArray.parseArray(value));
            } else if (value.startsWith("{") && value.endsWith("}")) {
                requestBodyJSON.put(key, JSONObject.parseObject(value));
            } else {
                requestBodyJSON.put(key, value);
            }
        }
        return JSON.toJSONString(requestBodyJSON);
    }

    public static void main(String[] args) {
        System.out.println(HttpMethod.POST.name());
    }
}
