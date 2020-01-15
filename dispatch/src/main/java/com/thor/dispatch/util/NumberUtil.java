package com.thor.dispatch.util;

/**
 * @author huangpin
 * @date 2019-07-12
 */
public class NumberUtil {
    public static Integer findFirstNegative(double[] matrix, int begin, int end) {
        for (int i = begin; i <= end; i++) {
            if (matrix[i] < 0) {
                return i;
            }
        }
        return -1;
    }
    public static Integer findFirstPositive(double[] matrix, int begin, int end) {
        for (int i = begin; i <= end; i++) {
            if (matrix[i] > 0) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 注意 注意 注意  所有判断的值都是正数
     *
     * @param matrix
     * @param begin
     * @param end
     * @param col
     * @param judgeCol
     * @return
     */
    public static Integer findMinRowNumber(double[][] matrix, int begin, int end, int col, int judgeCol) {
        if (judgeCol <= 0) {
            return -1;
        }

        Double min = null;
        int minRow = -1;

        for (int i = begin; i <= end; i++) {
            if (matrix[i][judgeCol] >= 0 && min == null) {
                min = matrix[i][col] / matrix[i][judgeCol];
                minRow = i;
                continue;
            }
            if (matrix[i][judgeCol] >= 0 && min != null && matrix[i][col] / matrix[i][judgeCol] >= 0 && matrix[i][col] / matrix[i][judgeCol] < min) {
                min = matrix[i][col] / matrix[i][judgeCol];
                minRow = i;
            }
        }
        return minRow;
    }

}
