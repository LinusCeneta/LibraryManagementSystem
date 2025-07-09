package servlet;

import dao.SupplierDAO;
import model.Supplier;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/suppliers/edit")
public class SupplierEditServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(SupplierEditServlet.class.getName());
    private final SupplierDAO supplierDAO = new SupplierDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String supplierId = request.getParameter("supplierId");
        logger.log(Level.INFO, "Editing supplier with ID: {0}", supplierId);
        
        try {
            Optional<Supplier> supplier = supplierDAO.findById(Integer.parseInt(supplierId));
            if (supplier.isPresent()) {
                throw new ServletException("Supplier not found with ID: " + supplierId);
            }
            
            request.setAttribute("supplier", supplier);
            generateAndSetCSRFToken(request);
            request.getRequestDispatcher("/supplierEdit.jsp").forward(request, response);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in SupplierEditServlet", e);
            request.setAttribute("error", "Error editing supplier: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.log(Level.INFO, "SupplierEditServlet - doPost() called");
        
        if (!validateCSRFToken(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF Token");
            return;
        }

        try {
            Supplier supplier = createSupplierFromRequest(request);
            boolean success = supplierDAO.update(supplier);
            
            if (!success) {
                throw new ServletException("Failed to update supplier");
            }
            
            request.getSession().setAttribute("successMessage", "Supplier updated successfully");
            response.sendRedirect(request.getContextPath() + "/suppliers");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating supplier: " + e.getMessage(), e);
            request.setAttribute("supplier", createSupplierFromRequest(request));
            request.setAttribute("error", "Error updating supplier: " + e.getMessage());
            request.getRequestDispatcher("/supplierEdit.jsp").forward(request, response);
        }
    }

    private Supplier createSupplierFromRequest(HttpServletRequest request) {
        Supplier supplier = new Supplier();
        supplier.setSupplierId(Integer.parseInt(request.getParameter("id")));
        supplier.setName(request.getParameter("name"));
        supplier.setContactPerson(request.getParameter("contactPerson"));
        supplier.setAddress(request.getParameter("address"));
        supplier.setPhone(request.getParameter("phone"));
        supplier.setEmail(request.getParameter("email"));
        supplier.setPaymentTerms(request.getParameter("paymentTerms"));
        return supplier;
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