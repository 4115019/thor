package com.thor.bitcoin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

/**
 * @author huangpin
 * @date 2020-02-18
 */
public class NewTrade {
    public static void main(String[] args) throws Exception {
        JSONArray data = JSON.parseArray(FileUtil.getFileContent("/Users/hp/Desktop/data/formated_data.json"));
        String benefitBeginTime = "2020-02-01 00:00:00";
        String benefitEndTime = "2020-02-15 24:00:00";

//        doBestTrade(benefitBeginTime, benefitEndTime, data);
        trade(benefitBeginTime, benefitEndTime, data,
                3, 3, 3, 3,
                4, 6, false);
    }

    public static void doBestTrade(String beginTime, String endTime, JSONArray data) {
        double benefit = 0;
        int buyMorePoint = 0;
        int sellMorePoint = 0;
        int buyLessPoint = 0;
        int sellLessPoint = 0;
        int moreMorePoint = 0;
        int lessLessPoint = 0;

        for (int i = 1; i < 8; i++) {
            for (int j = 1; j < 8; j++) {
                for (int k = 1; k < 8; k++) {
                    for (int l = 1; l < 8; l++) {
//            for (int m = 1; m < 8; m++) {
//                for (int n = 1; n < 8; n++) {
                        double thisBenefit = trade(beginTime, endTime, data, i, i, i, i, 2, 6, false);
                        if (thisBenefit > benefit) {
                            benefit = thisBenefit;
                            buyMorePoint = i;
                            sellMorePoint = j;
                            buyLessPoint = k;
                            sellLessPoint = l;
                            moreMorePoint = 2;
                            lessLessPoint = 6;
                        }
//                }
//            }
                    }
                }
            }
        }

        System.out.println("最优方案");
        System.out.println(benefit);
        System.out.println(buyMorePoint);
        System.out.println(sellMorePoint);
        System.out.println(buyLessPoint);
        System.out.println(sellLessPoint);
        System.out.println(moreMorePoint);
        System.out.println(lessLessPoint);
    }

