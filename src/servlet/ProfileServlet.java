package servlet;

import model.User;
import utils.CSRFTokenManager;
import utils.PasswordUtils;
import dao.UserDAO;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;

public class ProfileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        CSRFTokenManager.setToken(request.getSession());
        request.getRequestDispatcher("profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Validate CSRF token
        if (!CSRFTokenManager.isValid(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF token.");
            return;
        }

        // Retrieve user from session
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not logged in.");
            return;
        }

        // Fetch user from DB
        UserDAO userDAO = new UserDAO();
        User user = userDAO.findById(userId);

        if (user == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found.");
            return;
        }

        // Update fields
        String newEmail = request.getParameter("email");
        String newPassword = request.getParameter("password");

        user.setEmail(newEmail);

        if (newPassword != null && !newPassword.isEmpty()) {
            String hashedPassword = PasswordUtils.hashPassword(newPassword);
            user.setPasswordHash(hashedPassword);
        }

        // Save changes
        userDAO.updateUser(user);

        // Refresh CSRF token after successful update
        CSRFTokenManager.setToken(request.getSession());

        request.setAttribute("message", "Profile updated successfully.");
        request.setAttribute("user", user);

        request.getRequestDispatcher("profile.jsp").forward(request, response);
    }
}
