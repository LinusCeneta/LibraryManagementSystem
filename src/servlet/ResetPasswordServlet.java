package servlet;

import dao.UserDAO;
import model.User;
import utils.CSRFTokenManager;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;

public class ResetPasswordServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String token = request.getParameter("token");
        String userIdParam = request.getParameter("userId");

        if (token == null || userIdParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid reset link.");
            return;
        }

        int userId = Integer.parseInt(userIdParam);
        String sessionToken = (String) request.getSession().getAttribute("resetToken_" + userId);

        if (sessionToken == null || !sessionToken.equals(token)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or expired token.");
            return;
        }

        request.setAttribute("userId", userId);
        request.setAttribute("token", token);
        CSRFTokenManager.setToken(request.getSession());
        request.getRequestDispatcher("resetPassword.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!CSRFTokenManager.isValid(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF token.");
            return;
        }

        String userIdParam = request.getParameter("userId");
        String token = request.getParameter("token");
        String newPassword = request.getParameter("newPassword");

        if (userIdParam == null || token == null || newPassword == null || newPassword.isEmpty()) {
            request.setAttribute("error", "Invalid request.");
            CSRFTokenManager.setToken(request.getSession());
            request.getRequestDispatcher("resetPassword.jsp").forward(request, response);
            return;
        }

        int userId = Integer.parseInt(userIdParam);
        String sessionToken = (String) request.getSession().getAttribute("resetToken_" + userId);

        if (sessionToken == null || !sessionToken.equals(token)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or expired token.");
            return;
        }

        UserDAO userDAO = new UserDAO();
        boolean updated = userDAO.updatePassword(userId, newPassword);

        if (updated) {
            request.getSession().removeAttribute("resetToken_" + userId);
            request.setAttribute("message", "Password reset successfully. Please login with your new password.");
            response.sendRedirect("login.jsp");
        } else {
            request.setAttribute("error", "Failed to reset password.");
            CSRFTokenManager.setToken(request.getSession());
            request.getRequestDispatcher("resetPassword.jsp").forward(request, response);
        }
    }
}