    public static double trade(String beginTime, String endTime, JSONArray data,
                               int buyMorePoint,
                               int sellMorePoint,
                               int buyLessPoint,
                               int sellLessPoint,
                               int moreMorePoint,
                               int lessLessPoint,
                               boolean detail) {
        double lastDistance = 0;

        double lastAmount = 0;
        int haveMoreMorePoint = 0;
        int haveLessLessPoint = 0;

        double last1HAmount = 0;
        int haveMoreMore1HPoint = 0;
        int haveLessLess1HPoint = 0;

        boolean haveMore = false;
        int haveMorePoint = 0;
        double morePrice = 0;

        boolean haveLess = false;
        int haveLessPoint = 0;
        double lessPrice = 0;

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

                double thisAmount = oneData.getDoubleValue(5) + oneData.getDoubleValue(6);
                if (thisAmount > lastAmount) {
                    haveMoreMorePoint++;
                    haveLessLessPoint = 0;
                } else {
                    haveLessLessPoint++;
                    haveMoreMorePoint = 0;
                }

                double this1HAmount = oneData.getDoubleValue(7) + oneData.getDoubleValue(8);
                if (this1HAmount > last1HAmount) {
                    haveMoreMore1HPoint++;
                    haveLessLess1HPoint = 0;
                } else {
                    haveLessLess1HPoint++;
                    haveMoreMore1HPoint = 0;
                }


                if (oneData.getDoubleValue(9) > lastDistance) {
                    haveMorePoint++;
                    haveLessPoint = 0;
                } else {
                    haveLessPoint++;
                    haveMorePoint = 0;
                }

                System.out.println(String.format("%s,haveMorePoint:%s,haveLessPoint:%s,haveMore1HPoint:%s,haveLess1HPoint:%s,haveMoreMorePoint:%s,haveLessLessPoint:%s",
                        oneData.getString(0), haveMorePoint, haveLessPoint, haveMoreMore1HPoint, haveLessLess1HPoint, haveMoreMorePoint, haveLessLessPoint));

                /**
                 * 做多追踪
                 */
                if (detail && haveMore) {
                    double thisBenefit = oneData.getDoubleValue(1) - morePrice;
                    if (haveMorePoint > buyMorePoint && haveMoreMorePoint > moreMorePoint) {
                        System.out.println(String.format("做多确认，时间：%s,方向：做多，收益：%s", oneData.getString(0), thisBenefit));
                    } else {
                        System.out.println(String.format("跟踪，时间：%s,方向：做多，收益：%s", oneData.getString(0), thisBenefit));
                    }
                }

                if (!haveMore && haveMorePoint > buyMorePoint && haveMoreMorePoint > moreMorePoint) {
                    haveMore = true;
                    morePrice = oneData.getDoubleValue(1);
                    if (detail) {
                        System.out.println(String.format("时间：%s,方向：做多，价格：%s", oneData.getString(0), morePrice));
                    }
                    tradeTime++;
                }

                if (detail && haveLess) {
                    double thisBenefit = lessPrice - oneData.getDoubleValue(1);
                    if (haveLessPoint > buyLessPoint && haveMoreMorePoint > moreMorePoint) {
                        System.out.println(String.format("做空确认，时间：%s,方向：做空，收益：%s", oneData.getString(0), thisBenefit));
                    } else {
                        System.out.println(String.format("跟踪，时间：%s,方向：做空，收益：%s", oneData.getString(0), thisBenefit));
                    }
                }

                if (!haveLess && haveLessPoint > buyLessPoint && haveMoreMorePoint > moreMorePoint) {
                    haveLess = true;
                    lessPrice = oneData.getDoubleValue(1);
                    if (detail) {
                        System.out.println(String.format("时间：%s,方向：做空，价格：%s", oneData.getString(0), lessPrice));
                    }
                    tradeTime++;
                }

                double thisBenefit = oneData.getDoubleValue(1) - morePrice;
                if (haveMore && (haveLessPoint > sellMorePoint || haveLessLessPoint > lessLessPoint)) {
                    haveMore = false;
                    if (thisBenefit > 0) {
                        moreMore++;
                        moreMoreAmount += thisBenefit;
                    } else {
                        moreLess++;
                        moreLessAmount -= thisBenefit;
                    }
                    benefit += thisBenefit;

                    if (detail) {
                        System.out.println(String.format("时间：%s,方向：平多，价格：%s，收益：%s", oneData.getString(0), oneData.getDoubleValue(1), thisBenefit));
                    }
                    tradeTime++;
                }

                thisBenefit = lessPrice - oneData.getDoubleValue(1);
                if (haveLess && (haveMorePoint > sellLessPoint || haveLessLessPoint > lessLessPoint)) {
                    haveLess = false;

                    if (thisBenefit > 0) {
                        lessMore++;
                        lessMoreAmount += thisBenefit;
                    } else {
                        lessLess++;
                        lessLessAmount -= thisBenefit;
                    }
                    benefit += thisBenefit;
                    if (detail) {
                        System.out.println(String.format("时间：%s,方向：平空，价格：%s，收益：%s", oneData.getString(0), oneData.getDoubleValue(1), thisBenefit));
                    }
                    tradeTime++;

                }
            }

            lastDistance = oneData.getDoubleValue(9);
            lastAmount = oneData.getDoubleValue(5) + oneData.getDoubleValue(6);
            last1HAmount = oneData.getDoubleValue(7) + oneData.getDoubleValue(8);
        }

        double rate = (moreMore + lessMore) * 1.0 / (moreMore + moreLess + lessMore + lessLess);
        if (detail) {
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
            System.out.println("成功做到比例：" + moreMore * 1.0 / moreLess);
            System.out.println("成功做空比例：" + lessMore * 1.0 / lessLess);
            System.out.println("总胜率：" + rate);
        }

//        if (rate < 0.5) {
//            return 0;
//        }
        return benefit;
    }
}
