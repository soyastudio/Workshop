package com.albertsons.edis.tools.aes;

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

    public static void main(String[] args) {
        String src = "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";
        System.out.println(decrypt(src, "EDISEncryptionKey".toLowerCase()));

    }

}
