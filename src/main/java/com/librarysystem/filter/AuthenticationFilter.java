package com.librarysystem.filter;

import com.librarysystem.model.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

// This filter can complement web.xml security-constraints or act as primary auth check
// if not using container-managed security for everything.
@WebFilter("/*") // Check all requests
public class AuthenticationFilter implements Filter {

    private Set<String> publicPaths = new HashSet<>(Arrays.asList(
            "/user/login",
            "/user/register",
            "/user/forgotPassword",
            "/user/resetPassword",
            "/css/",    // Allow CSS
            "/js/",     // Allow JavaScript
            "/images/"  // Allow images
    ));

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code, if any
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false); // Don't create session if not exists

        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        // Allow access to public paths
        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        boolean loggedIn = (session != null && session.getAttribute("user") != null);

        if (!loggedIn) {
            // User is not logged in, redirect to login page
            // Save the original requested path to redirect after successful login
            if (session == null) session = httpRequest.getSession(true); // Create session to store target path
            session.setAttribute("requestedPath", path + (httpRequest.getQueryString() != null ? "?" + httpRequest.getQueryString() : ""));
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/user/login?message=loginRequiredForResource");
            return;
        }

        // User is logged in, proceed with role-based checks if necessary for specific paths
        // This filter primarily ensures authentication. Authorization (role checks) can be
        // done in servlets or more specific filters, or via web.xml <security-constraint>.

        // Example: Programmatic role check for admin paths (if not fully covered by web.xml)
        if (path.startsWith("/user/admin/") || path.startsWith("/admin/")) { // Generalizing admin paths
            User user = (User) session.getAttribute("user");
            if (user == null || user.getRole() == null || !"ROLE_ADMIN".equals(user.getRole().getRoleName())) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Admin role required.");
                return;
            }
        }

        // Example: Programmatic role check for staff paths
        if (path.startsWith("/acquisition/") || path.startsWith("/inventory/")) {
            User user = (User) session.getAttribute("user");
            if (user == null || user.getRole() == null ||
                (!"ROLE_STAFF".equals(user.getRole().getRoleName()) && !"ROLE_ADMIN".equals(user.getRole().getRoleName()))) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Staff or Admin role required.");
                return;
            }
        }


        // If execution reaches here, user is authenticated (and passed any programmatic role checks here)
        chain.doFilter(request, response);
    }

    private boolean isPublicPath(String path) {
        if (path.equals("/") || path.equals("/index.jsp")) return true; // Allow homepage
        for (String publicPrefix : publicPaths) {
            if (path.startsWith(publicPrefix)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void destroy() {
        // Cleanup code, if any
    }
}
