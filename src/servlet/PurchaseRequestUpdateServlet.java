package servlet;

import dao.PurchaseRequestDAO;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/purchase-request-update")
public class PurchaseRequestUpdateServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(PurchaseRequestUpdateServlet.class.getName());
    private PurchaseRequestDAO requestDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        requestDAO = new PurchaseRequestDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain");
        resp.getWriter().println("GET requests are working - but should use POST");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Verify CSRF token
        String sessionToken = (String) request.getSession().getAttribute("csrfToken");
        String requestToken = request.getParameter("csrfToken");
        
        if (sessionToken == null || !sessionToken.equals(requestToken)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF token");
            return;
        }

        try {
            int requestId = Integer.parseInt(request.getParameter("requestId"));
            String status = request.getParameter("status");
            
            // Validate status
            if (!isValidStatus(status)) {
                request.getSession().setAttribute("errorMessage", "Invalid status value");
                response.sendRedirect(request.getContextPath() + "/purchase-request");
                return;
            }
            
            // Update status
            requestDAO.updateStatus(requestId, status);
            
            // Success - redirect to purchase requests list
            request.getSession().setAttribute("successMessage", "Status updated successfully!");
            response.sendRedirect(request.getContextPath() + "/purchase-request");
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating status", e);
            request.getSession().setAttribute("errorMessage", "Database error: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/purchase-request");
        } catch (NumberFormatException e) {
            logger.log(Level.SEVERE, "Invalid request ID", e);
            request.getSession().setAttribute("errorMessage", "Invalid request ID");
            response.sendRedirect(request.getContextPath() + "/purchase-request");
        }
    }

    private boolean isValidStatus(String status) {
        return status != null && 
               (status.equals("Pending") || 
                status.equals("Approved") || 
                status.equals("Rejected") ||
                status.equals("Completed"));
    }
}