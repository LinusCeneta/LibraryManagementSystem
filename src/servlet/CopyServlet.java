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
import java.net.URLEncoder;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CopyServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(CopyServlet.class.getName());
    private final CopyDAO copyDao = new CopyDAO();
    private final BookDAO bookDao = new BookDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        logger.log(Level.INFO, "Entered doGet() method");
        
        String action = request.getParameter("action");
        logger.log(Level.INFO, "Action parameter: {0}", action);

        try {
            if (action == null) {
                handleListCopies(request, response);
            } else if (action.equals("delete")) {
                handleDeleteCopy(request, response);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in doGet: " + e.getMessage(), e);
            handleError(request, response, "Error processing request: " + e.getMessage());
        }
    }
    private void handleListCopies(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        logger.info("Listing all copies");
        List<Copy> copies = copyDao.getAllCopies();
        logger.log(Level.INFO, "Retrieved {0} copies from database", copies.size());
        
        if (copies.isEmpty()) {
            logger.info("Database is empty - attempting to add test data");
            initializeTestData(request, response);
            copies = copyDao.getAllCopies();
        }
        
        request.setAttribute("copies", copies);
        request.getRequestDispatcher("copies.jsp").forward(request, response);
    }

    private void initializeTestData(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            Optional<Book> existingBook = bookDao.findByIsbn("1234567890");
            if (!existingBook.isPresent()) {
                logger.info("Creating test book");
                Book testBook = createTestBook();
                if (!bookDao.save(testBook)) {
                    throw new ServletException("Failed to create test book");
                }
            }

            Copy testCopy = createTestCopy();
            if (!copyDao.save(testCopy)) {
                throw new ServletException("Failed to create test copy");
            }
            logger.info("Test data initialized successfully");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error creating test data: " + e.getMessage(), e);
            handleError(request, response, "Failed to initialize test data: " + e.getMessage());
        }
    }

    private Book createTestBook() {
        return new Book("1234567890", "Test Book", null, null, null, 
                        2025, "Test Publisher", "Test Author", null, 
                        "English", 100, "Test summary", Book.Format.PAPERBACK);
    }

    private Copy createTestCopy() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return new Copy(
            "TEST-001",
            "1234567890",
            sdf.parse("2025-01-01"),
            29.99,
            Condition.GOOD,
            "Shelf-A",
            Status.AVAILABLE
        );
    }


    private void handleDeleteCopy(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, ServletException {
        String copyId = request.getParameter("copyId");
        logger.log(Level.INFO, "Deleting copy with ID: {0}", copyId);
        
        boolean deleted = copyDao.delete(copyId);
        logger.log(Level.INFO, "Delete operation {0}", deleted ? "successful" : "failed");
        
        if (!deleted) {
            handleError(request, response, "Failed to delete copy with ID: " + copyId);
            return;
        }
        
        response.sendRedirect(request.getContextPath() + "/copies");
    }


    private void handleError(HttpServletRequest request, HttpServletResponse response, String message) 
            throws ServletException, IOException {
        request.setAttribute("error", message);
        request.getRequestDispatcher("/error.jsp").forward(request, response);
    }
}