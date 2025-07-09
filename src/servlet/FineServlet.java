package servlet;

import dao.FineDAO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

public class FineServlet extends HttpServlet {
    private FineDAO fineDAO;

    @Override
    public void init() throws ServletException {
        try {
            fineDAO = new FineDAO();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException("Database connection error in FineServlet", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Display the fine form
        request.getRequestDispatcher("/fineForm.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String memberId = request.getParameter("memberId");
        String loanId = request.getParameter("loanId");
        String amountStr = request.getParameter("amount");

        try {
            // Basic validation
            if (memberId == null || loanId == null || amountStr == null ||
                memberId.isEmpty() || loanId.isEmpty() || amountStr.isEmpty()) {
                request.setAttribute("errorMessage", "All fields are required.");
                request.getRequestDispatcher("/fineForm.jsp").forward(request, response);
                return;
            }

            double amount = Double.parseDouble(amountStr);

            // Record the fine
            fineDAO.recordFine(memberId, loanId, amount);

            // Redirect to success page
            response.sendRedirect("fineSuccess.jsp");
        } catch (NumberFormatException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Invalid amount format.");
            request.getRequestDispatcher("/fineForm.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An unexpected error occurred.");
            request.getRequestDispatcher("/fineForm.jsp").forward(request, response);
        }
    }
}
