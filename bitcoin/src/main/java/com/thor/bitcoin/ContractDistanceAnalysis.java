package com.thor.bitcoin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @author huangpin
 * @date 2020-01-14
 */
public class ContractDistanceAnalysis {
    public static void main(String[] args) throws Exception {
        JSONArray data = JSON.parseArray(FileUtil.getFileContent("/Users/hp/Desktop/data/formated_data.json"));
        String benefitBeginTime = "2020-01-30 08:00:00";
        String benefitEndTime = "2020-01-31 08:00:00";

        testBenefit(benefitBeginTime, benefitEndTime, data,
                2, 3, 3, 2, false, true);
    }

    public static double testBenefit(String beginTime, String endTime, JSONArray data,
                                     int buyMorePoint,
                                     int sellMorePoint,
                                     int buyLessPoint,
                                     int sellLessPoint,
                                     boolean analysis,
                                     boolean sout) {
        double lastDistancce = 0;

        boolean haveMore = false;
        int haveMorePoint = 0;
        double firstMoreDistance = 0;
        double haveMoreDistance = 0;
        double morePrice = 0;
        boolean haveLess = false;
        int haveLessPoint = 0;
        double firstLessDistance = 0;
        double haveLessDistance = 0;
        double lessPrice = 0;

        double moreSuccessDistance = 0;
        double moreFailDistance = 0;
        double lessSuccessDistance = 0;
        double lessFailDistance = 0;

        double benefit = 0;
        int tradeTime = 0;
        int moreMore = 0;
        double moreMoreAmount = 0;
        int lessMore = 0;
        double lessMoreAmount = 0;
        int moreLess = 0;
        double moreLessAmount = 0;
        int lessLess = 0;
        double lessLessAmount = 0;


        for (int i = 0; i < data.size(); i++) {
            JSONArray oneData = data.getJSONArray(i);
            if (oneData.getString(0).compareTo(beginTime) >= 0
                    && oneData.getString(0).compareTo(endTime) <= 0) {

                if (oneData.getDoubleValue(9) > lastDistancce) {
                    if (haveMorePoint == 0) {
                        firstMoreDistance = oneData.getDoubleValue(9);
                    }
                    haveMorePoint++;
                    haveLessPoint = 0;
                } else {
                    if (haveLessPoint == 0) {
                        firstLessDistance = oneData.getDoubleValue(9);
                    }
                    haveLessPoint++;
                    haveMorePoint = 0;
                }

                haveMoreDistance = oneData.getDoubleValue(9) - firstMoreDistance;

                if (!haveMore && haveMorePoint > buyMorePoint) {
                    haveMore = true;
                    morePrice = oneData.getDoubleValue(1);
                    System.out.println(String.format("时间：%s,方向：做多，价格：%s", oneData.getString(0), morePrice));

                    tradeTime++;
                }

                haveLessDistance = oneData.getDoubleValue(9) - firstLessDistance;
                if (!haveLess && haveLessPoint > buyLessPoint) {
                    haveLess = true;
                    lessPrice = oneData.getDoubleValue(1);
                    System.out.println(String.format("时间：%s,方向：做空，价格：%s", oneData.getString(0), lessPrice));


                    tradeTime++;
                }

                double thisBenefit = oneData.getDoubleValue(1) - morePrice;

                if (haveMore && (haveLessPoint > sellMorePoint)) {
                    haveMore = false;
                    if (thisBenefit > 0) {
                        moreMore++;
                        moreMoreAmount += thisBenefit;
                        moreSuccessDistance += haveMoreDistance;
                    } else {
                        moreLess++;
                        moreLessAmount -= thisBenefit;
                        moreFailDistance += haveMoreDistance;
                    }
                    benefit += thisBenefit;
                    System.out.println(String.format("时间：%s,方向：平多，价格：%s，收益：%s，distance：%s", oneData.getString(0), oneData.getDoubleValue(1), thisBenefit, haveMoreDistance));
                    tradeTime++;
                }

                thisBenefit = lessPrice - oneData.getDoubleValue(1);
                if (haveLess && (haveMorePoint > sellLessPoint)) {
                    haveLess = false;
                    if (thisBenefit > 0) {
                        lessMore++;
                        lessMoreAmount += thisBenefit;
                        lessSuccessDistance += haveLessDistance;
                    } else {
                        lessLess++;
                        lessLessAmount -= thisBenefit;
                        lessFailDistance += haveLessDistance;
                    }
                    benefit += thisBenefit;
                    System.out.println(String.format("时间：%s,方向：平空，价格：%s，收益：%s，distance：%s", oneData.getString(0), oneData.getDoubleValue(1), thisBenefit, haveLessDistance));
                    tradeTime++;

                }
            }

            lastDistancce = oneData.getDoubleValue(9);
        }

        System.out.println(beginTime + "至" + endTime);
        System.out.println("一共操作：" + tradeTime);
        System.out.println("一共盈利：" + benefit);
        System.out.println("做多盈利次数：" + moreMore);
        System.out.println("做多盈利金额：" + moreMoreAmount);
        System.out.println("做空盈利次数：" + lessMore);
        System.out.println("做空盈利金额：" + lessMoreAmount);
        System.out.println("做多亏损次数：" + moreLess);
        System.out.println("做多亏损金额：" + moreLessAmount);
        System.out.println("做空亏损次数：" + lessLess);
        System.out.println("做空亏损金额：" + lessLessAmount);
        System.out.println(moreMore * 1.0 / moreLess);
        System.out.println(lessMore * 1.0 / lessLess);
        System.out.println((moreMore + lessMore) * 1.0 / (moreMore + moreLess + lessMore + lessLess));

        System.out.println("做多成功平均distance：" + moreSuccessDistance / moreMore / 10000);
        System.out.println("做多失败平均distance：" + moreFailDistance / moreLess / 10000);
        System.out.println("做空成功平均distance：" + lessSuccessDistance / lessMore / 10000);
        System.out.println("做空失败平均distance：" + lessFailDistance / lessLess / 10000);
        return benefit;
    }
}
