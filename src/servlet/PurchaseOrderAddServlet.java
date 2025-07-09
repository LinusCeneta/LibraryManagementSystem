package servlet;

import dao.PurchaseOrderDAO;
import model.PurchaseOrder;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/po/add")
public class PurchaseOrderAddServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(PurchaseOrderAddServlet.class.getName());
    private PurchaseOrderDAO poDAO;

    @Override
    public void init() {
        poDAO = new PurchaseOrderDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        generateAndSetCSRFToken(request);
        request.getRequestDispatcher("/poAdd.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.info("Handling POST request for new PO");
        
        // Log all parameters for debugging
        logRequestParameters(request);
        
        PurchaseOrderFormData formData = extractFormData(request);
        
        try {
            validateCSRFToken(request);
            validateFormData(formData);
            PurchaseOrder po = createPurchaseOrderFromFormData(formData);
            
            int poId = poDAO.createPurchaseOrder(po);
            logger.info("Created PO with ID: " + poId);
            
            // Use a safer redirect - either to list or view the created PO
            String redirectPath = determineRedirectPath(request);
            request.getSession().setAttribute("successMessage", "Purchase order created successfully!");
            response.sendRedirect(redirectPath);
            
        } catch (ValidationException e) {
            logger.warning("Validation error: " + e.getMessage());
            handleError(request, response, formData, e.getMessage());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error creating PO", e);
            handleError(request, response, formData, "Database error: " + e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error", e);
            handleError(request, response, formData, "System error: " + e.getMessage());
        }
    }

    private String determineRedirectPath(HttpServletRequest request) {
        // First try the list view
        String listPath = request.getContextPath() + "/po/purchaseorder";
        // Fallback to application root if needed
        return listPath;
    }

    private void handleError(HttpServletRequest request, HttpServletResponse response, 
            PurchaseOrderFormData formData, String errorMessage)
             throws ServletException, IOException {
              logger.severe("Error occurred: " + errorMessage);
              logger.severe("Form data: " + formData.toString());
             request.setAttribute("error", errorMessage);
             prepareFormForRedisplay(request, formData);
             request.getRequestDispatcher("/poAdd.jsp").forward(request, response);
            }

    private void logRequestParameters(HttpServletRequest request) {
        logger.info("Request parameters:");
        request.getParameterMap().forEach((key, values) -> {
            logger.info(key + "=" + String.join(", ", values));
        });
    }

    private void prepareFormForRedisplay(HttpServletRequest request, PurchaseOrderFormData formData) {
        request.setAttribute("formData", formData);
        generateAndSetCSRFToken(request);
    }

    private void generateAndSetCSRFToken(HttpServletRequest request) {
        String csrfToken = UUID.randomUUID().toString();
        request.getSession().setAttribute("csrfToken", csrfToken);
        request.setAttribute("csrfToken", csrfToken);
        logger.fine("Generated CSRF token: " + csrfToken);
    }

    private void validateCSRFToken(HttpServletRequest request) throws ValidationException {
        String sessionToken = (String) request.getSession().getAttribute("csrfToken");
        String requestToken = request.getParameter("csrfToken");
        
        logger.fine("Validating CSRF token. Session: " + sessionToken + ", Request: " + requestToken);
        
        if (sessionToken == null || requestToken == null || !sessionToken.equals(requestToken)) {
            throw new ValidationException("Invalid CSRF token. Please refresh the form and try again.");
        }
    }

    private PurchaseOrderFormData extractFormData(HttpServletRequest request) {
        PurchaseOrderFormData formData = new PurchaseOrderFormData();
        formData.setPoNumber(request.getParameter("poNumber"));
        formData.setSupplierId(request.getParameter("supplierId"));
        formData.setCreatedDate(request.getParameter("createdDate"));
        formData.setExpectedDeliveryDate(request.getParameter("expectedDeliveryDate"));
        return formData;
    }

    private void validateFormData(PurchaseOrderFormData formData) throws ValidationException {
        if (formData.getPoNumber() == null || formData.getPoNumber().trim().isEmpty()) {
            throw new ValidationException("PO Number is required");
        }
        if (formData.getSupplierId() == null || formData.getSupplierId().trim().isEmpty()) {
            throw new ValidationException("Supplier ID is required");
        }
        if (formData.getCreatedDate() == null || formData.getCreatedDate().trim().isEmpty()) {
            throw new ValidationException("Created Date is required");
        }
        if (formData.getExpectedDeliveryDate() == null || formData.getExpectedDeliveryDate().trim().isEmpty()) {
            throw new ValidationException("Expected Delivery Date is required");
        }
        
        try {
            Integer.parseInt(formData.getSupplierId());
        } catch (NumberFormatException e) {
            throw new ValidationException("Supplier ID must be a valid number");
        }
    }

    private PurchaseOrder createPurchaseOrderFromFormData(PurchaseOrderFormData formData) 
            throws ValidationException {
        PurchaseOrder po = new PurchaseOrder();
        
        try {
            po.setPoNumber(formData.getPoNumber());
            po.setSupplierId(Integer.parseInt(formData.getSupplierId()));
            po.setCreatedDate(Date.valueOf(formData.getCreatedDate()));
            po.setExpectedDeliveryDate(Date.valueOf(formData.getExpectedDeliveryDate()));
            po.setStatus("Created");
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid date format. Please use yyyy-MM-dd");
        }
        
        return po;
    }

    private static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }

    private static class PurchaseOrderFormData {
        private String poNumber;
        private String supplierId;
        private String createdDate;
        private String expectedDeliveryDate;

        // No-arg constructor
        public PurchaseOrderFormData() {}

        // All-args constructor (optional)
        public PurchaseOrderFormData(String poNumber, String supplierId, 
                                   String createdDate, String expectedDeliveryDate) {
            this.poNumber = poNumber;
            this.supplierId = supplierId;
            this.createdDate = createdDate;
            this.expectedDeliveryDate = expectedDeliveryDate;
        }

        // Getters and Setters
        public String getPoNumber() { 
            return poNumber; 
        }
        
        public void setPoNumber(String poNumber) { 
            this.poNumber = poNumber != null ? poNumber.trim() : null; 
        }
        
        public String getSupplierId() { 
            return supplierId; 
        }
        
        public void setSupplierId(String supplierId) { 
            this.supplierId = supplierId != null ? supplierId.trim() : null; 
        }
        
        public String getCreatedDate() { 
            return createdDate; 
        }
        
        public void setCreatedDate(String createdDate) { 
            this.createdDate = createdDate != null ? createdDate.trim() : null; 
        }
        
        public String getExpectedDeliveryDate() { 
            return expectedDeliveryDate; 
        }
        
        public void setExpectedDeliveryDate(String expectedDeliveryDate) { 
            this.expectedDeliveryDate = expectedDeliveryDate != null ? expectedDeliveryDate.trim() : null;
        }
        
        @Override
        public String toString() {
            return new StringBuilder("PurchaseOrderFormData{")
                .append("poNumber='").append(poNumber).append('\'')
                .append(", supplierId='").append(supplierId).append('\'')
                .append(", createdDate='").append(createdDate).append('\'')
                .append(", expectedDeliveryDate='").append(expectedDeliveryDate).append('\'')
                .append('}')
                .toString();
        }

        // Additional useful methods
        public boolean isValid() {
            return poNumber != null && !poNumber.isEmpty()
                && supplierId != null && !supplierId.isEmpty()
                && createdDate != null && !createdDate.isEmpty()
                && expectedDeliveryDate != null && !expectedDeliveryDate.isEmpty();
        }

        public Map<String, String> toMap() {
            Map<String, String> map = new LinkedHashMap<>();
            map.put("poNumber", poNumber);
            map.put("supplierId", supplierId);
            map.put("createdDate", createdDate);
            map.put("expectedDeliveryDate", expectedDeliveryDate);
            return map;
        }
    }
}