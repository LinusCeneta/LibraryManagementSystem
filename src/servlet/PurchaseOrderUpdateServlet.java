package servlet;

import dao.PurchaseOrderDAO;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/po/update")
public class PurchaseOrderUpdateServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(PurchaseOrderUpdateServlet.class.getName());
    private PurchaseOrderDAO poDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        poDAO = new PurchaseOrderDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain");
        resp.getWriter().println("GET requests are not supported - please use POST");
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
            int poId = Integer.parseInt(request.getParameter("poId"));
            String status = request.getParameter("status");
            String action = request.getParameter("action");
            
            if ("delete".equals(action)) {
                // Handle delete action
                poDAO.deletePurchaseOrder(poId);
                request.getSession().setAttribute("successMessage", "Purchase order deleted successfully!");
                response.sendRedirect(request.getContextPath() + "/po/purchaseorder");
                return;
            }
            
            // Validate status for update action
            if (!isValidStatus(status)) {
                request.getSession().setAttribute("errorMessage", "Invalid status value");
                response.sendRedirect(request.getContextPath() + "/po/purchaseorder");
                return;
            }
            
            // Update status
            poDAO.updateStatus(poId, status);
            
            // Success - redirect to purchase orders list
            request.getSession().setAttribute("successMessage", "Status updated successfully!");
            response.sendRedirect(request.getContextPath() + "/po/purchaseorder");
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating purchase order", e);
            request.getSession().setAttribute("errorMessage", "Database error: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/po/purchaseorder");
        } catch (NumberFormatException e) {
            logger.log(Level.SEVERE, "Invalid purchase order ID", e);
            request.getSession().setAttribute("errorMessage", "Invalid purchase order ID");
            response.sendRedirect(request.getContextPath() + "/po/purchaseorder");
        }
    }

    private boolean isValidStatus(String status) {
        return status != null && 
               (status.equals("Created") || 
                status.equals("Submitted") || 
                status.equals("Partially Received") ||
                status.equals("Fully Received") ||
                status.equals("Closed"));
    }
}