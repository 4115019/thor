package com.thor.dispatch.util;

import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * @author huangpin
 * @date 2019-07-12
 */
public class DispatchUtil {

    public static double[][] generateMatrix(int userSize, int serviceSize, int thirdPartySize, List<Double> userData, List<Double> thirdPartyData) {
        int iLen = 1 + thirdPartySize + userSize;
        int useX = userSize * thirdPartySize;
        int jLen = 1 + useX + thirdPartySize + userSize;
        double[][] matrix = new double[iLen][jLen];
        for (int i = 1; i <= useX; i++) {
            matrix[0][i] = 1;
        }
        for (int i = 1; i <= thirdPartySize; i++) {
            matrix[i][0] = thirdPartyData.get(i - 1);
            for (int j = 1; j <= useX; j++) {
                if (i != thirdPartySize && j % thirdPartySize == i) {
                    matrix[i][j] = 1;
                }
                if (i == thirdPartySize && j % thirdPartySize == 0) {
                    matrix[i][j] = 1;

                }
            }
            matrix[i][userSize * thirdPartySize + i] = 1;
        }

        for (int i = thirdPartySize + 1; i < iLen; i++) {
            int beginJ = (i - thirdPartySize-1) * thirdPartySize;
            for (int j = 0; j < thirdPartySize; j++) {
                matrix[i][j+beginJ+1] = 1;
            }
            matrix[i][0] = userData.get(i-thirdPartySize-1);
            matrix[i][userSize * thirdPartySize + i] = 1;
        }

        return matrix;
    }

    public static void printResult(double[] result, double[][] matrix) {
        if (result == null || result.length == 0) {
            return;
        }
        System.out.println("result = " + (-matrix[0][0]));
        for (int i = 1; i < result.length; i++) {
            System.out.println("X" + result[i] + "=" + matrix[i][0]);
        }
    }

    public static void printResult(double[][] matrix, int iLen, int jLen) {
        for (int i = 1; i < jLen; i++) {
            if (matrix[0][i] != 0) {
                for (int j = 1; j < iLen; j++) {
                    matrix[j][i] = 0;
                }
            }
        }
        for (int i = 1; i < iLen; i++) {

            for (int j = 1; j < jLen; j++) {
                if (matrix[i][j] != 0) {
                    System.out.println(String.format("X%s = %s", j, matrix[i][0] / matrix[i][j]));
                }
            }
        }
    }

    private static String getFormatString(String str, Integer formatLength) {
        Integer spaceLenth = 0;
        if (str == null) {
            str = "";
            spaceLenth = formatLength;
        } else {
            spaceLenth = formatLength - str.length() > 0 ? formatLength - str.length() : 0;
        }
        StringBuilder stringBuilder = new StringBuilder(str);
        while (spaceLenth > 0) {
            stringBuilder.append(" ");
            spaceLenth--;
        }
        return stringBuilder.toString();
    }

    public static void printMatrix(double[][] matrix, Integer formatLength) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < matrix[0].length; i++) {
            sb.append(getFormatString(String.valueOf(i), formatLength)).append(",");
        }
        System.out.println(sb.toString());
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        for (double[] row : matrix) {
            StringBuilder oneData = new StringBuilder();
            for (double data : row) {
                oneData.append(getFormatString(String.valueOf(data), formatLength)).append(",");
            }
            System.out.println(oneData.toString());
        }
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    }
}
