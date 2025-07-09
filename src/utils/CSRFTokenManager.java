package utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.SecureRandom;
import java.math.BigInteger;

public class CSRFTokenManager {
    private static final String CSRF_TOKEN = "csrfToken";

    public static String generateToken() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }

    public static void setToken(HttpSession session) {
        String token = generateToken();
        session.setAttribute(CSRF_TOKEN, token);
    }

    public static String getToken(HttpSession session) {
        Object token = session.getAttribute(CSRF_TOKEN);
        return token != null ? token.toString() : null;
    }

    public static boolean isValid(HttpServletRequest request) {
        String sessionToken = getToken(request.getSession());
        String requestToken = request.getParameter("csrfToken");
        return sessionToken != null && sessionToken.equals(requestToken);
    }
}
