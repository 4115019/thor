package com.thor.bitcoin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @author huangpin
 * @date 2020-01-14
 */
public class ContractAmountAnalysis {
    public static void main(String[] args) throws Exception {
        JSONArray data = JSON.parseArray(FileUtil.getFileContent("/Users/hp/Desktop/data/formated_data.json"));
        String benefitBeginTime = "2020-01-16 00:00:00";
        String benefitEndTime = "2020-01-16 24:00:00";

//        testBestPoint(benefitBeginTime, benefitEndTime, data);
        testBenefit(benefitBeginTime, benefitEndTime, data, 2, 3, 3, 2, false);
    }

    public static void testBestPoint(String beginTime, String endTime, JSONArray data) {
        double bestBenefit = 0;
        int buyMorePoint = 0;
        int sellMorePoint = 0;
        int buyLessPoint = 0;
        int sellLessPoint = 0;
        for (int i = 1; i < 10; i++) {
            for (int j = 1; j < 10; j++) {
                for (int k = 1; k < 10; k++) {
                    for (int l = 1; l < 10; l++) {
                        double benefit = testBenefit(beginTime, endTime, data,
                                i, j, k, l, false);
                        if (benefit > bestBenefit) {
                            bestBenefit = benefit;
                            buyMorePoint = i;
                            sellMorePoint = j;
                            buyLessPoint = k;
                            sellLessPoint = l;
                        }
                    }
                }
            }
        }
        System.out.println(bestBenefit);
        System.out.println(buyMorePoint);
        System.out.println(sellMorePoint);
        System.out.println(buyLessPoint);
        System.out.println(sellLessPoint);
    }

    public static double testBenefit(String beginTime, String endTime, JSONArray data,
                                     int buyMorePoint,
                                     int sellMorePoint,
                                     int buyLessPoint,
                                     int sellLessPoint,
                                     boolean analysis) {
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

        JSONArray tradeDetail = new JSONArray();
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

//                if (haveMore && haveLessPoint == buyLessPoint && oneData.getDoubleValue(1) < morePrice) {
////                    haveMore = true;
//                    morePrice = (morePrice + oneData.getDoubleValue(1)) / 2;
//                }

                if (!haveMore && haveMorePoint > buyMorePoint) {
                    haveMore = true;
                    morePrice = oneData.getDoubleValue(1);
                    haveMoreDistance = oneData.getDoubleValue(9) - firstMoreDistance;
                    System.out.println(String.format("时间：%s,方向：做多，价格：%s", oneData.getString(0), morePrice));

                    JSONObject subDetail = new JSONObject();
                    subDetail.put("type", 1);
                    subDetail.put("index", i);
                    tradeDetail.add(subDetail);

                    tradeTime++;
                }

//                if (haveLess && haveLessPoint == buyMorePoint && oneData.getDoubleValue(1) > lessLess){
//                    lessPrice = (lessPrice + oneData.getDoubleValue(1))/2;
//                }

                if (!haveLess && haveLessPoint > buyLessPoint) {
                    haveLess = true;
                    lessPrice = oneData.getDoubleValue(1);
                    haveLessDistance = oneData.getDoubleValue(9) - firstLessDistance;
                    System.out.println(String.format("时间：%s,方向：做空，价格：%s", oneData.getString(0), lessPrice));

                    JSONObject subDetail = new JSONObject();
                    subDetail.put("type", -1);
                    subDetail.put("index", i);
                    tradeDetail.add(subDetail);

                    tradeTime++;
                }

                if (haveMore && haveLessPoint > sellMorePoint) {
                    haveMore = false;
                    double thisBenefit = oneData.getDoubleValue(1) - morePrice;
                    if (thisBenefit > 0) {
                        moreMore++;
                        moreMoreAmount += thisBenefit;
                    } else {
                        moreLess++;
                        moreLessAmount -= thisBenefit;
                    }
                    benefit += thisBenefit;
                    System.out.println(String.format("时间：%s,方向：平多，价格：%s，收益：%s，distance：%s", oneData.getString(0), oneData.getDoubleValue(1), thisBenefit, haveMoreDistance));
                    JSONObject subDetail = new JSONObject();
                    subDetail.put("type", 2);
                    subDetail.put("index", i);
                    tradeDetail.add(subDetail);
                    tradeTime++;
                }

                if (haveLess && haveMorePoint > sellLessPoint) {
                    haveLess = false;
                    double thisBenefit = lessPrice - oneData.getDoubleValue(1);
                    if (thisBenefit > 0) {
                        lessMore++;
                        lessMoreAmount += thisBenefit;
                    } else {
                        lessLess++;
                        lessLessAmount -= thisBenefit;
                    }
                    benefit += thisBenefit;
                    System.out.println(String.format("时间：%s,方向：平空，价格：%s，收益：%s，distance：%s", oneData.getString(0), oneData.getDoubleValue(1), thisBenefit, haveLessDistance));
                    JSONObject subDetail = new JSONObject();
                    subDetail.put("type", -2);
                    subDetail.put("index", i);
                    tradeDetail.add(subDetail);
                    tradeTime++;
                }
            }

