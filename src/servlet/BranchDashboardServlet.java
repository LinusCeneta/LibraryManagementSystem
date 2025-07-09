package servlet;

import dao.BranchDashboardDAO;
import model.BranchOverviewReport;
import utils.DBConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;

public class BranchDashboardServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int branchId = Integer.parseInt(request.getParameter("branchId"));
        int lowInventoryThreshold = 3; 

        try (Connection conn = DBConnection.getConnection()) {
            BranchDashboardDAO dao = new BranchDashboardDAO(conn);
            BranchOverviewReport report = dao.getBranchOverview(branchId, lowInventoryThreshold);

            request.setAttribute("report", report);
            request.getRequestDispatcher("/branchDashboard.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error retrieving branch dashboard data.");
            request.getRequestDispatcher("/rror.jsp").forward(request, response);
        }
    }
}
