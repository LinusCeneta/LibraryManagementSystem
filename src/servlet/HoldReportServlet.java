package servlet;

import dao.HoldReportDAO;
import model.HoldReport;
import utils.DBConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;

public class HoldReportServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String export = request.getParameter("export");

        try (Connection conn = DBConnection.getConnection()) {
            HoldReportDAO dao = new HoldReportDAO(conn);
            List<HoldReport> reportList = dao.getHoldStats();
            request.setAttribute("reportList", reportList);

            if ("csv".equalsIgnoreCase(export)) {
                response.setContentType("text/csv");
                response.setHeader("Content-Disposition", "attachment; filename=\"hold_report.csv\"");

                StringBuilder csv = new StringBuilder("Title,Hold Queue,Average Wait Days\n");
                for (HoldReport r : reportList) {
                    csv.append("\"").append(r.getTitle()).append("\",")
                       .append(r.getHoldQueue()).append(",")
                       .append(r.getAvgWaitDays()).append("\n");
                }
                response.getWriter().write(csv.toString());
            } else {
                request.getRequestDispatcher("/holdReport.jsp").forward(request, response);
            }
        } catch (Exception e) {
            throw new ServletException("Error generating hold report", e);
        }
    }
}