            lastDistancce = oneData.getDoubleValue(9);
        }

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

        if (analysis) {
            System.out.println("交易明细分析开始》》》》》》》》》》》》》》》》》》》》》》》》》》》》》》》");
            analysisTrade(tradeDetail, data);
            System.out.println("交易明细分析结束《《《《《《《《《《《《《《《《《《《《《《《《《《《《《《《");
        }
        return benefit;
    }

    public static void analysisTrade(JSONArray tradeDetail, JSONArray data) {
        double totalLessBenefit = 0;
        int time = 0;
        for (int i = 0; i < tradeDetail.size(); i++) {
            JSONObject trade = tradeDetail.getJSONObject(i);
            int type = trade.getIntValue("type");
            if (type == 1 || type == -1) {
                for (int j = i + 1; j < tradeDetail.size(); j++) {
                    JSONObject tradeComplete = tradeDetail.getJSONObject(j);
                    int typeComplete = tradeComplete.getIntValue("type");
                    if ((type == 1 && typeComplete == 2)
                            || (type == -1 && typeComplete == -2)) {
                        totalLessBenefit += analysisTwoTrade(trade.getIntValue("index"), tradeComplete.getIntValue("index"), data, type);
                        time++;
                        break;
                    }
                }
            }
        }

        System.out.println("平均少收益：" + totalLessBenefit / time);
        System.out.println("有效交易次数：" + time);
        System.out.println("总共少收益：" + totalLessBenefit);
    }

    public static double analysisTwoTrade(int begin, int end, JSONArray data, int type) {
        System.out.println("单次分析开始>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        double price = 0;

        double lessBenefit = 0;
        double benefit = 0;
        for (int i = begin; i < data.size(); i++) {
            lessBenefit = 0;
            JSONArray oneData = data.getJSONArray(i);
            if (i == begin) {
                price = oneData.getDoubleValue(1);
                System.out.println(String.format("时间：%s,方向：%s，价格：%s", oneData.getString(0),
                        type == 1 ? "做多" : "做空",
                        price));
            }
            if (i < end) {
                String index = oneData.getDoubleValue(9) > data.getJSONArray(i - 1).getDoubleValue(9) ? "⬆️" : "⤵️";
                System.out.println(String.format("时间：%s，价格：%s，收益：%s，distance：%s",
                        oneData.getString(0),
                        oneData.getDoubleValue(1),
                        type == 1 ? oneData.getDoubleValue(1) - price : price - oneData.getDoubleValue(1),
                        index));
            }

            if (i == end) {
                benefit = type == 1 ? oneData.getDoubleValue(1) - price : price - oneData.getDoubleValue(1);
                System.out.println(String.format("时间：%s,方向：%s，开仓价格：%s，平仓价格：%s，收益：%s",
                        oneData.getString(0),
                        type == 1 ? "做多" : "做空",
                        price,
                        oneData.getDoubleValue(1),
                        benefit));
            }

            if (i > end && benefit < 0) {
                lessBenefit += ((type == 1 ?
                        oneData.getDoubleValue(1) - price
                        : price - oneData.getDoubleValue(1)) - benefit);
            }

            if (i - end > 2) {
                System.out.println(String.format("平均少收益：%s", lessBenefit / 2));
                break;
            }
        }
        System.out.println("单次分析开始<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        return lessBenefit;
    }
}
