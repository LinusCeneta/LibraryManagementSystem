package com.librarysystem.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@WebFilter("/*") // Apply to all requests
public class CSRFTokenFilter implements Filter {

    // Actions that don't need CSRF protection (typically GET requests for viewing pages)
    // Or specific POST endpoints if they are designed to be exempt (e.g. an API endpoint with token auth)
    private static final Set<String> EXCLUDED_METHODS = new HashSet<>();
    static {
        EXCLUDED_METHODS.add("GET");
        EXCLUDED_METHODS.add("HEAD");
        EXCLUDED_METHODS.add("OPTIONS");
        // Add any other specific paths or methods if needed, e.g. "/api/public/*"
    }

    public static final String CSRF_TOKEN_SESSION_ATTR = "csrfToken_session";
    public static final String CSRF_TOKEN_REQUEST_PARAM = "_csrf";


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code, if any
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession();

        // Generate CSRF token if not present in session or if it's a new session
        String sessionToken = (String) session.getAttribute(CSRF_TOKEN_SESSION_ATTR);
        if (sessionToken == null) {
            sessionToken = UUID.randomUUID().toString();
            session.setAttribute(CSRF_TOKEN_SESSION_ATTR, sessionToken);
        }

        // Make the token available as a request attribute for JSPs to use in forms
        httpRequest.setAttribute(CSRF_TOKEN_REQUEST_PARAM, sessionToken); // For easily accessing in JSP e.g. ${ _csrf }

        if (EXCLUDED_METHODS.contains(httpRequest.getMethod().toUpperCase())) {
            // For GET, HEAD, OPTIONS requests, just proceed
            chain.doFilter(request, response);
        } else {
            // For POST, PUT, DELETE, etc., validate the token
            String requestToken = httpRequest.getParameter(CSRF_TOKEN_REQUEST_PARAM);

            if (sessionToken.equals(requestToken)) {
                // Token matches, proceed with the request
                chain.doFilter(request, response);
            } else {
                // Token mismatch - CSRF attack suspected or session issue
                System.err.println("CSRF Token Mismatch: Session Token='" + sessionToken + "', Request Token='" + requestToken + "' for URI: " + httpRequest.getRequestURI());
                session.removeAttribute(CSRF_TOKEN_SESSION_ATTR); // Invalidate current session token
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF Token. Please try again.");
                // Optionally, log this attempt more formally
            }
        }
    }

    @Override
    public void destroy() {
        // Cleanup code, if any
    }

    /**
     * Utility method to add CSRF token field to forms in JSPs.
     * Example usage in JSP:
     * <form method="post" action="...">
     *     <%= com.librarysystem.filter.CSRFTokenFilter.getCSRFTokenField(request) %>
     *     <!-- other form fields -->
     *     <button type="submit">Submit</button>
     * </form>
     */
    public static String getCSRFTokenField(HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(CSRF_TOKEN_SESSION_ATTR);
        if (token == null) { // Should not happen if filter runs first, but as a safeguard
             token = UUID.randomUUID().toString();
             request.getSession().setAttribute(CSRF_TOKEN_SESSION_ATTR, token);
        }
        return "<input type=\"hidden\" name=\"" + CSRF_TOKEN_REQUEST_PARAM + "\" value=\"" + token + "\">";
    }
}
