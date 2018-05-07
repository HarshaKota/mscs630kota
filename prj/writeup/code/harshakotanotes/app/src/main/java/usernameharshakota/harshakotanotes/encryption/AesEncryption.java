package usernameharshakota.harshakotanotes.encryption;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/*
This class provides Encryption and Decryption using a 256-bit AES algorithm in CBC mode.
It returns the encrypted data in Base64 and decrypted cipher text.
 */
public class AesEncryption {

    public static String aesEncrypt(Context context, String plainText) throws Exception{

        // Password is fetched from the SharedPreferences, which was saved during initial
        // password setup
        SharedPreferences settings = context.getSharedPreferences("PREFS", 0);
        final String password = settings.getString("password", "");

        byte[] pText = plainText.getBytes();
        byte[] pKey  = password.getBytes("UTF-8");

        // Generate IV
        int ivSize = 16;
        byte[] iv = new byte[ivSize];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // Hashing the key
        MessageDigest hash = MessageDigest.getInstance("SHA-256");
        hash.update(pKey);
        SecretKeySpec secretKey = new SecretKeySpec(hash.digest(), "AES");

        // Encrypt
        Cipher eCipher = Cipher.getInstance("AES_256/CBC/PKCS7Padding");
        eCipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        byte[] encrypted = eCipher.doFinal(pText);

        // Combine IV to Cipher Message
        byte[] ivAndCipher = new byte[ivSize+encrypted.length];
        System.arraycopy(iv,0,ivAndCipher,0,ivSize);
        System.arraycopy(encrypted,0,ivAndCipher,ivSize,encrypted.length);


        return Base64.encodeToString(ivAndCipher,Base64.DEFAULT);
    }

    public static String aesDecrypt(Context context, String cipherText) throws Exception {

        // Password is fetched from the SharedPreferences, which was saved during initial
        // password setup
        SharedPreferences settings = context.getSharedPreferences("PREFS", 0);
        final String password = settings.getString("password", "");

        byte[] cText = Base64.decode(cipherText, Base64.DEFAULT);
        byte[] pKey  = password.getBytes("UTF-8");

        int ivSize = 16;

        //Extract IV from Cipher Text
        byte[] iv = new byte[ivSize];
        System.arraycopy(cText,0,iv,0,iv.length);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // Extract cipher text
        int cipherTextSize = cText.length - ivSize;
        byte[] cipher = new byte[cipherTextSize];
        System.arraycopy(cText,ivSize,cipher,0,cipherTextSize);

        // Hashed Key
        MessageDigest hash = MessageDigest.getInstance("SHA-256");
        hash.update(pKey);
        SecretKeySpec secretKey = new SecretKeySpec(hash.digest(), "AES");

        // Decrypt
        Cipher dCipher = Cipher.getInstance("AES_256/CBC/PKCS7Padding");
        dCipher.init(Cipher.DECRYPT_MODE,secretKey,ivSpec);
        byte[] decrypted = dCipher.doFinal(cipher);

        return new String(decrypted);
    }


}
