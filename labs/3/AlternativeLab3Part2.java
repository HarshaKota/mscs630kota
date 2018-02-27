/*
 * file: AlternativeLab3Part2.java
 * author: Harsha Kota
 * course: MSCS 630
 * assignment: Lab 3
 * due date: Wednesday, February 28, 2018
 * version: 1.0
 *
 * This file contains the declaration of the
 * AlternativeLab3Part2 class.
 */

import java.util.Scanner;

/*
 * AlternativeLab3Part2
 *
 * This class provides a padded matrix Ps for any given plaintext P and
 * a substitution character s and outputs a hexadecimal representation of Ps in
 * a 4 x 4 matrix.
 */
public class AlternativeLab3Part2 {

  /*
   * getHexMatP
   *
   * This function takes two inputs, a substitution character and a string p of
   * length <= 16 and returns p as a representation of a 4 x 4 matrix in Ascii.
   *
   * Parameters:
   *   s:                 substitution character
   *   p:                 plaintext string/substring of P
   *   index:             counter variable to go through each character in string p
   *   temp[][]:          temporary matrix to hold the 4 x 4 representation of p
   *   matrixSize:        number of values required to create a 4 x 4 matrix
   *   noOfPaddingValues: number of values to be padded in to create a 4 x 4 matrix
   *
   * Return value: integer matrix of length 4 x 4 for the string p in Ascii
   */
  public static int[][] getHexMatP(char s, String p) {
    int index = 0;
    int[][] A = new int[4][4];

    if (p.length() < 16) {
      int matrixSize = 16;
      int noOfPaddingValues = matrixSize - p.length();
      for (int i=0; i<noOfPaddingValues; i++) {
        p += s;
      }
    }

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
    int plainTextLength = plainText.length();
    scanner.close();

    int lengthFor4x4Matrix = 16;
    for (int i=0; i<plainTextLength; i+=lengthFor4x4Matrix) {
      String subStr = "";
      try {
        subStr = plainText.substring(i, i + lengthFor4x4Matrix + 1);
      } catch (StringIndexOutOfBoundsException e) {
        subStr = plainText.substring(i, plainTextLength);
      }
      int[][] A = getHexMatP(subChar, subStr);
      for (int j = 0; j < 4; j++) {
        //Converting Ascii value's to Hexadecimal value's before printing
        System.out.println(toHex(A[j][0]) + " " + toHex(A[j][1]) + " " +
                           toHex(A[j][2]) + " " + toHex(A[j][3]));
      }
      System.out.println();
    }
  }
}
