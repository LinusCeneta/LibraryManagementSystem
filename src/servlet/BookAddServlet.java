package servlet;

import dao.*;
import model.*;
import model.Book.Format;
import utils.DBConnection;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/book-add")
public class BookAddServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(BookAddServlet.class.getName());
    private BookDAO bookDAO;
    private AuthorDAO authorDAO;
    private CategoryDAO categoryDAO;
    private PublisherDAO publisherDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        bookDAO = new BookDAO();
        authorDAO = new AuthorDAO();
        categoryDAO = new CategoryDAO();
        publisherDAO = new PublisherDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
    	logRequestParameters(request);

    	if (!validateCSRFToken(request)) {
    	    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF Token");
    	    return;
    	}

    	Connection conn = null;  // Declare connection here
    	BookFormData formData = extractFormData(request);

    	try {
    	    validateFormData(formData);
    	    
    	    // Initialize connection and start transaction
    	    conn = DBConnection.getConnection();
    	    conn.setAutoCommit(false);
    	    
    	    // Get or create publisher (passing the connection)
    	    Publisher publisher = publisherDAO.findOrCreateByName(formData.getPublisherName(), conn);
    	    Book book = buildBookFromFormData(formData, publisher);

    	    saveBookWithAssociations(book, formData, conn);  // Modified to accept connection
    	    
    	    conn.commit();  // Commit transaction if everything succeeds
    	    
    	    setSuccessMessage(request, "Book successfully " + (book.getId() == null ? "created" : "updated"));
    	    redirectToBookList(response, request);

    	} catch (ValidationException e) {
    	    handleValidationError(request, response, e.getMessage(), formData);
    	} catch (NumberFormatException e) {
    	    handleNumberFormatException(request, response, e, formData);
    	} catch (IllegalArgumentException e) {
    	    handleIllegalArgumentException(request, response, e, formData);
    	} catch (SQLException e) {
    	    if (conn != null) {
    	        try {
    	            conn.rollback();
    	        } catch (SQLException ex) {
    	            logger.log(Level.SEVERE, "Failed to rollback transaction", ex);
    	        }
    	    }
    	    handleGeneralException(request, response, e);
    	} catch (Exception e) {
    	    if (conn != null) {
    	        try {
    	            conn.rollback();
    	        } catch (SQLException ex) {
    	            logger.log(Level.SEVERE, "Failed to rollback transaction", ex);
    	        }
    	    }
    	    handleGeneralException(request, response, e);
    	} finally {
    	    if (conn != null) {
    	        try {
    	            conn.setAutoCommit(true);  // Reset auto-commit before closing
    	            conn.close();
    	        } catch (SQLException e) {
    	            logger.log(Level.WARNING, "Failed to close connection", e);
    	        }
    	    }
    	}
    	}

    private void handleGeneralException(HttpServletRequest request, HttpServletResponse response, Exception e) 
            throws ServletException, IOException {
        logger.log(Level.SEVERE, "Unexpected error occurred", e);
        request.setAttribute("error", "An unexpected error occurred. Please try again later.");
        request.getRequestDispatcher("/error.jsp").forward(request, response);
    }

    private void handleIllegalArgumentException(HttpServletRequest request, HttpServletResponse response,
            IllegalArgumentException e, BookFormData formData) throws ServletException, IOException {
        logger.log(Level.WARNING, "Invalid argument", e);
        request.setAttribute("error", e.getMessage());
        prepareRequestForRedisplay(request, formData);
        repopulateDropdowns(request);
        request.getRequestDispatcher("/bookForm.jsp").forward(request, response);
    }

    private void handleNumberFormatException(HttpServletRequest request, HttpServletResponse response,
            NumberFormatException e, BookFormData formData) throws ServletException, IOException {
        logger.log(Level.WARNING, "Invalid number format", e);
        request.setAttribute("error", "Invalid number format. Please enter valid numeric values.");
        prepareRequestForRedisplay(request, formData);
        repopulateDropdowns(request);
        request.getRequestDispatcher("/bookForm.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        generateAndSetCSRFToken(request);
        repopulateDropdowns(request);
        request.getRequestDispatcher("/bookForm.jsp").forward(request, response);
    }

    private void generateAndSetCSRFToken(HttpServletRequest request) {
        // Generate a secure random CSRF token
        String csrfToken = UUID.randomUUID().toString();
        
        // Store the token in the session
        HttpSession session = request.getSession();
        session.setAttribute("csrfToken", csrfToken);
        
        // Also make the token available to the view (JSP)
        request.setAttribute("csrfToken", csrfToken);
        
        // Log the token generation (debug level only)
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

    private BookFormData extractFormData(HttpServletRequest request) {
        BookFormData formData = new BookFormData();
        formData.setIsbn(request.getParameter("isbn"));
        formData.setTitle(request.getParameter("title"));
        formData.setSubtitle(request.getParameter("subtitle"));
        formData.setPublisherName(request.getParameter("publisherName"));
        formData.setPublicationYear(request.getParameter("publicationYear"));
        formData.setEdition(request.getParameter("edition"));
        formData.setLanguage(request.getParameter("language"));
        formData.setCallNumber(request.getParameter("callNumber"));
        formData.setNumberOfPages(request.getParameter("numberOfPages"));
        formData.setSummary(request.getParameter("summary"));
        formData.setFormatStr(request.getParameter("format"));
        formData.setAuthorNames(request.getParameter("authorNames"));
        formData.setCategoryNames(request.getParameter("categoryNames"));
        return formData;
    }

    private void validateFormData(BookFormData formData) throws ValidationException {
        if (formData.getTitle() == null || formData.getTitle().trim().isEmpty()) {
            throw new ValidationException("Title is required");
        }
        if (formData.getAuthorNames() == null || formData.getAuthorNames().trim().isEmpty()) {
            throw new ValidationException("At least one author is required");
        }
        try {
            Integer.parseInt(formData.getPublicationYear());
            Integer.parseInt(formData.getNumberOfPages());
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid numeric values");
        }
    }

    private Book buildBookFromFormData(BookFormData formData, Publisher publisher) {
        Book book = new Book();
        book.setIsbn(formData.getIsbn());
        book.setTitle(formData.getTitle());
        book.setSubtitle(formData.getSubtitle());
        book.setPublisher(publisher);
        book.setPublicationYear(Integer.parseInt(formData.getPublicationYear()));
        book.setEdition(formData.getEdition());
        book.setLanguage(formData.getLanguage());
        book.setCallNumber(formData.getCallNumber());
        book.setNumberOfPages(Integer.parseInt(formData.getNumberOfPages()));
        book.setSummary(formData.getSummary());
        
        if (formData.getFormatStr() != null && !formData.getFormatStr().isEmpty()) {
            book.setFormat(Book.Format.valueOf(formData.getFormatStr().toUpperCase()));
        }
        
        return book;
    }

    private void saveBookWithAssociations(Book book, BookFormData formData, Connection conn) throws Exception {
        try {
            // Check if book exists first
            boolean bookExists = bookDAO.findByIsbn(book.getIsbn()).isPresent();
            
            // Save book first
            if (!bookExists) {
                bookDAO.create(book, conn);
            } else {
                bookDAO.update(book, conn);
            }

            // Clear existing associations
            bookDAO.clearAssociations(conn, book.getIsbn());
            
            // Then save new associations
            List<Author> authors = authorDAO.findOrCreateList(formData.getAuthorNames(), conn);
            bookDAO.linkAuthors(book.getIsbn(), authors, conn);

            List<Category> categories = categoryDAO.findOrCreateList(formData.getCategoryNames(), conn);
            bookDAO.linkCategories(book.getIsbn(), categories, conn); 

            conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        }
    }

    private void setSuccessMessage(HttpServletRequest request, String message) {
        request.getSession().setAttribute("successMessage", message);
    }

    private void redirectToBookList(HttpServletResponse response, HttpServletRequest request) throws IOException {
        response.sendRedirect(request.getContextPath() + "/book-list");
    }

    private void handleValidationError(HttpServletRequest request, HttpServletResponse response,
                                     String errorMessage, BookFormData formData) 
            throws ServletException, IOException {
        logger.warning("Validation error: " + errorMessage);
        request.setAttribute("error", errorMessage);
        prepareRequestForRedisplay(request, formData);
        repopulateDropdowns(request);
        request.getRequestDispatcher("/bookForm.jsp").forward(request, response);
    }

    private void prepareRequestForRedisplay(HttpServletRequest request, BookFormData formData) {
        Book book = new Book();
        book.setIsbn(formData.getIsbn());
        book.setTitle(formData.getTitle());
        book.setSubtitle(formData.getSubtitle());
        book.setPublicationYear(parseIntSafe(formData.getPublicationYear(), 0));
        book.setEdition(formData.getEdition());
        book.setLanguage(formData.getLanguage());
        book.setCallNumber(formData.getCallNumber());
        book.setNumberOfPages(parseIntSafe(formData.getNumberOfPages(), 0));
        book.setSummary(formData.getSummary());
        
        if (formData.getFormatStr() != null && !formData.getFormatStr().isEmpty()) {
            try {
                book.setFormat(Book.Format.valueOf(formData.getFormatStr().toUpperCase()));
            } catch (IllegalArgumentException e) {
                book.setFormat(Book.Format.PAPERBACK);
            }
        }
        
        request.setAttribute("book", book);
        request.setAttribute("publisherName", formData.getPublisherName());
        request.setAttribute("authorNames", formData.getAuthorNames());
        request.setAttribute("categoryNames", formData.getCategoryNames());
    }

    private int parseIntSafe(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private void repopulateDropdowns(HttpServletRequest request) {
        request.setAttribute("categories", categoryDAO.getAll());
        request.setAttribute("authors", authorDAO.getAll());
        request.setAttribute("publishers", publisherDAO.getAll());
    }

    private static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }

    private static class BookFormData {
        private String isbn;
        private String title;
        private String subtitle;
        private String publisherName;
        private String publicationYear;
        private String edition;
        private String language;
        private String callNumber;
        private String numberOfPages;
        private String summary;
        private String formatStr;
        private String authorNames;
        private String categoryNames;

        // Getters and Setters
        public String getIsbn() { return isbn; }
        public void setIsbn(String isbn) { this.isbn = isbn; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getSubtitle() { return subtitle; }
        public void setSubtitle(String subtitle) { this.subtitle = subtitle; }
        public String getPublisherName() { return publisherName; }
        public void setPublisherName(String publisherName) { this.publisherName = publisherName; }
        public String getPublicationYear() { return publicationYear; }
        public void setPublicationYear(String publicationYear) { this.publicationYear = publicationYear; }
        public String getEdition() { return edition; }
        public void setEdition(String edition) { this.edition = edition; }
        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }
        public String getCallNumber() { return callNumber; }
        public void setCallNumber(String callNumber) { this.callNumber = callNumber; }
        public String getNumberOfPages() { return numberOfPages; }
        public void setNumberOfPages(String numberOfPages) { this.numberOfPages = numberOfPages; }
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
        public String getFormatStr() { return formatStr; }
        public void setFormatStr(String formatStr) { this.formatStr = formatStr; }
        public String getAuthorNames() { return authorNames; }
        public void setAuthorNames(String authorNames) { this.authorNames = authorNames; }
        public String getCategoryNames() { return categoryNames; }
        public void setCategoryNames(String categoryNames) { this.categoryNames = categoryNames; }
    }
}