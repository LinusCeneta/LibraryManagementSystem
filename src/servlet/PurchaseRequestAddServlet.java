package servlet;

import dao.PurchaseRequestDAO;
import model.PurchaseRequest;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/purchase-request-add")
public class PurchaseRequestAddServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(PurchaseRequestAddServlet.class.getName());
    private PurchaseRequestDAO requestDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        requestDAO = new PurchaseRequestDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        generateAndSetCSRFToken(request);
        request.getRequestDispatcher("/prAdd.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        PurchaseRequestFormData formData = extractFormData(request);
        
        try {
            validateFormData(formData);
            PurchaseRequest pr = createPurchaseRequestFromFormData(formData);
            
            // Save the purchase request
            requestDAO.addPurchaseRequest(pr);

            // Success - redirect to purchase requests list
            request.getSession().setAttribute("successMessage", "Purchase request added successfully!");
            response.sendRedirect(request.getContextPath() + "/purchase-request");
            
        } catch (ValidationException e) {
            request.setAttribute("error", e.getMessage());
            prepareFormForRedisplay(request, formData);
            request.getRequestDispatcher("/prAdd.jsp").forward(request, response);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error adding purchase request", e);
            request.setAttribute("error", "Database error: " + e.getMessage());
            prepareFormForRedisplay(request, formData);
            request.getRequestDispatcher("/prAdd.jsp").forward(request, response);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error", e);
            request.setAttribute("error", "System error: " + e.getMessage());
            prepareFormForRedisplay(request, formData);
            request.getRequestDispatcher("/prAdd.jsp").forward(request, response);
        }
    }

    private void prepareFormForRedisplay(HttpServletRequest request, PurchaseRequestFormData formData) {
        request.setAttribute("title", formData.getTitle());
        request.setAttribute("requestedBy", formData.getRequestedBy());
        request.setAttribute("requestDate", formData.getRequestDate());
        
        // Regenerate CSRF token for the form
        generateAndSetCSRFToken(request);
    }

    private void generateAndSetCSRFToken(HttpServletRequest request) {
        String csrfToken = UUID.randomUUID().toString();
        request.getSession().setAttribute("csrfToken", csrfToken);
        request.setAttribute("csrfToken", csrfToken);
    }

    private PurchaseRequestFormData extractFormData(HttpServletRequest request) {
        PurchaseRequestFormData formData = new PurchaseRequestFormData();
        formData.setTitle(request.getParameter("title"));
        formData.setRequestedBy(request.getParameter("requestedBy"));
        formData.setRequestDate(request.getParameter("requestDate"));
        return formData;
    }

    private void validateFormData(PurchaseRequestFormData formData) throws ValidationException {
        if (formData.getTitle() == null || formData.getTitle().trim().isEmpty()) {
            throw new ValidationException("Title is required");
        }
        if (formData.getRequestedBy() == null || formData.getRequestedBy().trim().isEmpty()) {
            throw new ValidationException("Requested By is required");
        }
        if (formData.getRequestDate() == null || formData.getRequestDate().trim().isEmpty()) {
            throw new ValidationException("Request Date is required");
        }
    }

    private PurchaseRequest createPurchaseRequestFromFormData(PurchaseRequestFormData formData) 
            throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date utilDate = sdf.parse(formData.getRequestDate());
        
        PurchaseRequest pr = new PurchaseRequest();
        pr.setTitle(formData.getTitle());
        pr.setRequestedBy(formData.getRequestedBy());
        pr.setRequestDate(new Date(utilDate.getTime()));
        pr.setStatus("Pending"); // Default status
        
        return pr;
    }

    private static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }

    private static class PurchaseRequestFormData {
        private String title;
        private String requestedBy;
        private String requestDate;

        // Getters and Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getRequestedBy() { return requestedBy; }
        public void setRequestedBy(String requestedBy) { this.requestedBy = requestedBy; }
        public String getRequestDate() { return requestDate; }
        public void setRequestDate(String requestDate) { this.requestDate = requestDate; }
    }
}