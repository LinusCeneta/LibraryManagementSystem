package servlet;

import dao.LoanDAO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/LoanServlet")
public class LoanServlet extends HttpServlet {
    private LoanDAO loanDAO;

    @Override
    public void init() throws ServletException {
        try {
            loanDAO = new LoanDAO();
        } catch (SQLException e) {
            throw new ServletException("Failed to initialize LoanDAO", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/loanForm.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String memberId = request.getParameter("memberId");
            String copyId = request.getParameter("copyId");
            String staffId = request.getParameter("staffId");

            if (memberId == null || memberId.isEmpty() || copyId == null || copyId.isEmpty()) {
                request.setAttribute("errorMessage", "Member ID and Copy ID are required.");
                request.getRequestDispatcher("/loanForm.jsp").forward(request, response);
                return;
            }

            loanDAO.issueLoan(memberId, copyId, staffId);

            response.sendRedirect("loanSuccess.jsp");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error issuing loan.");
            request.getRequestDispatcher("/loanForm.jsp").forward(request, response);
        }
    }
}
