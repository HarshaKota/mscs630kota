/*
 * file: DriverAES.java
 * author: Harsha Kota
 * course: MSCS 630
 * assignment: Lab 4
 * due date: Wednesday, April 4, 2018
 * version: 1.0
 *
 * This file contains the declaration of the
 * DriverAES class.
 */

package lab4;

import java.util.Scanner;

/*
 * DriverAES
 *
 * This class tests the implementation of AES round keys generation in the AESCipher class
 */
public class DriverAES {

  /*
   * main
   *
   * This function takes an input 128-bit key as a Hexadecimal string of length 32 and calls the aesRoundKeys function
   * from the AESCipher class and prints the Original key and all round keys as Hexadecimal strings.
   *
   * Parameters:
   *   key:          128-bit Hexadecimal key
   *   roundKeysHex: Holds all the keys returned from the aesRoundKeys function including the original key
   *
   */
  public static void main(String[] args) {
    Scanner scan = new Scanner(System.in);
    String key = scan.nextLine();
    scan.close();

    String[] roundKeysHex = AESCipher.aesRoundKeys(key);

    for (String s: roundKeysHex){
      System.out.println(s);
    }
  }
}
