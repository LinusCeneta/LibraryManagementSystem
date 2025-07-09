package servlet;

import dao.MemberActivityReportDAO;
import model.MemberActivityReport;
import utils.DBConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.util.List;

public class FineReportServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");
        String export = request.getParameter("export");

        try (Connection conn = DBConnection.getConnection()) {
            MemberActivityReportDAO dao = new MemberActivityReportDAO(conn);
            Date startDate = (startDateStr != null && !startDateStr.isEmpty()) ? Date.valueOf(startDateStr) : null;
            Date endDate = (endDateStr != null && !endDateStr.isEmpty()) ? Date.valueOf(endDateStr) : null;

            if (startDate == null || endDate == null) {
                request.setAttribute("error", "Start and end dates are required.");
                request.getRequestDispatcher("/fineReport.jsp").forward(request, response);
                return;
            }

            List<MemberActivityReport> reportList = dao.getFineRevenue(startDate, endDate);
            request.setAttribute("reportList", reportList);

            if ("csv".equalsIgnoreCase(export)) {
                response.setContentType("text/csv");
                response.setHeader("Content-Disposition", "attachment; filename=\"fine_report.csv\"");

                StringBuilder csv = new StringBuilder("Member ID,Member Name,Total Fines,Average Fine\n");
                for (MemberActivityReport r : reportList) {
                    csv.append(r.getMemberId()).append(",")
                       .append(r.getMemberName()).append(",")
                       .append(r.getTotalFines()).append(",")
                       .append(r.getAverageFine()).append("\n");
                }
                response.getWriter().write(csv.toString());
            } else {
                request.getRequestDispatcher("/fineReport.jsp").forward(request, response);
            }
        } catch (Exception e) {
            throw new ServletException("Error generating fine report", e);
        }
    }
}
