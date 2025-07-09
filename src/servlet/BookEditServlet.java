package servlet;

import dao.*;
import model.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class BookEditServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(BookEditServlet.class.getName());
    
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            logger.info("Processing GET request for book edit");
            
            String id = request.getParameter("id");
            String isbn = request.getParameter("isbn");
            Book book = resolveBook(id, isbn);
            
            if (id != null && book == null) {
                throw new ServletException("Book not found with ID: " + id);
            }
            
            prepareRequestAttributes(request, book);
            request.getRequestDispatcher("/bookEdit.jsp").forward(request, response);
            
        } catch (Exception e) {
            logger.severe("Error in GET request: " + e.getMessage());
            request.setAttribute("error", "Error loading book: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            logger.info("Processing POST request for book save");
            
            BookFormData formData = extractFormData(request);
            Book book = createOrUpdateBook(formData);
            
            validateBook(book);
            saveBook(book);
            
            request.getSession().setAttribute("successMessage", 
                "Book " + (formData.getId() == null ? "created" : "updated") + " successfully");
            response.sendRedirect(request.getContextPath() + "/book-list");
            
        } catch (NumberFormatException e) {
            handleValidationError(request, response, "Invalid number format: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            handleValidationError(request, response, "Invalid input: " + e.getMessage(), e);
        } catch (Exception e) {
            handleValidationError(request, response, "Error saving book: " + e.getMessage(), e);
        }
    }

    private Book resolveBook(String id, String isbn) {
        if (id != null && !id.isEmpty()) {
            return bookDAO.findById(Integer.parseInt(id));
        } else if (isbn != null && !isbn.isEmpty()) {
            return bookDAO.findByIsbn(isbn).orElse(new Book());
        }
        return new Book();
    }

    private void prepareRequestAttributes(HttpServletRequest request, Book book) {
        List<Author> authors = authorDAO.getAll();
        List<Category> categories = categoryDAO.getAll();
        List<Publisher> publishers = publisherDAO.getAll();
        
        request.setAttribute("book", book);
        request.setAttribute("authors", authors);
        request.setAttribute("categories", categories);
        request.setAttribute("publishers", publishers);
        
        if (book != null) {
            request.setAttribute("selectedAuthorIds", 
                book.getAuthors().stream().map(Author::getId).collect(Collectors.toList()));
            request.setAttribute("selectedCategoryIds", 
                book.getCategories().stream().map(Category::getId).collect(Collectors.toList()));
        }
    }

    private BookFormData extractFormData(HttpServletRequest request) {
        BookFormData formData = new BookFormData();
        formData.setId(request.getParameter("id"));
        formData.setIsbn(request.getParameter("isbn"));
        formData.setTitle(request.getParameter("title"));
        formData.setSubtitle(request.getParameter("subtitle"));
        formData.setAuthorIds(request.getParameterValues("authorIds"));
        formData.setPublisherId(request.getParameter("publisherId"));
        formData.setCategoryIds(request.getParameterValues("categoryIds"));
        formData.setPublicationYear(request.getParameter("publicationYear"));
        formData.setEdition(request.getParameter("edition"));
        formData.setLanguage(request.getParameter("language"));
        formData.setCallNumber(request.getParameter("callNumber"));
        formData.setNumberOfPages(request.getParameter("numberOfPages"));
        formData.setSummary(request.getParameter("summary"));
        formData.setFormat(request.getParameter("format"));
        return formData;
    }

    private Book createOrUpdateBook(BookFormData formData) {
        Book book = (formData.getId() == null || formData.getId().isEmpty()) 
            ? new Book() 
            : bookDAO.findById(Integer.parseInt(formData.getId()));
        
        book.setIsbn(formData.getIsbn());
        book.setTitle(formData.getTitle());
        book.setSubtitle(formData.getSubtitle());
        book.setPublicationYear(Integer.parseInt(formData.getPublicationYear()));
        book.setEdition(formData.getEdition());
        book.setLanguage(formData.getLanguage());
        book.setCallNumber(formData.getCallNumber());
        book.setNumberOfPages(Integer.parseInt(formData.getNumberOfPages()));
        book.setSummary(formData.getSummary());
        book.setFormat(Book.Format.valueOf(formData.getFormat()));
        
        // Handle relationships
        if (formData.getAuthorIds() != null) {
            book.setAuthors(Arrays.stream(formData.getAuthorIds())
                .map(aId -> authorDAO.findById(Integer.parseInt(aId)))
                .collect(Collectors.toList()));
        }
        
        if (formData.getPublisherId() != null && !formData.getPublisherId().isEmpty()) {
            book.setPublisher(publisherDAO.findById(Integer.parseInt(formData.getPublisherId())));
        }
        
        if (formData.getCategoryIds() != null) {
            book.setCategories(Arrays.stream(formData.getCategoryIds())
                .map(cId -> categoryDAO.findById(Integer.parseInt(cId)))
                .collect(Collectors.toList()));
        }
        
        return book;
    }

    private void validateBook(Book book) {
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }
        if (book.getAuthors() == null || book.getAuthors().isEmpty()) {
            throw new IllegalArgumentException("At least one author is required");
        }
    }

    private void saveBook(Book book) {
        if (book.getId() == null) {
            bookDAO.create(book);
        } else {
            bookDAO.update(book);
        }
    }

    private void handleValidationError(HttpServletRequest request, HttpServletResponse response,
                                     String errorMessage, Exception e) throws ServletException, IOException {
        logger.severe("Validation error: " + errorMessage);
        e.printStackTrace();
        
        request.setAttribute("error", errorMessage);
        prepareRequestAttributes(request, createBookFromRequest(request));
        request.getRequestDispatcher("/bookEdit.jsp").forward(request, response);
    }

    private Book createBookFromRequest(HttpServletRequest request) {
        Book book = new Book();
        book.setIsbn(request.getParameter("isbn"));
        book.setTitle(request.getParameter("title"));
        book.setSubtitle(request.getParameter("subtitle"));
        
        try {
            book.setPublicationYear(Integer.parseInt(request.getParameter("publicationYear")));
            book.setNumberOfPages(Integer.parseInt(request.getParameter("numberOfPages")));
        } catch (NumberFormatException e) {
            // Use default values if parsing fails
            book.setPublicationYear(0);
            book.setNumberOfPages(0);
        }
        
        book.setEdition(request.getParameter("edition"));
        book.setLanguage(request.getParameter("language"));
        book.setCallNumber(request.getParameter("callNumber"));
        book.setSummary(request.getParameter("summary"));
        
        try {
            book.setFormat(Book.Format.valueOf(request.getParameter("format")));
        } catch (IllegalArgumentException e) {
            book.setFormat(Book.Format.PAPERBACK);
        }
        
        return book;
    }

    // Inner class to hold form data
    private static class BookFormData {
        private String id;
        private String isbn;
        private String title;
        private String subtitle;
        private String[] authorIds;
        private String publisherId;
        private String[] categoryIds;
        private String publicationYear;
        private String edition;
        private String language;
        private String callNumber;
        private String numberOfPages;
        private String summary;
        private String format;

        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getIsbn() { return isbn; }
        public void setIsbn(String isbn) { this.isbn = isbn; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getSubtitle() { return subtitle; }
        public void setSubtitle(String subtitle) { this.subtitle = subtitle; }
        public String[] getAuthorIds() { return authorIds; }
        public void setAuthorIds(String[] authorIds) { this.authorIds = authorIds; }
        public String getPublisherId() { return publisherId; }
        public void setPublisherId(String publisherId) { this.publisherId = publisherId; }
        public String[] getCategoryIds() { return categoryIds; }
        public void setCategoryIds(String[] categoryIds) { this.categoryIds = categoryIds; }
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
        public String getFormat() { return format; }
        public void setFormat(String format) { this.format = format; }
    }
}