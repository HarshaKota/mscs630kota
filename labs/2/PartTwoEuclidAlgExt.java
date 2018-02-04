/*
 * file: PartTwoEuclidAlgExt.java
 * author: Harsha Kota
 * course: MSCS 630
 * assignment: Lab 2
 * due date: Saturday, February 3, 2018
 * version: 1.0
 *
 * This file contains the declaration of the
 * PartTwoEuclidAlgExt class.
 */

import java.util.Scanner;

/*
 * PartTwoEuclidAlgExt
 *
 * This class implements the Extended Euclidean Algorithm to find the greatest common divisor
 * between two integers a,b > 0 and computes the values for x, y that satisfy the equation
 * d = a*x + b*y
 */
public class PartTwoEuclidAlgExt {

  /*
   * euclidAlgExt
   *
   * This function takes two positive integers
   * and returns the corresponding greatest common divisor
   * and the values for x and y that satisfy the below equation
   * d = a*x + b*y
   * as an array of type long
   *
   * Parameters:
   *   a,b: positive integers
   *   U,V: array of initial values for record keeping
   *   W: temporary array to assist with calculations
   *   quotient: integer result of computation between two integers
   *
   * Return value: array containing {gcd, x, y}
   */
  private static long[] euclidAlgExt(long a, long b) {
    long[] U = {a,1,0};
    long[] V = {b,0,1};
    long[] W = new long[3];
    while (V[0] > 0) {
      long quotient = Math.floorDiv(U[0],V[0]);
      for (int i=0; i<W.length; i++) {
        W[i] = U[i] - V[i]*quotient;
      }
      System.arraycopy(V,0,U,0,3);
      System.arraycopy(W,0,V,0,3);
    }
    return U;
  }

  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    while (scanner.hasNextLine()) {
      long a = scanner.nextLong();
      long b = scanner.nextLong();
      long[] result = a > b ? euclidAlgExt(a,b) : euclidAlgExt(b,a);
      for (long x: result) System.out.print(x + " ");
      System.out.println();
    }
    scanner.close();
  }

}
