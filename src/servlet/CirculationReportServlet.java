package servlet;

import dao.LoanReportDAO;
import model.CirculationReport;
import utils.DBConnection;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;

public class CirculationReportServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String branch = request.getParameter("branch");
        String export = request.getParameter("export");
        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try (Connection conn = DBConnection.getConnection()) {
            LoanReportDAO dao = new LoanReportDAO(conn);

            Date startDate = sdf.parse(startDateStr);
            Date endDate = sdf.parse(endDateStr);

            List<CirculationReport> reports = dao.getCheckoutCounts(branch, startDate, endDate);

            if ("csv".equals(export)) {
                response.setContentType("text/csv");
                response.setHeader("Content-Disposition", "attachment; filename=circulation_report.csv");
                PrintWriter writer = response.getWriter();
                writer.println("Date,Checkout Count");
                for (CirculationReport report : reports) {
                    writer.printf("%s,%d%n", sdf.format(report.getLoanDate()), report.getCheckoutCount());
                }
                writer.flush();
                writer.close();
                return;
            }

            request.setAttribute("reports", reports);
            request.setAttribute("branch", branch);
            request.setAttribute("startDate", startDateStr);
            request.setAttribute("endDate", endDateStr);
            request.getRequestDispatcher("circulationReport.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error generating report: " + e.getMessage());
            request.getRequestDispatcher("circulationReport.jsp").forward(request, response);
        }
    }
}
