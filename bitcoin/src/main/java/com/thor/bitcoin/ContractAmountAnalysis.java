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
        String benefitBeginTime = "2020-02-01 00:00:00";
        String benefitEndTime = "2020-02-18 24:00:00";

//        JSONArray detail = new JSONArray();
//        for (int i = 1; i < 14; i++) {
//            String begin = "2020-02-0" + i + " 00:00:00";
//            String end = "2020-02-0" + i + " 24:00:00";
//            double benefit = testBenefit(begin, end, data, 2, 3, 3, 2, false, false);
//            JSONObject subDetail = new JSONObject();
//            subDetail.put("begin",begin);
//            subDetail.put("end",end);
//            subDetail.put("benefit",benefit);
//            detail.add(subDetail);
//        }
//        System.out.println(detail.toJSONString());

//        testBestPoint(benefitBeginTime, benefitEndTime, data);
//        testIndexLimit(benefitBeginTime, benefitEndTime, data);
//        testAll(benefitBeginTime, benefitEndTime, data);
        testBenefit(benefitBeginTime, benefitEndTime, data,
                2, 3, 3, 2, 0, false, true);
    }

    public static void testAll(String beginTime, String endTime, JSONArray data) {
        double bestBenefit = 0;
        int buyMorePoint = 0;
        int sellMorePoint = 0;
        int buyLessPoint = 0;
        int sellLessPoint = 0;
        int indexLimit = 0;
        for (int i = 1; i < 6; i++) {
            for (int j = 1; j < 6; j++) {
                for (int k = 1; k < 6; k++) {
                    for (int l = 1; l < 6; l++) {
                        for (int m = 0; m < 10; m++) {
                            double benefit = testBenefit(beginTime, endTime, data,
                                    i, j, k, l, m, false, false);
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
        }
        System.out.println(bestBenefit);
        System.out.println(buyMorePoint);
        System.out.println(sellMorePoint);
        System.out.println(buyLessPoint);
        System.out.println(sellLessPoint);
        System.out.println(indexLimit);
    }

    public static void testIndexLimit(String beginTime, String endTime, JSONArray data) {
        double bestBenefit = 0;
        double indexLimit = 0;
        for (int i = 1; i < 10; i++) {

            double benefit = testBenefit(beginTime, endTime, data,
                    4, 4, 4, 4, i, false, false);
            if (benefit > bestBenefit) {
                bestBenefit = benefit;
                indexLimit = i;
            }
            System.out.println(bestBenefit);
            System.out.println(indexLimit);
        }
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
                                i, j, k, l, 0, false, false);
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
                                     int indexLimit,
                                     boolean analysis,
                                     boolean sout) {
        double lastDistancce = 0;
        double last1HDistance = 0;

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

        int haveMoreMoreBenefitPoint = 0;
        int haveLessMoreBenefitPoint = 0;
        double lastMoreBenefit = 0;
        double lastLessBenefit = 0;

        boolean haveMorePlus = false;
        boolean haveLessPlus = false;
        double morePlusPrice = 0;
        double lessPlusPrice = 0;
        double morePlusBenefit = 0;
        double lessPlusBenefit = 0;
        int morePlusTradeTime = 0;
        int lessPlusTradeTime = 0;

        String moreInfo = "";
        String lessInfo = "";

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

                double index = oneData.getDoubleValue(10) / last1HDistance;
                if (index < 0) {
                    index *= -1;
                }

                if (sout && haveMore) {
                    double thisBenefit = oneData.getDoubleValue(1) - morePrice;
                    if (haveMorePoint > buyMorePoint) {
                        System.out.println(String.format("做多确认，时间：%s,方向：做多，收益：%s", oneData.getString(0), thisBenefit));
                    } else {
                        System.out.println(String.format("跟踪，时间：%s,方向：做多，收益：%s", oneData.getString(0), thisBenefit));
                    }
                    if (thisBenefit > lastMoreBenefit) {
                        haveMoreMoreBenefitPoint++;
                    } else {
                        haveMoreMoreBenefitPoint = 0;
                    }
                    lastMoreBenefit = thisBenefit;

                    if (haveMoreMoreBenefitPoint > 2) {
                        System.out.println("干他妈的");
                        if (!haveMorePlus) {
                            morePlusPrice = oneData.getDoubleValue(1);
                            morePlusTradeTime++;
                            haveMorePlus = true;
                        }
                    }
                }

                if (!haveMore && haveMorePoint > buyMorePoint && index > indexLimit) {
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

                if (sout && haveLess) {
                    double thisBenefit = lessPrice - oneData.getDoubleValue(1);
                    if (haveLessPoint > buyLessPoint) {
                        System.out.println(String.format("做空确认，时间：%s,方向：做空，收益：%s", oneData.getString(0), thisBenefit));
                    } else {
                        System.out.println(String.format("跟踪，时间：%s,方向：做空，收益：%s", oneData.getString(0), thisBenefit));
                    }

                    if (thisBenefit > lastLessBenefit) {
                        haveLessMoreBenefitPoint++;
                    } else {
                        haveLessMoreBenefitPoint = 0;
                    }

                    lastLessBenefit = thisBenefit;
                    if (haveLessMoreBenefitPoint > 2) {
                        System.out.println("干他妈的");
                        if (!haveLessPlus) {
                            lessPlusPrice = oneData.getDoubleValue(1);
                            lessPlusTradeTime++;
                            haveLessPlus = true;
                        }
                    }
                }

                if (!haveLess && haveLessPoint > buyLessPoint && index > indexLimit) {
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

                double thisBenefit = oneData.getDoubleValue(1) - morePrice;
                if (haveMore && haveLessPoint > sellMorePoint) {
                    if (haveMorePlus) {
                        haveMorePlus = false;
                        morePlusTradeTime++;
                        morePlusBenefit += oneData.getDoubleValue(1) - morePlusPrice;
                    }
                    haveMore = false;

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

                thisBenefit = lessPrice - oneData.getDoubleValue(1);
                if (haveLess && haveMorePoint > sellLessPoint) {
                    if (haveLessPlus) {
                        haveLessPlus = false;
                        lessPlusTradeTime++;
                        lessPlusBenefit += lessPlusPrice - oneData.getDoubleValue(1);
                    }
                    haveLess = false;

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
            last1HDistance = oneData.getDoubleValue(10);
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

        System.out.println(morePlusBenefit + lessPlusBenefit);
        System.out.println(morePlusBenefit);
        System.out.println(morePlusTradeTime);
        System.out.println(lessPlusBenefit);
        System.out.println(lessPlusTradeTime);
        if (analysis) {
            System.out.println("交易明细分析开始》》》》》》》》》》》》》》》》》》》》》》》》》》》》》》》");
            analysisTrade(tradeDetail, data);
            System.out.println("交易明细分析结束《《《《《《《《《《《《《《《《《《《《《《《《《《《《《《《");
        }
        return benefit;
//        return (moreMore + lessMore) * 1.0 / (moreMore + moreLess + lessMore + lessLess);
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
