package com.thor.prediction.util;

import com.thor.common.utils.FileUtil;

import java.util.List;

/**
 * @author huangpin
 * @date 2019-06-19
 */
public class StatisticUtil {
    public static void main(String[] args) {
        String predictionPath = "data/20190602-20190609-1";
        List<Integer> qpsData = FileUtil.getListIntegerFile(predictionPath);
        Integer totalDiffNum = 0;
        double totalDiff = 0;
        for (int i = 1; i < qpsData.size(); i++) {
            Integer diffNum = qpsData.get(i - 1) - qpsData.get(i);
            double diff = diffNum * 1.0 / qpsData.get(i - 1) * 100;
            if (diffNum > 0) {
                totalDiffNum += diffNum;
                totalDiff += diff;
            } else {
                totalDiffNum -= diffNum;
                totalDiff -= diff;
            }
            System.out.println(String.format("local:%s,diff num:%s,diff:%s", qpsData.get(i - 1), diffNum, diff));
        }
        System.out.println(String.format("total diff num:%s,total diff:%s", (totalDiffNum / qpsData.size() - 1), (totalDiff / qpsData.size() - 1)));
    }
}
