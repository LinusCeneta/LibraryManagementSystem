package servlet;

import dao.MemberDAO;
import utils.CSRFTokenManager;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;

public class MembershipRenewalServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!CSRFTokenManager.isValid(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF token");
            return;
        }

        try {
            int memberId = Integer.parseInt(request.getParameter("memberId"));

            MemberDAO memberDAO = new MemberDAO();
            boolean success = memberDAO.renewMembership(memberId);

            if (success) {
                request.setAttribute("successMessage", "Membership renewed successfully.");
            } else {
                request.setAttribute("errorMessage", "Failed to renew membership. Please try again.");
            }
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid member ID.");
        }

        CSRFTokenManager.setToken(request.getSession());
        request.getRequestDispatcher("membershipRenewal.jsp").forward(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        CSRFTokenManager.setToken(request.getSession());
        request.getRequestDispatcher("membershipRenewal.jsp").forward(request, response);
    }
}