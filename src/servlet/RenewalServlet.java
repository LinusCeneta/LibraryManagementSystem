package servlet;

import dao.LoanDAO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/RenewalServlet")
public class RenewalServlet extends HttpServlet {
    private LoanDAO loanDAO;

    @Override
    public void init() throws ServletException {
        try {
			loanDAO = new LoanDAO();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/renewalForm.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String loanId = request.getParameter("loanId");

            // Placeholder: call DAO to process renewal
            loanDAO.renewLoan(loanId);

            response.sendRedirect("renewalSuccess.jsp");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error processing renewal.");
            request.getRequestDispatcher("/renewalForm.jsp").forward(request, response);
        }
    }
}
