package servlet;

import dao.UserDAO;
import model.User;
import utils.CSRFTokenManager;
import utils.PasswordUtils;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;

public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Generate CSRF token and store in session
        String csrfToken = CSRFTokenManager.generateToken();
        request.getSession().setAttribute("csrfToken", csrfToken);

        // Set token in request so it can be used in JSP form
        request.setAttribute("csrfToken", csrfToken);
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // CSRF token validation
        String sessionToken = (String) request.getSession().getAttribute("csrfToken");
        String requestToken = request.getParameter("csrfToken");

        if (sessionToken == null || requestToken == null || !sessionToken.equals(requestToken)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF token");
            return;
        }

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        System.out.println("Login attempt - username: " + username);
        
        UserDAO userDAO = new UserDAO();
        User user = userDAO.findByUsername(username);
        
        if (user == null) {
            System.out.println("User not found in database");
            request.setAttribute("errorMessage", "Invalid username or password.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }
        
        System.out.println("Found user: " + user.getUsername());
        System.out.println("Stored hash: " + user.getPasswordHash());
        
        boolean passwordMatch = PasswordUtils.checkPassword(password, user.getPasswordHash());
        System.out.println("Password check result: " + passwordMatch);
        
        if (passwordMatch) {
            // Successful login logic
            HttpSession session = request.getSession();
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("role", user.getRole());
            session.setMaxInactiveInterval(30 * 60); // 30 minute session
            
            // Debug output
            System.out.println("Login successful - redirecting to dashboard");
            System.out.println("Context path: " + request.getContextPath());
            
            // Clear the CSRF token after successful login
            session.removeAttribute("csrfToken");
            
            // Redirect to dashboard
            response.sendRedirect(request.getContextPath() + "/dashboard.jsp");
        } else {
            System.out.println("Password verification failed");
            request.setAttribute("errorMessage", "Invalid username or password.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}