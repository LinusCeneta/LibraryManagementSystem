package servlet;

import dao.UserDAO;
import model.User;
import utils.CSRFTokenManager;
import utils.EmailUtils;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.UUID;

public class ForgotPasswordServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!CSRFTokenManager.isValid(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF token");
            return;
        }

        String email = request.getParameter("email");
        if (email == null || email.isEmpty()) {
            request.setAttribute("error", "Please enter a valid email address.");
            CSRFTokenManager.setToken(request.getSession());
            request.getRequestDispatcher("forgotPassword.jsp").forward(request, response);
            return;
        }

        UserDAO userDAO = new UserDAO();
        User user = userDAO.findByEmail(email);

        if (user != null) {
            // Generate a secure token (UUID for simplicity)
            String resetToken = UUID.randomUUID().toString();

            // Store this token temporarily in session (or in DB with expiry, recommended for production)
            request.getSession().setAttribute("resetToken_" + user.getId(), resetToken);

            // Send reset email
            String resetLink = request.getRequestURL().toString().replace("ForgotPasswordServlet", "ResetPasswordServlet")
                                + "?token=" + resetToken + "&userId=" + user.getId();

            String emailContent = "Dear " + user.getUsername() + ",\n\n"
                    + "You requested a password reset. Click the link below to reset your password:\n"
                    + resetLink + "\n\n"
                    + "If you did not request this, please ignore this email.";

            EmailUtils.sendEmail(email, "Password Reset Request", emailContent);
        }

        request.setAttribute("message", "If the email exists in our system, password reset instructions have been sent.");
        CSRFTokenManager.setToken(request.getSession());
        request.getRequestDispatcher("forgotPassword.jsp").forward(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        CSRFTokenManager.setToken(request.getSession());
        request.getRequestDispatcher("forgotPassword.jsp").forward(request, response);
    }
}
