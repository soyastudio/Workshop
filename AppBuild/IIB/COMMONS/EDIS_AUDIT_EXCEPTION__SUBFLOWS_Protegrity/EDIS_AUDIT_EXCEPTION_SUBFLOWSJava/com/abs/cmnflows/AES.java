package com.abs.cmnflows;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.logging.Logger;

public class AES {
    private static Logger logger = Logger.getLogger(AES.class.getName());

    public static final String DIGEST_ALGORITHM_MD5 = "MD5";
    public static final String DIGEST_ALGORITHM_SHA1 = "SHA-1";
    public static final String DIGEST_ALGORITHM_SHA256 = "SHA-256";

    public static final String[] ALGORITHMS = {
            "AES/CBC/PKCS5Padding",
            "AES/ECB/PKCS5Padding",
            "DES/CBC/PKCS5Padding",
            "DES/ECB/PKCS5Padding",
            "DESede/CBC/PKCS5Padding",
            "DESede/ECB/PKCS5Padding",
            "RSA/ECB/PKCS1Padding",
            "RSA/ECB/OAEPWithSHA-1AndMGF1Padding",
            "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"
    };

    private static SecretKeySpec secretKey;
    private static byte[] key;

    private static void setKey(String secret) {
        MessageDigest sha = null;
        try {
            key = secret.getBytes("UTF-8");
            sha = MessageDigest.getInstance(DIGEST_ALGORITHM_SHA1);
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException e) {
            logger.warning(e.getMessage());

        } catch (UnsupportedEncodingException e) {
            logger.warning(e.getMessage());

        }
    }

    public static String encrypt(String message, String secret) {
        try {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(message.getBytes("UTF-8")));
        } catch (Exception e) {
            logger.warning(e.getMessage());
        }

        return null;
    }

    public static String decrypt(String strToDecrypt, String secret) {
        try {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            logger.warning(e.getMessage());
        }

        return null;
    }

}
