package com.littlechoc.olddriver.utils;

/**
 * @author Junhao Zhou 2017/4/26
 */

public class MatrixUtils {

  public static float[][] toMatrix(float[] src, int row, int col) {
    if (row <= 0 || col <= 0) {
      throw new IllegalArgumentException("row or col can not less than 1");
    }
    if (src == null || src.length != row * col) {
      throw new IllegalArgumentException("the length of res is not illegal");
    }
    float[][] matrix = new float[row][col];
    for (int i = 0; i < row; i++) {
      for (int j = 0; j < col; j++) {
        matrix[i][j] = src[i * col + j];
      }
    }
    return matrix;
  }

  public static void multi(float[][] left, float[][] right, float[][] res) {
    // check
    if (!isMatrixListLegal(left, right, res)) {
      throw new IllegalArgumentException("matrix is illegal");
    }
    if (left[0].length != right.length) {
      throw new IllegalArgumentException("the left's column is not equal the right's row");
    }
    if (left.length != res.length || right[0].length != res[0].length) {
      throw new IllegalArgumentException("the size of res is illegal");
    }

    // compute
    float[][] temp = new float[res.length][res[0].length];
    for (int row = 0; row < res.length; row++) {
      for (int col = 0; col < res[0].length; col++) {
        float cell = 0f;
        for (int i = 0; i < left[0].length; i++) {
          cell += left[row][i] * right[i][col];
        }
        temp[row][col] = cell;
      }
    }

    // copy
    for (int row = 0; row < temp.length; row++) {
      System.arraycopy(temp[row], 0, res[row], 0, temp[0].length);
    }
  }

  private static boolean isMatrixListLegal(float[][]... matrixList) {
    if (matrixList == null || matrixList.length == 0) {
      return false;
    }
    for (float[][] matrix : matrixList) {
      if (!isMatrixLegal(matrix)) {
        return false;
      }
    }
    return true;
  }

  private static boolean isMatrixLegal(float[][] matrix) {
    if (matrix == null) {
      return false;
    }
    if (matrix.length == 0 || matrix[0].length == 0) {
      return false;
    }
    return true;
  }

}
