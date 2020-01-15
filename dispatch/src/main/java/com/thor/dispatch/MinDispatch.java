package com.thor.dispatch;

import com.alibaba.fastjson.JSON;
import com.thor.dispatch.util.DispatchUtil;
import com.thor.dispatch.util.NumberUtil;

/**
 * @author huangpin
 * @date 2019-07-12
 */
public class MinDispatch {
    /**
     * @param args
     */
    public static void main(String[] args) {
        int iLen = 5, jLen = 8;
        double[][] matrix = new double[iLen][jLen];
        matrix[0] = new double[]{0, -1, -14, -6, 0, 0, 0, 0};
        matrix[1] = new double[]{4, 1, 1, 1, 1, 0, 0, 0};
        matrix[2] = new double[]{2, 1, 0, 0, 0, 1, 0, 0};
        matrix[3] = new double[]{3, 0, 0, 1, 0, 0, 1, 0};
        matrix[4] = new double[]{6, 0, 3, 1, 0, 0, 0, 1};
//        int iLen = 5, jLen = 8;
//        double[][] matrix = new double[iLen][jLen];
//        matrix[0] = new double[]{0, -1, -14, 16, 0, 0, 0, 0};
//        matrix[1] = new double[]{4, 1, 1, 1, 1, 0, 0, 0};
//        matrix[2] = new double[]{2, 1, 0, 0, 0, 1, 0, 0};
//        matrix[3] = new double[]{3, 0, 0, 1, 0, 0, 1, 0};
//        matrix[4] = new double[]{6, 0, 3, 1, 0, 0, 0, 1};
        DispatchUtil.printMatrix(matrix,8);

        int inIndex = NumberUtil.findFirstNegative(matrix[0], 1, jLen - 1);
        int outIndex = NumberUtil.findMinRowNumber(matrix, 1, iLen - 1, 0, inIndex);
        double tempNumber = 0.0;
        while (inIndex != -1 || outIndex != -1) {

            tempNumber = matrix[outIndex][inIndex];
            for (int i = 0; i < jLen; i++) {
                matrix[outIndex][i] /= tempNumber;
            }

            /**
             * 消元
             */
            for (int i = 0; i < iLen; i++) {
                if (i != outIndex && matrix[i][inIndex] != 0) {
                    tempNumber = matrix[i][inIndex];
                    for (int j = 0; j < jLen; j++) {
                        matrix[i][j] = matrix[i][j] - tempNumber * matrix[outIndex][j];
                    }
                }
            }
            DispatchUtil.printMatrix(matrix,8);

            inIndex = NumberUtil.findFirstNegative(matrix[0], 1, jLen - 1);
            outIndex = NumberUtil.findMinRowNumber(matrix, 1, iLen - 1, 0, inIndex);
        }

        DispatchUtil.printResult(matrix, iLen, jLen);
    }
}
