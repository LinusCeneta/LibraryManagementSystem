package servlet;

import dao.HoldDAO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

public class HoldServlet extends HttpServlet {
    private HoldDAO holdDAO;

    @Override
    public void init() throws ServletException {
        try {
            holdDAO = new HoldDAO();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException("Database connection error in HoldServlet", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Display the hold form
        request.getRequestDispatcher("/holdForm.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String memberId = request.getParameter("memberId");
        String title = request.getParameter("title");

        try {
            // Basic validation
            if (memberId == null || title == null || memberId.isEmpty() || title.isEmpty()) {
                request.setAttribute("errorMessage", "All fields are required.");
                request.getRequestDispatcher("/holdForm.jsp").forward(request, response);
                return;
            }

            // Place the hold
            holdDAO.placeHold(memberId, title);

            // Redirect to success page
            response.sendRedirect("holdSuccess.jsp");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An unexpected error occurred.");
            request.getRequestDispatcher("/holdForm.jsp").forward(request, response);
        }
    }
}
