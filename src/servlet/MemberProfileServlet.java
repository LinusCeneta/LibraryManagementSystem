package servlet;

import dao.MemberDAO;
import model.Member;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MemberProfileServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(MemberProfileServlet.class.getName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String memberId = request.getParameter("memberId");
        
        if (memberId == null || memberId.isEmpty()) {
            response.sendRedirect("memberSearch.jsp?error=NoMemberIdProvided");
            return;
        }

        try {
            MemberDAO memberDAO = new MemberDAO();
            Member member = memberDAO.getMemberById(memberId);

            if (member != null) {
                request.setAttribute("member", member);
                request.getRequestDispatcher("/memberProfile.jsp").forward(request, response);
            } else {
                response.sendRedirect("memberSearch.jsp?error=MemberNotFound&memberId=" + memberId);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading member profile", e);
            response.sendRedirect("memberSearch.jsp?error=SystemError");
        }
    }
}
