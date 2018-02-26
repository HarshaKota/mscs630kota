/*
 * file: Lab3Part1.java
 * author: Harsha Kota
 * course: MSCS 630
 * assignment: Lab 3
 * due date: Wednesday, February 28, 2018
 * version: 1.0
 *
 * This file contains the declaration of the
 * Lab3Part1 class.
 */

import java.util.Scanner;

/*
 * Lab3Part1
 *
 * This class implements the cofactor expansion algorithm to find the determinant
 * of n x n matrix in modulo m.
 */
public class Lab3Part1 {

  /*
   * cofModDet
   *
   * This function takes two inputs, a modulo under which all integer calculations
   * are performed and an n x n matrix and returns the determinant in modulo m.
   *
   * It uses the following equation to find the determinant;
   * 2 x 2 matrix: a[0][0] * a[1][1] - a[0][1] * a[1][0]
   * 3 x 3 matrix and above: a[0][0]*det(a[0][0]) - a[0][1]*det(a[0][1]) +
   *                         a[0][2]*det(a[0][2])......+(-1)^n+1*a[0][n]*det(a[0][n])
   *
   * Parameters:
   *   m:          modulo integer
   *   A:          n x n matrix
   *   temp[][]:   temporary matrix to hold the sub matrix
   *
   * Return value: integer determinant of A in modulo m
   */
  public static int cofModDet(int m, int[][] A) {
    int result = 0;

    if (A.length == 1) {
      result = A[0][0];
      return result;
    }

    if (A.length == 2) {
      result = Math.floorMod(A[0][0],m) * Math.floorMod(A[1][1],m) -
               Math.floorMod(A[0][1],m) * Math.floorMod(A[1][0],m);
      return result;
    }

    for (int i = 0; i < A.length; i++) {
      int temp[][] = new int[A.length - 1][A.length - 1];

      for (int j = 1; j < A.length; j++) {
        for (int k = 0; k < A.length; k++) {
          if (k < i) {
            temp[j - 1][k] = A[j][k];
          } else if (k > i) {
            temp[j - 1][k - 1] = A[j][k];
          }
        }
      }
      result += (A[0][i] * Math.pow(-1, i) * cofModDet(m, temp));
    }
    return Math.floorMod(result,m);
  }

  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);

    int modulo = scanner.nextInt();
    int matrixSize = scanner.nextInt();
    int[][] A = new int[matrixSize][matrixSize];

    for (int i=0; i<matrixSize; i++) {
      for (int j=0; j<matrixSize; j++) {
        A[i][j] = scanner.nextInt();
      }
    }

    int result = cofModDet(modulo,A);
    System.out.println(result);
  }
}
