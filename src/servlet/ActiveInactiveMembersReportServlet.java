package servlet;

import dao.MemberActivityReportDAO;
import model.MemberActivityReport;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ActiveInactiveMembersReportServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Get DB connection from servlet context or session (adjust as per your setup)
        Connection conn = (Connection) getServletContext().getAttribute("DBConnection");

        MemberActivityReportDAO dao = new MemberActivityReportDAO(conn);
        try {
            List<MemberActivityReport> reportList = dao.getActiveInactiveMembers();
            request.setAttribute("reportList", reportList);

            // Forward to JSP to display the report
            request.getRequestDispatcher("/WEB-INF/views/activeInactiveMembersReport.jsp")
                   .forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Error retrieving Active/Inactive Members report", e);
        }
    }
}
