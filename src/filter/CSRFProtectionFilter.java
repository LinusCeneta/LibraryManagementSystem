package filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class CSRFProtectionFilter implements Filter {

    public void init(FilterConfig filterConfig) throws ServletException {
        // Optional init
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        if ("POST".equalsIgnoreCase(httpRequest.getMethod())) {
            String sessionToken = (String) httpRequest.getSession().getAttribute("csrfToken");
            String requestToken = request.getParameter("csrfToken");

            if (sessionToken == null || !sessionToken.equals(requestToken)) {
                // Invalid or missing token
                request.setAttribute("errorMessage", "Invalid CSRF token");
                request.getRequestDispatcher("/WEB-INF/error403.jsp").forward(request, response);
                return;
            }
        }

        chain.doFilter(request, response);
    }

    public void destroy() {
        // Optional cleanup
    }
}
