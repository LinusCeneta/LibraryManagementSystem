package servlet;

import dao.StaffTaskDAO;
import model.StaffTaskReport;
import utils.DBConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;

@WebServlet("/staffTasks")
public class StaffTaskServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int branchId = Integer.parseInt(request.getParameter("branchId"));

        try (Connection conn = DBConnection.getConnection()) {
            StaffTaskDAO dao = new StaffTaskDAO(conn);
            StaffTaskReport report = dao.getStaffTasks(branchId);

            request.setAttribute("report", report);
            request.getRequestDispatcher("/staffTasks.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error loading staff tasks.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }
}
