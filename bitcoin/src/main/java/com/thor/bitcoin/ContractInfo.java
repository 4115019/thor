package com.thor.bitcoin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.china.mobile.util.httpclient.HttpClientUtil;
import com.china.mobile.util.httpclient.builder.HCB;
import com.china.mobile.util.httpclient.common.HttpConfig;
import com.china.mobile.util.httpclient.exception.HttpProcessException;

import java.math.BigDecimal;

/**
 * @author huangpin
 * @date 2020-01-07
 */
public class ContractInfo {
    public static void main(String[] args) throws HttpProcessException {
        JSONArray data = JSON.parseObject(HttpClientUtil.get(
                HttpConfig.custom()
                        .url("https://cn.coinfarm.online/index/margin_site.asp")
                        .client(HCB.custom().build())
        )).getJSONArray("data");
        BigDecimal data24_long_total = BigDecimal.ZERO;
        BigDecimal data24_short_total = BigDecimal.ZERO;
        BigDecimal data1h_long_total = BigDecimal.ZERO;
        BigDecimal data1h_short_total = BigDecimal.ZERO;
        for (int i = 0; i < data.size(); i++) {
            JSONObject subData = data.getJSONObject(i);
            String rank = subData.getString("rank");
            String name = subData.getString("site");
            BigDecimal data24_long = new BigDecimal(subData.getString("data24_long"));
            BigDecimal data24_short = new BigDecimal(subData.getString("data24_short"));
            BigDecimal data1h_long = new BigDecimal(subData.getString("data1h_long"));
            BigDecimal data1h_short = new BigDecimal(subData.getString("data1h_short"));
            BigDecimal price = new BigDecimal(subData.getDouble("price"));
            System.out.println(price.doubleValue());

            data24_long_total = data24_long_total.add(data24_long);
            data24_short_total = data24_short_total.add(data24_short);
            data1h_long_total = data1h_long_total.add(data1h_long);
            data1h_short_total = data1h_short_total.add(data1h_short);
            System.out.println(String.format("rank:%s site:%s 24hvalue:%s 1hvalue:%s", rank, name,
                    data24_long.divide(data24_short,3,BigDecimal.ROUND_HALF_UP),
                    data1h_long.divide(data1h_short,3,BigDecimal.ROUND_HALF_UP)));
        }

        System.out.println(String.format("total24HValue:%s,total1HValue:%s", data24_long_total.divide(data24_short_total,3,BigDecimal.ROUND_HALF_UP),
                data1h_long_total.divide(data1h_short_total,3,BigDecimal.ROUND_HALF_UP)));
    }
}
