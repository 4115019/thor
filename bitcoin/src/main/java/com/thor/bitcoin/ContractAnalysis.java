package com.thor.bitcoin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

/**
 * @author huangpin
 * @date 2020-01-08
 */
public class ContractAnalysis {

    public static double moreBuyPoint = 0;
    public static double moreSellPoint = 0;
    public static double lessBuyPoint = 0;
    public static double lessSellPoint = 0;

    public static int minLength = 0;

    public static void main(String[] args) throws Exception {
        JSONArray data = JSON.parseArray(FileUtil.getFileContent("/Users/hp/Desktop/data/formated_data.json"));

        /**
         * 训练的时间周期
         * 复盘的时间周期
         */
        String trainBeginTime = "2020-01-07 00:00:00";
        String trainEndTime = "2020-01-10 24:00:00";
        String benefitBeginTime = "2020-01-11 00:00:00";
        String benefitEndTime = "2020-01-11 24:00:00";
        testBuy(data, trainBeginTime, trainEndTime);
        testSell(data, trainBeginTime, trainEndTime);

        testBenefit(data, benefitBeginTime, benefitEndTime);
    }

    public static double testBuy(JSONArray data, String trainBeginTime, String trainEndTime) {
        System.out.println("做多训练开始>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        double benefit = 0;
        int buyTime = 0;
        int sellTime = 0;
        double bestBuyPoint = 0;
        double bestSellPoint = 0;
        JSONArray bestDetail = null;
        for (int buy = 200; buy < 3000; buy++) {
            for (int sell = buy + 1; sell < 3000; sell++) {
                double testBenefit = 0;
                int testBuyTime = 0;
                int testSellTime = 0;
                double buyPoint = buy * 1.0 / 1000;
                double sellPoint = sell * 1.0 / 1000;

                double buyPrice = 0;
                int buyIndex = 0;
                double lastValue = 100;

                boolean haveContract = false;

                JSONArray detail = new JSONArray();
                for (int i = 0; i < data.size(); i++) {
                    JSONArray oneData = data.getJSONArray(i);
                    if (oneData.getString(0).compareTo(trainBeginTime) >= 0
                            && oneData.getString(0).compareTo(trainEndTime) <= 0) {
                        if (lastValue <= buyPoint
                                && oneData.getDoubleValue(3) > lastValue
                                && !haveContract) {
                            buyPrice = oneData.getDoubleValue(4);
                            buyIndex = i;
                            testBuyTime++;
                            haveContract = true;
                            detail.add(oneData);
                        }

                        if (i - buyIndex > minLength
                                && oneData.getDoubleValue(3) < lastValue
                                && oneData.getDoubleValue(3) >= (sellPoint + 0.1)
                                && haveContract) {
                            testBenefit += oneData.getDoubleValue(4) - buyPrice;
                            testSellTime++;
                            haveContract = false;
                            detail.add(oneData);
                        }
                        lastValue = oneData.getDoubleValue(3);
                    }
                }

                if (sellPoint - buyPoint > 0.3 && testBenefit > benefit) {
                    benefit = testBenefit;
                    buyTime = testBuyTime;
                    sellTime = testSellTime;
                    bestBuyPoint = buyPoint;
                    bestSellPoint = sellPoint;
                    bestDetail = detail;
                }
            }
        }

        if (bestDetail != null) {
            for (int i = 0; i < bestDetail.size(); i++) {
                JSONArray jsonArray = bestDetail.getJSONArray(i);
                if (i % 2 == 0) {
                    System.out.println(String.format("时间:%s,方向：%s,价格：%s", jsonArray.getString(0),
                            "买入做多", jsonArray.getDoubleValue(4)));
                } else {
                    System.out.println(String.format("时间:%s,方向：%s,价格：%s，收益：%s", jsonArray.getString(0),
                            "卖出平多", jsonArray.getDoubleValue(4),
                            (jsonArray.getDoubleValue(4) - bestDetail.getJSONArray(i - 1).getDoubleValue(4))));
                }
            }
        }

        System.out.println("测试时间周期：" + trainBeginTime + "至" + trainEndTime);
        System.out.println("测试做多最大收益：" + benefit);
        System.out.println("做空买入次数：" + buyTime);
        System.out.println("做空卖出次数：" + sellTime);
        System.out.println("做多买入点：" + bestBuyPoint);
        moreBuyPoint = bestBuyPoint;
        System.out.println("做多卖出点：" + bestSellPoint);
        moreSellPoint = bestSellPoint;
        return benefit;
    }

    public static double testSell(JSONArray data, String trainBeginTime, String trainEndTime) {
        System.out.println("做空训练开始>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

        double benefit = 0;
        int buyTime = 0;
        int sellTime = 0;
        double bestBuyPoint = 0;
        double bestSellPoint = 0;
        JSONArray bestDetail = null;
        for (int buy = 200; buy < 3000; buy++) {
            for (int sell = buy + 1; sell < 3000; sell++) {
                double testBenefit = 0;
                int testBuyTime = 0;
                int testSellTime = 0;
                double buyPoint = buy * 1.0 / 1000;
                double sellPoint = sell * 1.0 / 1000;

                double buyPrice = 0;
                int buyIndex = 0;
                double lastValue = 100;

                boolean haveContract = false;
                JSONArray detail = new JSONArray();

                for (int i = 0; i < data.size(); i++) {
                    JSONArray oneData = data.getJSONArray(i);
                    if (oneData.getString(0).compareTo(trainBeginTime) >= 0
                            && oneData.getString(0).compareTo(trainEndTime) <= 0) {
                        if (lastValue >= sellPoint
                                && oneData.getDoubleValue(3) < sellPoint
                                && !haveContract) {
                            buyPrice = oneData.getDoubleValue(4);
                            testBuyTime++;
                            haveContract = true;
                            detail.add(oneData);
                        }

                        if (i - buyIndex > minLength
                                && oneData.getDoubleValue(3) <= buyPoint
                                && haveContract) {
                            testBenefit += buyPrice - oneData.getDoubleValue(4);
                            testSellTime++;
                            haveContract = false;
                            detail.add(oneData);
                        }
                        lastValue = oneData.getDoubleValue(3);
                    }
                }

                if (sellPoint - buyPoint > 0.3 && testBenefit > benefit) {
                    benefit = testBenefit;
                    buyTime = testBuyTime;
                    sellTime = testSellTime;
                    bestBuyPoint = buyPoint;
                    bestSellPoint = sellPoint;
                    bestDetail = detail;
                }
            }
        }

        if (bestDetail != null) {
            for (int i = 0; i < bestDetail.size(); i++) {
                JSONArray jsonArray = bestDetail.getJSONArray(i);
                if (i % 2 == 0) {
                    System.out.println(String.format("时间:%s,方向：%s,价格：%s", jsonArray.getString(0),
                            "卖出做空", jsonArray.getDoubleValue(4)));
                } else {
                    System.out.println(String.format("时间:%s,方向：%s,价格：%s，收益：%s", jsonArray.getString(0),
                            "买入平空", jsonArray.getDoubleValue(4),
                            (bestDetail.getJSONArray(i - 1).getDoubleValue(4) - jsonArray.getDoubleValue(4))));
                }
            }
        }


        System.out.println("测试时间周期：" + trainBeginTime + "至" + trainEndTime);
        System.out.println("测试做空最大收益：" + benefit);
        System.out.println("做空买入次数：" + buyTime);
        System.out.println("做空卖出次数：" + sellTime);
        System.out.println("做空买入点：" + bestSellPoint);
        lessBuyPoint = bestSellPoint;
        System.out.println("做空卖出点：" + bestBuyPoint);
        lessSellPoint = bestBuyPoint;
        return benefit;
    }

    public static double testBenefit(JSONArray data, String benefitBeginTime, String benefitEndTime) {
        System.out.println("模拟交易开始>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

        double lastValue = 0;

        double moreBenefit = 0;
        double lessBenefit = 0;
        double morePrice = 0;
        double lessPrice = 0;
        int moreBuyTime = 0;
        int moreSellTime = 0;
        int lessBuyTime = 0;
        int lessSellTime = 0;
        for (int i = 0; i < data.size(); i++) {
            JSONArray subData = data.getJSONArray(i);
            if (subData.getString(0).compareTo(benefitBeginTime) >= 0
                    && subData.getString(0).compareTo(benefitEndTime) <= 0) {

                /**
                 * 多单买入
                 * 多单卖出
                 * 空单买入
                 * 空单卖出
                 */
                if (morePrice <= 1
                        && lastValue <= moreBuyPoint
                        && subData.getDoubleValue(3) > lastValue) {
                    morePrice = subData.getDoubleValue(4);
                    moreBuyTime++;
                    System.out.println(String.format("时间:%s,方向：%s,价格：%s", subData.getString(0),
                            "买入做多", subData.getDoubleValue(4)));
                }

                if (morePrice >= 1
                        && subData.getDoubleValue(3) < lastValue
                        && subData.getDoubleValue(3) >= (moreSellPoint + 0.1)) {
                    System.out.println(String.format("时间:%s,方向：%s,价格：%s，收益：%s", subData.getString(0),
                            "卖出平多", subData.getDoubleValue(4), (subData.getDoubleValue(4) - morePrice)));
                    moreBenefit += subData.getDoubleValue(4) - morePrice;
                    moreSellTime++;
                    morePrice = 0;
                }

                if (lessPrice <= 1
                        && lastValue >= lessBuyPoint
                        && subData.getDoubleValue(3) < lessBuyPoint) {
                    lessPrice = subData.getDoubleValue(4);
                    lessBuyTime++;
                    System.out.println(String.format("时间:%s,方向：%s,价格：%s", subData.getString(0),
                            "卖出做空", subData.getDoubleValue(4)));
                }

                if (lessPrice >= 1
                        && subData.getDoubleValue(3) <= lessSellPoint) {
                    System.out.println(String.format("时间:%s,方向：%s,价格：%s，收益：%s", subData.getString(0),
                            "买入平空", subData.getDoubleValue(4), (lessPrice - subData.getDoubleValue(4))));
                    lessBenefit += lessPrice - subData.getDoubleValue(4);
                    lessSellTime++;
                    lessPrice = 0;
                }

                lastValue = subData.getDoubleValue(3);
            }
        }

        System.out.println("测试时间周期：" + benefitBeginTime + "至" + benefitEndTime);
        System.out.println("测试总收益：" + (moreBenefit + lessBenefit));

        System.out.println("测试做多收益：" + moreBenefit);
        System.out.println("做多买入次数：" + moreBuyTime);
        System.out.println("做多卖出次数：" + moreSellTime);


        System.out.println("测试做空收益：" + lessBenefit);
        System.out.println("做空买入次数：" + lessBuyTime);
        System.out.println("做空卖出次数：" + lessSellTime);

        return 0;
    }
}
