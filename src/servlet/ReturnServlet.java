package servlet;

import dao.LoanDAO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/ReturnServlet")
public class ReturnServlet extends HttpServlet {
    private LoanDAO loanDAO;

    @Override
    public void init() throws ServletException {
        try {
            loanDAO = new LoanDAO();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/returnForm.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String copyId = request.getParameter("copyId");

            loanDAO.processReturn(copyId);

            response.sendRedirect("returnSuccess.jsp");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error processing return.");
            request.getRequestDispatcher("/returnForm.jsp").forward(request, response);
        }
    }
}
