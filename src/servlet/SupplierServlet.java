package servlet;

import dao.SupplierDAO;
import model.Supplier;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.SQLException;

@WebServlet("/suppliers")
public class SupplierServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(SupplierServlet.class.getName());
    private final SupplierDAO supplierDAO = new SupplierDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        logger.log(Level.INFO, "Entered doGet() method");
        
        String action = request.getParameter("action");
        logger.log(Level.INFO, "Action parameter: {0}", action);

        try {
            if (action == null) {
                handleListSuppliers(request, response);
            } else if (action.equals("delete")) {
                handleDeleteSupplier(request, response);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in doGet: " + e.getMessage(), e);
            handleError(request, response, "Error processing request: " + e.getMessage());
        }
    }

    private void handleListSuppliers(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        logger.info("Listing all suppliers");
        try {
            List<Supplier> suppliers = supplierDAO.findAll();
            logger.log(Level.INFO, "Retrieved {0} suppliers from database", suppliers.size());
            
            request.setAttribute("suppliers", suppliers);
            generateAndSetCSRFToken(request);
            request.getRequestDispatcher("/supplierList.jsp").forward(request, response);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error while listing suppliers", e);
            handleError(request, response, "Database error occurred while retrieving suppliers");
        }
    }

    private void handleDeleteSupplier(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, ServletException {
        String supplierId = request.getParameter("supplierId");
        logger.log(Level.INFO, "Deleting supplier with ID: {0}", supplierId);
        
        if (!validateCSRFToken(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF Token");
            return;
        }

        try {
            boolean deleted = supplierDAO.delete(Integer.parseInt(supplierId));
            logger.log(Level.INFO, "Delete operation {0}", deleted ? "successful" : "failed");
            
            if (!deleted) {
                handleError(request, response, "Failed to delete supplier with ID: " + supplierId);
                return;
            }
            
            request.getSession().setAttribute("successMessage", "Supplier deleted successfully");
            response.sendRedirect(request.getContextPath() + "/suppliers");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error while deleting supplier", e);
            handleError(request, response, "Database error occurred while deleting supplier");
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid supplier ID format", e);
            handleError(request, response, "Invalid supplier ID format");
        }
    }

    private void handleError(HttpServletRequest request, HttpServletResponse response, String message) 
            throws ServletException, IOException {
        request.setAttribute("error", message);
        request.getRequestDispatcher("/error.jsp").forward(request, response);
    }

    private void generateAndSetCSRFToken(HttpServletRequest request) {
        String csrfToken = UUID.randomUUID().toString();
        request.getSession().setAttribute("csrfToken", csrfToken);
        request.setAttribute("csrfToken", csrfToken);
    }

    private boolean validateCSRFToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String sessionToken = session != null ? (String) session.getAttribute("csrfToken") : null;
        String requestToken = request.getParameter("csrfToken");

        return sessionToken != null && sessionToken.equals(requestToken);
    }
}