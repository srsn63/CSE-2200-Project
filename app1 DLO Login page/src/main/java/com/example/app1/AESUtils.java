package com.example.app1;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.file.*;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class AESUtils {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";

    // Load the existing key or generate a new one if it doesn't exist
    public static SecretKey loadKey() throws Exception {
        Path keyPath = Paths.get("secret.key");
        if (!Files.exists(keyPath)) {
            SecretKey key = generateKey();
            saveKey(key, keyPath);
            return key;
        }
        byte[] keyBytes = Files.readAllBytes(keyPath);
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }

    // Generate a new AES key
    public static SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(128); // 128-bit AES
        return keyGen.generateKey();
    }

    // Save the key to a file
    private static void saveKey(SecretKey key, Path path) throws Exception {
        Files.write(path, key.getEncoded(), StandardOpenOption.CREATE);
    }

    // Encrypt a string
    public static String encrypt(String input, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(input.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // Decrypt a string
    public static String decrypt(String encryptedInput, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedInput);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes, "UTF-8");
    }
}
