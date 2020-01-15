package com.thor.dispatch;

import com.thor.dispatch.util.DispatchUtil;
import com.thor.dispatch.util.NumberUtil;

import java.util.ArrayList;

/**
 * @author huangpin
 * @date 2019-07-12
 */
public class MaxDispatch {
    /**
     * @param args
     */
    public static void main(String[] args) {

        double[][] matrix = DispatchUtil.generateMatrix(9, 1, 2,
                new ArrayList<Double>() {{
                    add(15173D);
                    add(15175D);
                    add(15176D);
                    add(3656D);
                    add(2151D);
                    add(2153D);
                    add(473D);
                    add(2156D);
                    add(45445D);
                }},
                new ArrayList<Double>() {{
                    add(5000D);
                    add(6000D);
                }});
        for (int i = 0; i < matrix.length; i++) {
            matrix[i][18] = 0;
        }

        DispatchUtil.printMatrix(matrix, 8);

        int iLen = matrix.length, jLen = matrix[0].length;
        double[] result = new double[iLen];
        for (int i = 1; i < result.length; i++) {
            result[i] = jLen - iLen + i;
        }

        DispatchUtil.printMatrix(matrix, 8);

        int inIndex = NumberUtil.findFirstPositive(matrix[0], 1, jLen - 1);
        int outIndex = NumberUtil.findMinRowNumber(matrix, 1, iLen - 1, 0, inIndex);
        double tempNumber;
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
            DispatchUtil.printMatrix(matrix, 8);
            result[outIndex] = inIndex;
            DispatchUtil.printResult(result, matrix);

            inIndex = NumberUtil.findFirstPositive(matrix[0], 1, jLen - 1);
            outIndex = NumberUtil.findMinRowNumber(matrix, 1, iLen - 1, 0, inIndex);
        }

    }
}
