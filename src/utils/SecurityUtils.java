package utils;

import java.security.SecureRandom;
import java.util.regex.Pattern;

import org.mindrot.jbcrypt.BCrypt;

public class SecurityUtils {
    private static final SecureRandom random = new SecureRandom();
    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    
    // Regex patterns (adjust as needed)
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^\\+?[0-9\\s-]{10,15}$");

    /**
     * Generates a secure random ID of specified length
     */
    public static String generateRandomId(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHANUMERIC.charAt(random.nextInt(ALPHANUMERIC.length())));
        }
        return sb.toString();
    }

    /**
     * Validates email format against RFC 5322 (simplified)
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validates international phone numbers (basic format)
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * Checks if a string is safe for SQL LIKE clauses (escapes % and _)
     */
    public static String sanitizeForLike(String input) {
        if (input == null) return null;
        return input.replace("%", "\\%").replace("_", "\\_");
    }

    /**
     * Basic password strength checker (adjust rules as needed)
     */
    public static boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) return false;
        
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            if (Character.isLowerCase(c)) hasLower = true;
            if (Character.isDigit(c)) hasDigit = true;
            if ("!@#$%^&*()-_=+[]{}|;:'\",.<>/?".indexOf(c) >= 0) hasSpecial = true;
        }
        
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    /**
     * Sanitizes file paths to prevent directory traversal
     */
    public static String sanitizeFilePath(String path) {
        if (path == null) return null;
        return path.replaceAll("[^a-zA-Z0-9./_-]", "");
    }
    
    public class PasswordUtils {
        public static String hashPassword(String plainPassword) {
            return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
        }
        
        public static boolean checkPassword(String plainPassword, String hashedPassword) {
            try {
                return BCrypt.checkpw(plainPassword, hashedPassword);
            } catch (Exception e) {
                return false;
            }
        }
    }
}