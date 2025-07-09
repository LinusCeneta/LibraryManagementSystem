package servlet;

import dao.CopyDAO;
import dao.BookDAO;
import model.Copy;
import model.Book;
import model.Condition;
import model.Status;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/copies/add")
public class CopyAddServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(CopyAddServlet.class.getName());
    private CopyDAO copyDao;
    private BookDAO bookDao;

    @Override
    public void init() throws ServletException {
        super.init();
        copyDao = new CopyDAO();
        bookDao = new BookDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        generateAndSetCSRFToken(request);
        request.getRequestDispatcher("/addCopy.jsp").forward(request, response);
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        CopyFormData formData = extractFormData(request);
        
        try {
            validateFormData(formData);
            Copy copy = createCopyFromFormData(formData);
            
            // Verify book exists
            Optional<Book> book = bookDao.findByIsbn(copy.getIsbn());
            if (!book.isPresent()) {
                String errorMsg = "Cannot add copy - Book with ISBN " + copy.getIsbn() + 
                               " doesn't exist. Please add the book first.";
                request.setAttribute("error", errorMsg);
                prepareFormForRedisplay(request, formData);
                request.getRequestDispatcher("/addCopy.jsp").forward(request, response);
                return;
            }

            // Save the copy
            boolean success = copyDao.save(copy);
            if (!success) {
                throw new ServletException("Failed to save copy to database");
            }

            // Success - redirect to copies list
            request.getSession().setAttribute("successMessage", "Copy added successfully!");
            response.sendRedirect(request.getContextPath() + "/copies");
            
        } catch (ValidationException e) {
            request.setAttribute("error", e.getMessage());
            prepareFormForRedisplay(request, formData);
            request.getRequestDispatcher("/addCopy.jsp").forward(request, response);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in CopyAddServlet", e);
            request.setAttribute("error", "System error: " + e.getMessage());
            prepareFormForRedisplay(request, formData);
            request.getRequestDispatcher("/addCopy.jsp").forward(request, response);
        }
    }
    

    private void prepareFormForRedisplay(HttpServletRequest request, CopyFormData formData) {
        request.setAttribute("copyId", formData.getCopyId());
        request.setAttribute("isbn", formData.getIsbn());
        request.setAttribute("acquisitionDate", formData.getAcquisitionDate());
        request.setAttribute("cost", formData.getCost());
        request.setAttribute("condition", formData.getCondition());
        request.setAttribute("location", formData.getLocation());
        request.setAttribute("status", formData.getStatus());
        
        // Regenerate CSRF token for the form
        generateAndSetCSRFToken(request);
    }

	private void generateAndSetCSRFToken(HttpServletRequest request) {
        String csrfToken = UUID.randomUUID().toString();
        HttpSession session = request.getSession();
        session.setAttribute("csrfToken", csrfToken);
        request.setAttribute("csrfToken", csrfToken);
        
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("Generated CSRF token: " + csrfToken + " for session: " + session.getId());
        }
    }

    private void logRequestParameters(HttpServletRequest request) {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Received parameters:");
            Enumeration<String> params = request.getParameterNames();
            while (params.hasMoreElements()) {
                String paramName = params.nextElement();
                logger.fine(paramName + ": " + request.getParameter(paramName));
            }
        }
    }

    private boolean validateCSRFToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String sessionToken = (session != null) ? (String) session.getAttribute("csrfToken") : null;
        String requestToken = request.getParameter("csrfToken");

        if (sessionToken == null || !sessionToken.equals(requestToken)) {
            logger.warning("CSRF Token validation failed");
            return false;
        }
        return true;
    }

    private CopyFormData extractFormData(HttpServletRequest request) {
        CopyFormData formData = new CopyFormData();
        formData.setCopyId(request.getParameter("copyId"));
        formData.setIsbn(request.getParameter("isbn"));
        formData.setAcquisitionDate(request.getParameter("acquisitionDate"));
        formData.setCost(request.getParameter("cost"));
        formData.setCondition(request.getParameter("condition"));
        formData.setLocation(request.getParameter("location"));
        formData.setStatus(request.getParameter("status"));
        return formData;
    }

    private void validateFormData(CopyFormData formData) throws ValidationException {
        if (formData.getCopyId() == null || formData.getCopyId().trim().isEmpty()) {
            throw new ValidationException("Copy ID is required");
        }
        if (formData.getIsbn() == null || formData.getIsbn().trim().isEmpty()) {
            throw new ValidationException("ISBN is required");
        }
        try {
            Double.parseDouble(formData.getCost());
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid cost value");
        }
    }

    private Copy createCopyFromFormData(CopyFormData formData) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        return new Copy(
            formData.getCopyId(),
            formData.getIsbn(),
            sdf.parse(formData.getAcquisitionDate()),
            Double.parseDouble(formData.getCost()),
            Condition.valueOf(formData.getCondition()),
            formData.getLocation(),
            Status.valueOf(formData.getStatus())
        );
    }

    private void setSuccessMessage(HttpServletRequest request, String message) {
        request.getSession().setAttribute("successMessage", message);
    }

    private void handleValidationError(HttpServletRequest request, HttpServletResponse response,
                                     String errorMessage) throws ServletException, IOException {
        logger.warning("Validation error: " + errorMessage);
        request.setAttribute("error", errorMessage);
        request.getRequestDispatcher("/addCopy.jsp").forward(request, response);
    }

    private void handleParseException(HttpServletRequest request, HttpServletResponse response,
                                    ParseException e) throws ServletException, IOException {
        logger.log(Level.WARNING, "Date parse error", e);
        request.setAttribute("error", "Invalid date format. Please use YYYY-MM-DD format.");
        request.getRequestDispatcher("/addCopy.jsp").forward(request, response);
    }

    private void handleServletException(HttpServletRequest request, HttpServletResponse response,
                                      ServletException e) throws ServletException, IOException {
        logger.log(Level.WARNING, "Servlet error", e);
        request.setAttribute("error", e.getMessage());
        request.getRequestDispatcher("/addCopy.jsp").forward(request, response);
    }

    private void handleGeneralException(HttpServletRequest request, HttpServletResponse response,
                                      Exception e) throws ServletException, IOException {
        logger.log(Level.SEVERE, "Unexpected error occurred", e);
        request.setAttribute("error", "An unexpected error occurred. Please try again later.");
        request.getRequestDispatcher("/error.jsp").forward(request, response);
    }

    private static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }

    private static class CopyFormData {
        private String copyId;
        private String isbn;
        private String acquisitionDate;
        private String cost;
        private String condition;
        private String location;
        private String status;

        // Getters and Setters
        public String getCopyId() { return copyId; }
        public void setCopyId(String copyId) { this.copyId = copyId; }
        public String getIsbn() { return isbn; }
        public void setIsbn(String isbn) { this.isbn = isbn; }
        public String getAcquisitionDate() { return acquisitionDate; }
        public void setAcquisitionDate(String acquisitionDate) { this.acquisitionDate = acquisitionDate; }
        public String getCost() { return cost; }
        public void setCost(String cost) { this.cost = cost; }
        public String getCondition() { return condition; }
        public void setCondition(String condition) { this.condition = condition; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}