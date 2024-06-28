package com.machpay.affiliate.twilio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

public class VerificationCodeGenerator {

    private static final Logger logger = LoggerFactory.getLogger(VerificationCodeGenerator.class);
    private static final int MAX_VERIFICATION_CODE = 99999;

    private static final int MIN_VERIFICATION_CODE = 10000;

    private static final SecretKey secretKey = decodeSecretKey("AcmeSecret12AcmeSecret");
    private VerificationCodeGenerator() {
        throw new IllegalStateException("Utility class");
    }

    public static String generate() {
        Random rand = new SecureRandom();
        int code = rand.nextInt(MAX_VERIFICATION_CODE
                - MIN_VERIFICATION_CODE + 1) + MAX_VERIFICATION_CODE;

        return Integer.toString(code);
    }

    public static String generateEncryptedVerificationCode() {
        String token = generate();

        return encrypt(token);
    }

    public static String encrypt(String token) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(token.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error while encrypting token");

            return null;
        }
    }

    public static String decrypt(String encryptedCode) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedCode));

            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error while decrypting token");

            return null;
        }
    }

    private static SecretKey decodeSecretKey(String base64Key) {
        try {
            byte[] decodedKey = Base64.getDecoder().decode(base64Key);

            return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error while decoding secret key");

            return null;
        }
    }
}
