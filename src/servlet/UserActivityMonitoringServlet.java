package servlet;

import dao.UserActivityMonitoringDAO;
import model.UserActivity;
import utils.DBConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/userActivityMonitoring")
public class UserActivityMonitoringServlet extends HttpServlet {
	// In UserActivityMonitoringServlet.java, modify the doGet method:
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
	    Connection conn = null;
	    try {
	        conn = DBConnection.getConnection();
	        UserActivityMonitoringDAO dao = new UserActivityMonitoringDAO(conn);

	        List<UserActivity> activeSessions = dao.getActiveSessions();
	        List<UserActivity> recentActions = dao.getRecentActions();
	        List<UserActivity> suspiciousActivities = dao.getSuspiciousActivities();

	        request.setAttribute("activeSessions", activeSessions);
	        request.setAttribute("recentActions", recentActions);
	        request.setAttribute("suspiciousActivities", suspiciousActivities);

	        request.getRequestDispatcher("/userActivityMonitoring.jsp").forward(request, response);
	        conn.commit(); // Explicitly commit before closing
	    } catch (Exception e) {
	        if (conn != null) {
	            try { conn.rollback(); } catch (SQLException ex) {}
	        }
	        e.printStackTrace();
	        request.setAttribute("errorMessage", "Error loading user activity data.");
	        request.getRequestDispatcher("/login.jsp").forward(request, response);
	    } finally {
	        if (conn != null) {
	            try { 
	                conn.setAutoCommit(true); // Reset auto-commit
	                conn.close(); 
	            } catch (SQLException e) {}
	        }
	    }
	}
    
    public boolean logActivity(UserActivity activity) {
        String sql = "INSERT INTO UserActivities (username, session_id, action, timestamp, ip_address, user_agent) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, activity.getUsername());
            stmt.setString(2, activity.getSessionId());
            stmt.setString(3, activity.getAction());
            stmt.setTimestamp(4, activity.getTimestamp());
            stmt.setString(5, activity.getIpAddress());
            stmt.setString(6, activity.getUsername());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        
        


    }
}
