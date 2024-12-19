package com.example.app1;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {
    // Hash the password
    public static String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(12));
    }

    // Check the password
    public static boolean checkPassword(String plainTextPassword, String hashedPassword) {
        if (hashedPassword == null || !hashedPassword.startsWith("$2a$")) {
            throw new IllegalArgumentException("Invalid hashed password");
        }
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }

    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (hashedPassword == null || !hashedPassword.startsWith("$2a$")) {
            throw new IllegalArgumentException("Invalid hashed password");
        }
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
