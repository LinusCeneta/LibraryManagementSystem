package com.librarysystem.util;

// Using Spring Security's BCryptPasswordEncoder.
// Add dependency: org.springframework.security:spring-security-crypto
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtil {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * Hashes a plain text password using BCrypt.
     *
     * @param plainPassword The password to hash.
     * @return The BCrypt hashed password.
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty.");
        }
        return encoder.encode(plainPassword);
    }

    /**
     * Checks if a plain text password matches a stored BCrypt hashed password.
     *
     * @param plainPassword    The plain text password to check.
     * @param hashedPassword   The stored BCrypt hashed password.
     * @return True if the passwords match, false otherwise.
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null || hashedPassword.isEmpty()) {
            return false; // Or throw IllegalArgumentException depending on desired handling
        }
        return encoder.matches(plainPassword, hashedPassword);
    }

    /**
     * Validates password strength.
     * Customize criteria as needed.
     * - Minimum 8 characters
     * - At least one uppercase letter
     * - At least one lowercase letter
     * - At least one digit
     * - At least one special character (e.g., !@#$%^&*)
     *
     * @param password The password to validate.
     * @return True if the password meets the criteria, false otherwise.
     */
    public static boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean hasUpper = false, hasLower = false, hasDigit = false, hasSpecial = false;
        String specialChars = "!@#$%^&*()_+-=[]{}|;':,./<>?";

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if (specialChars.indexOf(c) != -1) hasSpecial = true;
        }
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    public static String getPasswordStrengthCriteriaMessage() {
        return "Password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one digit, and one special character (e.g., !@#$%^&*).";
    }


    // Main method for simple testing
    public static void main(String[] args) {
        String myPassword = "Password123!";

        // Test hashing
        String hashedPassword = hashPassword(myPassword);
        System.out.println("Original: " + myPassword);
        System.out.println("Hashed: " + hashedPassword);
        System.out.println("Hashed length: " + hashedPassword.length()); // BCrypt hashes are typically 60 chars

        // Test checking
        System.out.println("Check correct password: " + checkPassword(myPassword, hashedPassword)); // true
        System.out.println("Check incorrect password (case): " + checkPassword("password123!", hashedPassword)); // false
        System.out.println("Check incorrect password (char): " + checkPassword("Password123?", hashedPassword)); // false

        // Test strength
        System.out.println("\nPassword strength tests:");
        String p1 = "short";
        String p2 = "longenoughbutnoupper";
        String p3 = "LongEnoughNoDigit!";
        String p4 = "LongEnough123noSpecial";
        String p5 = "ValidPass1!";
        String p6 = "anotherValidPass123$";

        System.out.println("'" + p1 + "' is strong: " + isPasswordStrong(p1)); // false
        System.out.println("'" + p2 + "' is strong: " + isPasswordStrong(p2)); // false
        System.out.println("'" + p3 + "' is strong: " + isPasswordStrong(p3)); // false
        System.out.println("'" + p4 + "' is strong: " + isPasswordStrong(p4)); // false
        System.out.println("'" + p5 + "' is strong: " + isPasswordStrong(p5)); // true
        System.out.println("'" + p6 + "' is strong: " + isPasswordStrong(p6)); // true

        System.out.println(getPasswordStrengthCriteriaMessage());

        // Test with empty/null
        // System.out.println(hashPassword(null)); // throws IllegalArgumentException
        System.out.println("Check null plain password: " + checkPassword(null, hashedPassword)); // false
        System.out.println("Check null hashed password: " + checkPassword(myPassword, null)); // false
    }
}
