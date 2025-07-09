package servlet;

import dao.AlertsNotificationsDAO;
import model.AlertNotification;
import utils.DBConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;

public class AlertsNotificationsServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int branchId = Integer.parseInt(request.getParameter("branchId"));

        try (Connection conn = DBConnection.getConnection()) {
            AlertsNotificationsDAO dao = new AlertsNotificationsDAO(conn);
            List<AlertNotification> alerts = dao.getAlerts(branchId);

            request.setAttribute("alerts", alerts);
            request.getRequestDispatcher("/WEB-INF/views/alertsNotifications.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error loading alerts and notifications.");
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
}
