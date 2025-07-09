package servlet;

import dao.InventoryReportDAO;
import model.InventoryReport;
import utils.DBConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.util.List;

@WebServlet("/inventoryReport")
public class InventoryReportServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");
        String category = request.getParameter("category");
        String export = request.getParameter("export");

        try (Connection conn = DBConnection.getConnection()) {
            InventoryReportDAO dao = new InventoryReportDAO(conn);
            Date startDate = (startDateStr != null && !startDateStr.isEmpty()) ? Date.valueOf(startDateStr) : null;
            Date endDate = (endDateStr != null && !endDateStr.isEmpty()) ? Date.valueOf(endDateStr) : null;

            if (startDate == null || endDate == null) {
                request.setAttribute("error", "Start and end dates are required.");
                request.getRequestDispatcher("/inventoryReport.jsp").forward(request, response);
                return;
            }

            List<InventoryReport> reportList = dao.getNewAcquisitions(startDate, endDate, category);
            request.setAttribute("reportList", reportList);

            if ("csv".equalsIgnoreCase(export)) {
                // CSV export logic here
                response.setContentType("text/csv");
                response.setHeader("Content-Disposition", "attachment; filename=\"inventory_report.csv\"");

                StringBuilder csv = new StringBuilder("Date Added,Item Count,Total Cost\n");
                for (InventoryReport r : reportList) {
                    csv.append(r.getDateAdded()).append(",")
                       .append(r.getItemCount()).append(",")
                       .append(r.getTotalCost()).append("\n");
                }
                response.getWriter().write(csv.toString());
            } else {
                request.getRequestDispatcher("/inventoryReport.jsp").forward(request, response);
            }
        } catch (Exception e) {
            throw new ServletException("Error generating inventory report", e);
        }
    }
}
