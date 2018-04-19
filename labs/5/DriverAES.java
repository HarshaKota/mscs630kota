/*
 * file: DriverAES.java
 * author: Harsha Kota
 * course: MSCS 630
 * assignment: Lab 5
 * due date: Wednesday, April 18, 2018
 * version: 1.0
 *
 * This file contains the declaration of the
 * DriverAES class.
 */

import java.util.Scanner;

/*
 * DriverAES
 *
 * This class tests the implementation of AES algorithm in the AESCipher class
 */
public class DriverAES {

  /*
   * main
   *
   * This function takes as input, a 128-bit key as a Hexadecimal string of length 32 and plain text in the same format
   * and calls the AES function from the AESCipher class and prints the Encrypted message
   *
   * Parameters:
   *   key:          128-bit Hexadecimal key
   *   plainText:    128-bit Plain text
   *   cipherText:   Holds the Encrypted message returned by the AES method
   *
   */
  public static void main(String[] args) {
    Scanner scan = new Scanner(System.in);
    String key = scan.nextLine();
    String plainText = scan.nextLine();
    scan.close();

    String cipherText = AESCipher.AES(plainText, key);

    System.out.println(cipherText);

  }
}
