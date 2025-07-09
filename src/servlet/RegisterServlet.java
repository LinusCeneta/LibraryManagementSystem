package servlet;

import model.User;
import dao.UserDAO;
import utils.PasswordUtils;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.UUID;

public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Show register.jsp with CSRF token
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String csrfToken = UUID.randomUUID().toString();
        request.getSession().setAttribute("csrfToken", csrfToken);
        request.getRequestDispatcher("register.jsp").forward(request, response);
    }

    // Handle form submission
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // CSRF token validation (existing code remains)
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        String role = request.getParameter("role");

        // Enhanced validation
        if (username == null || password == null || email == null || role == null ||
            username.trim().isEmpty() || password.trim().isEmpty() || 
            email.trim().isEmpty() || role.trim().isEmpty()) {
            
            request.setAttribute("error", "All fields are required.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        // Check if username already exists
        UserDAO userDAO = new UserDAO();
        if (userDAO.usernameExists(username)) {
            request.setAttribute("error", "Username already exists.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
         }

        try {
            String hashedPassword = PasswordUtils.hashPassword(password);
            User user = new User(username, hashedPassword, email, role);

            boolean success = userDAO.registerUser(user);
            
            if (success) {
                request.getSession().removeAttribute("csrfToken");
                response.sendRedirect(request.getContextPath() + "/login.jsp");
            } else {
                request.setAttribute("error", "Registration failed. Please try again.");
                request.getRequestDispatcher("register.jsp").forward(request, response);
            }
        } catch (Exception e) {
            System.err.println("Registration error: " + e.getMessage());
            request.setAttribute("error", "System error during registration.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
        }
    }
}