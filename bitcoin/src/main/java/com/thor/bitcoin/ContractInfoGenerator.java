package com.thor.bitcoin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author huangpin
 * @date 2020-01-07
 */
public class ContractInfoGenerator {
    public static void main(String[] args) throws Exception {
        fixPrice();

//        StandardChartTheme mChartTheme = new StandardChartTheme("CN");
//        mChartTheme.setLargeFont(new Font("黑体", Font.BOLD, 20));
//        mChartTheme.setExtraLargeFont(new Font("宋体", Font.PLAIN, 15));
//        mChartTheme.setRegularFont(new Font("宋体", Font.PLAIN, 10));
//        ChartFactory.setChartTheme(mChartTheme);
//        CategoryDataset mDataset = GetDataset();
//        JFreeChart mChart = ChartFactory.createLineChart(
//                "多空比对比图",
//                "时间点",
//                "趋势",
//                mDataset,
//                PlotOrientation.VERTICAL,
//                true,
//                true,
//                false);
//
//        CategoryPlot categoryPlot = mChart.getCategoryPlot();
//        ValueAxis rangeAxis = categoryPlot.getRangeAxis();
//        rangeAxis.setAutoRangeMinimumSize(0.001);
//        rangeAxis.setUpperBound(1.8);
//        rangeAxis.setLowerBound(0.5);
//        rangeAxis.setAutoRange(false);
//
//        CategoryAxis domainAxis = categoryPlot.getDomainAxis();
//        domainAxis.setMaximumCategoryLabelLines(10);
//        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);// 横轴 lable 的位置 横轴上的 Lable 45度倾斜 DOWN_45
//
//        CategoryPlot mPlot = (CategoryPlot) mChart.getPlot();
//        mPlot.setBackgroundPaint(Color.LIGHT_GRAY);
//        mPlot.setRangeGridlinePaint(Color.BLUE);//背景底部横虚线
//        mPlot.setOutlinePaint(Color.RED);//边界线
//
//
//        ChartFrame mChartFrame = new ChartFrame("多空比对比图", mChart);
//        mChartFrame.pack();
//        mChartFrame.setVisible(true);

    }

    public static void formatData() throws Exception {
        String fileContent = FileUtil.getFileContent("/Users/hp/Desktop/data/origin_data.json");
        JSONObject dataJSON = JSON.parseObject("{" + fileContent.substring(0, fileContent.length() - 1) + "}");

        JSONArray result = new JSONArray();
        ArrayList<String> strings = new ArrayList<String>(dataJSON.keySet());
        Collections.sort(strings);

        for (String key : strings){
            JSONObject subData = dataJSON.getJSONObject(key);
            subData.put("time",key);
            result.add(subData);
        }

        for (int i = 0; i < result.size(); i++) {
            System.out.println(result.getJSONObject(i).toJSONString()+",");
        }
    }

    public static void fixPrice() throws Exception {
        String begin = "2020-01-09 02";
        String end = "2020-01-09 03";

        String fileContent = FileUtil.getFileContent("/Users/hp/Desktop/contactInfo");
        JSONArray dataJSON = JSON.parseArray("[" + fileContent.substring(0, fileContent.length() - 1) + "]");

        for (int i = 0; i < dataJSON.size(); i++) {
            JSONObject subData = dataJSON.getJSONObject(i);
            String time = subData.getString("time");
            if (time.compareTo(begin)>0 && time.compareTo(end)<0){
                String string = subData.getJSONArray("data").getJSONObject(2).getString("price");
                subData.getJSONArray("data").getJSONObject(0).put("price",string);
            }
        }

        for (int i = 0; i < dataJSON.size(); i++) {
            System.out.println(dataJSON.getJSONObject(i).toJSONString()+",");
        }
    }

    public static CategoryDataset GetDataset() throws Exception {
        String fileContent = FileUtil.getFileContent("/Users/hp/Desktop/contactInfo");
        JSONArray dataJSON = JSON.parseArray("[" + fileContent.substring(0, fileContent.length() - 1) + "]");
        DefaultCategoryDataset mDataset = new DefaultCategoryDataset();

        double specific = 0;
        double averagePrice = 0;
        for (int j = 0; j < dataJSON.size(); j++) {
            JSONObject oneData = dataJSON.getJSONObject(j);
            String key = oneData.getString("time");
            JSONArray data = oneData.getJSONArray("data");
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

                if (rank.equals("1")) {
                    averagePrice += price.doubleValue();
                }
                data24_long_total = data24_long_total.add(data24_long);
                data24_short_total = data24_short_total.add(data24_short);
                data1h_long_total = data1h_long_total.add(data1h_long);
                data1h_short_total = data1h_short_total.add(data1h_short);
                System.out.println(String.format("time：%s,rank:%s site:%s 24hvalue:%s", key, rank, name,
                        data24_long.divide(data24_short, 3, BigDecimal.ROUND_HALF_UP)));
//                mDataset.addValue(data24_long.divide(data24_short, 3, BigDecimal.ROUND_HALF_UP), rank + name + "多空比", key);

            }
            System.out.println(String.format("time：%s,total24HValue:%s", key,
                    data24_long_total.divide(data24_short_total, 3, BigDecimal.ROUND_HALF_UP)));
            mDataset.addValue(data24_long_total.divide(data24_short_total, 3, BigDecimal.ROUND_HALF_UP), "总多空比", key);
            mDataset.addValue(data1h_long_total.divide(data1h_short_total, 3, BigDecimal.ROUND_HALF_UP), "1H总多空比", key);
            specific += data24_long_total.divide(data24_short_total, 3, BigDecimal.ROUND_HALF_UP).doubleValue();
        }

        double standardPrice = averagePrice/specific;
        for (int j = 0; j < dataJSON.size(); j++) {
            JSONArray data = dataJSON.getJSONObject(j).getJSONArray("data");
            String key = dataJSON.getJSONObject(j).getString("time");
            for (int i = 0; i < data.size(); i++) {
                JSONObject subData = data.getJSONObject(i);
                String rank = subData.getString("rank");

                if (rank.equals("1")) {
                    mDataset.addValue(subData.getDoubleValue("price")/standardPrice, "价格趋势", key);
                } else {
                    continue;
                }
            }
        }
        return mDataset;
    }
}
