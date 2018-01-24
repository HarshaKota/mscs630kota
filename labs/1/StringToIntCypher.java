/*
 * file: StringToIntCypher.java
 * author: Harsha Kota
 * course: MSCS 630
 * assignment: Lab 1
 * due date: Wednesday, January 24, 2018
 * version: 1.0
 *
 * This file contains the declaration of the
 * StringToIntCypher class.
 */

import java.util.Scanner;

/*
 * StringToIntCypher
 *
 * This class implements cryptography to convert
 * a given to string to an int cypher
 */

public class StringToIntCypher {

  /*
   * str2int
   *
   * This function creates an int array cypher of
   * numbers 0 to 26 for the given input string containing
   * letters in the english alphabet (a-z or A-Z) separated by
   * spaces.
   * (a-z or A-Z) are mapped to numbers 0 to 25.
   * spaces are mapped to number 26.
   *
   * Parameters:
   *   plainText: input String
   *   cypherText:  int array that will hold the transformed input cypher
   *   space: represents ascii value of space character
   *   cypherSpace: cypher number that replaces the space character
   *   asciiValueOfA: represents the ascii value of the character 'a'
   *   cypherTextIndex: int to index the cypherText array
   *   character: holds the ascii value of each character from the input string
   *
   * Return value: the transformed cypherText array of ints.
   */

  private static int[] str2int(String plainText) {
    final int space = 32;
    final int cypherSpace = 26;
    final int asciiValueOfA = 97;
    int[] cypherText = new int[plainText.length()];
    int cypherTextIndex = 0;
    // plainText string is converted to lowercase and then to an character array
    for (char c: plainText.toLowerCase().toCharArray()) {
      int character = (int) c;
      if (character == space) {
        cypherText[cypherTextIndex] = cypherSpace;
        cypherTextIndex++;
      } else {
        cypherText[cypherTextIndex] = character - asciiValueOfA;
        cypherTextIndex++;
      }
    }
    return cypherText;
  }

  public static void main(String[] args) {
    Scanner scannerObj = new Scanner(System.in);
    while (scannerObj.hasNextLine()){
      for (int x: str2int(scannerObj.nextLine())) {
        System.out.print(x+" ");
      }
      System.out.println();
    }
  }
}