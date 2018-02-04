/*
 * file: PartOneEuclidAlg.java
 * author: Harsha Kota
 * course: MSCS 630
 * assignment: Lab 2
 * due date: Saturday, February 3, 2018
 * version: 1.0
 *
 * This file contains the declaration of the
 * PartOneEuclidAlg class.
 */

import java.util.Scanner;

/*
 * PartOneEuclidAlg
 *
 * This class implements the Euclidean Algorithm to find the greatest common divisor
 * between two integers a,b > 0
 */
public class PartOneEuclidAlg {

  /*
   * euclidAlg
   *
   * This function takes two positive integers
   * and returns the corresponding greatest common divisor
   * as an integer using the following equation;
   * a = b*quotient + remainder
   *
   * Parameters:
   *   a,b: positive integers
   *   quotient: integer result of computation a|b
   *   remainder: integer result of a%b using equation remainder = a - b*quotient
   *
   * Return value: greatest common divisor of a,b of type long
   */
  private static long euclidAlg(long a, long b) {
    while (b != 0) {
      long quotient = Math.floorDiv(a, b);
      long remainder = a - b * quotient;
      a = b;
      b = remainder;
    }
    return a;
  }

  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    while (scanner.hasNextLine()) {
      long a = scanner.nextLong();
      long b = scanner.nextLong();
      // Appropriate function is called if a >= b
      long result = a >= b ? euclidAlg(a, b) : euclidAlg(b, a);
      System.out.println(result);
    }
    scanner.close();
  }

}