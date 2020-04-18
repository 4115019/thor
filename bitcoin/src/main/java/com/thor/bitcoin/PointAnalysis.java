package com.thor.bitcoin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

/**
 * @author huangpin
 * @date 2020-02-20
 */
public class PointAnalysis {
    public static void main(String[] args) throws Exception {
        JSONArray data = JSON.parseArray(FileUtil.getFileContent("/Users/hp/Desktop/data/formated_data.json"));
        String benefitBeginTime = "2020-02-01 00:00:00";
        String benefitEndTime = "2020-02-15 24:00:00";
        analysisPoint(benefitBeginTime, benefitEndTime, data);
    }

    public static void analysisPoint(String startTime, String endTime, JSONArray data) {

        for (int i = 0; i < data.size(); i++) {
            JSONArray oneData = data.getJSONArray(i);
            if (oneData.getString(0).compareTo(startTime) >= 0
                    && oneData.getString(0).compareTo(endTime) <= 0) {

            }
        }
    }
}
