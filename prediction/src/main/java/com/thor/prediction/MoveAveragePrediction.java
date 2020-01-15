package com.thor.prediction;

import com.thor.common.utils.FileUtil;
import com.thor.prediction.model.DataModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author huangpin
 * @date 2019-06-17
 */
public class MoveAveragePrediction {

    /**
     * 加权移动平均法
     *
     * @param args
     */
    public static void main(String[] args) {
        String predictionPath = "data/20190618-regeo-1";
        double ratio = 0.95;
        double standard = 20;

        List<Integer> x = new ArrayList<Integer>();
        List<String> xTitle = new ArrayList<String>();
        List<Integer> y = new ArrayList<Integer>();
        List<Double> yp = new ArrayList<Double>();
        List<Double> ypPlus = new ArrayList<Double>();
        List<Integer> qpsData = FileUtil.getListIntegerFile(predictionPath);
        if (qpsData.size() != 0) {
            double sumDiff = 0;
            double maxDiff = 0;
            double average = qpsData.get(0);
            double totalDiffNum = 0;
            int total = 0;
            List<DataModel> topDiff = new ArrayList();
            for (int i = 0; i < qpsData.size(); i++) {
                if ((i / 60) % 24 <= 14
                        || (i / 60) % 24 >= 19) {
                    continue;
                }
                total++;
                double diff = (average - qpsData.get(i)) / qpsData.get(i) * 100;
                System.out.println(String.format("实际：%s，预测：%s，差值：%s，占比：%s", qpsData.get(i), average, average - qpsData.get(i), diff) + "%");
                DataModel dataModel = DataModel.builder()
                        .queryNum(qpsData.get(i))
                        .diff(diff)
                        .index(i)
                        .build();
                if (diff > 0) {
                    sumDiff += diff;
                    if (maxDiff - diff < 0) {
                        maxDiff = diff;
                    }
                    totalDiffNum += (average - qpsData.get(i));
                } else {
                    sumDiff -= diff;
                    if (maxDiff + diff < 0) {
                        maxDiff = diff;
                    }
                    dataModel.setDiff(-diff);
                    totalDiffNum -= (average - qpsData.get(i));

                }
                topDiff.add(dataModel);
                if (i % 1 == 0) {
                    x.add(i + 1);
                    if (i % 60 == 0) {
                        xTitle.add(String.valueOf(i / 60));
                    } else {
                        xTitle.add("");
                    }
                    y.add(qpsData.get(i));
                    yp.add(average);
                    ypPlus.add(average + 1000);
                }
                average = (ratio * qpsData.get(i) + (1 - ratio) * average);
            }
            System.out.println("max diff:" + maxDiff + "%");
            double totalDiff = sumDiff / total;
            System.out.println("total diff:" + totalDiff + "%");
            System.out.println("total diff num:" + totalDiffNum / total);
            Collections.sort(topDiff, new Comparator<DataModel>() {
                @Override
                public int compare(DataModel o1, DataModel o2) {
//                    return o1 - o2;  //升序
                    //降序
                    return o2.getDiff() > o1.getDiff() ? 1 : -1;
                }
            });
            StringBuilder stringBuilder = new StringBuilder();
            int topn = 0;
            for (DataModel one : topDiff) {
                if (one.getDiff() > standard) {
                    topn++;
                    stringBuilder.append(one.getIndex() / 60).append(":").append(one.getIndex() % 60).append(":").append(one.getQueryNum()).append("|").append(one.getDiff()).append("\n");
                }
            }
            System.out.println("total:" + qpsData.size());
            System.out.println("topN:" + topn);
            System.out.println(stringBuilder);
            System.out.println(x);
            System.out.println(xTitle);
            System.out.println(y);
            System.out.println(yp);
            System.out.println(ypPlus);
        }
    }
}
