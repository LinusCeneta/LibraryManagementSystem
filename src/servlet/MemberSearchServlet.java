package servlet;

import dao.MemberDAO;
import model.Member;
import utils.CSRFTokenManager;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

public class MemberSearchServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!CSRFTokenManager.isValid(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF token");
            return;
        }

        String memberId = request.getParameter("memberId");
        if (memberId != null && !memberId.isEmpty()) {
            // Direct search by memberId - redirect to profile if found
            MemberDAO memberDAO = new MemberDAO();
            Member member = memberDAO.getMemberById(memberId);
            
            if (member != null) {
            	response.sendRedirect(request.getContextPath() + "/member-profile?memberId=" + memberId);
                return;
            } else {
                request.setAttribute("error", "MemberNotFound");
            }
        }

        // Fall back to keyword search if memberId not found or not provided
        String keyword = request.getParameter("keyword");
        if (keyword != null && !keyword.isEmpty()) {
            MemberDAO memberDAO = new MemberDAO();
            List<Member> members = memberDAO.searchMembers(keyword);
            request.setAttribute("members", members);
        }

        CSRFTokenManager.setToken(request.getSession());
        request.getRequestDispatcher("memberSearch.jsp").forward(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        CSRFTokenManager.setToken(request.getSession());
        request.getRequestDispatcher("memberSearch.jsp").forward(request, response);
    }
}