/*
 * file: Lab3Part2.java
 * author: Harsha Kota
 * course: MSCS 630
 * assignment: Lab 3
 * due date: Wednesday, February 28, 2018
 * version: 1.0
 *
 * This file contains the declaration of the
 * Lab3Part2 class.
 */

import java.util.Scanner;

/*
 * Lab3Part2
 *
 * This class provides a padded matrix Ps for any given plaintext P and
 * a substitution character s and outputs a hexadecimal representation of Ps in
 * a 4 x 4 matrix.
 */
public class Lab3Part2 {

  /*
   * getHexMatP
   *
   * This function takes two inputs, a substitution character and a string p of
   * length <= 16 and returns p as a representation of a 4 x 4 matrix in Ascii.
   *
   * Parameters:
   *   s:          substitution character
   *   p:          plaintext string/substring of P
   *   index:      counter variable to go through each character in string p
   *   temp[][]:   temporary matrix to hold the 4 x 4 representation of p
   *
   * Return value: integer matrix of length 4 x 4 for the string p in Ascii
   */
  public static int[][] getHexMatP(char s, String p) {
    int index = 0;
    int[][] A = new int[4][4];

    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        A[j][i] = (int) p.charAt(index);
        index++;
      }
    }

    return A;
  }

  /*
   * toHex
   *
   * This function takes a integer value and returns a Hexadecimal value
   *
   * Parameters:
   *   x:          Ascii integer value
   *
   * Return value: A string representing the Hexadecimal value of x
   */
  public static String toHex(int x) {
    return Integer.toHexString(x).toUpperCase();
  }

  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);

    char subChar = scanner.nextLine().charAt(0);

    String plainText = scanner.nextLine();
    int stringLength = plainText.length();
    //To calculate the no of 4 x 4 matrices for a given plainText length
    int nearestMultiple = (int) Math.ceil((double) stringLength / 16);
    //To calculate the required length for a #nearestMultiple number of matrices
    int nearestValue = 16 * nearestMultiple;
    //To calculate the number of values to be padded with the substitution character #subChar
    int toBeFilledIn = nearestValue-stringLength;

    String newPlainText = plainText;
    //Creating new plainText with padding #subChar
    if (toBeFilledIn <= 0){ }
    else {
      for (int i=0; i<toBeFilledIn; i++)
        newPlainText += subChar;
    }

    for (int i=0; i<newPlainText.length(); i+=16) {
      String subStr = newPlainText.substring(i, i + 16);
      int[][] A = getHexMatP(subChar, subStr);
      for (int j = 0; j < 4; j++) {
        //Converting Ascii value's to Hexadecimal value's before printing
        System.out.println(toHex(A[j][0]) + " " + toHex(A[j][1]) + " " + toHex(A[j][2]) + " " + toHex(A[j][3]));
      }
      System.out.println();
    }
  }
}
