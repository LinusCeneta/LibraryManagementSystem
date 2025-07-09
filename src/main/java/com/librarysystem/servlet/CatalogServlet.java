package com.librarysystem.servlet;

import com.librarysystem.dao.BookDAO;
import com.librarysystem.dao.BookCollectionDAO;
import com.librarysystem.dao.CopyDAO;
import com.librarysystem.dao.LocationDAO;
import com.librarysystem.dao.AuthorDAO;
import com.librarysystem.dao.CategoryDAO;
import com.librarysystem.dao.PublisherDAO;
import com.librarysystem.model.Book;
import com.librarysystem.model.BookCollection;
import com.librarysystem.model.Copy;
import com.librarysystem.model.Location;
import com.librarysystem.model.User; // For checking member status for "Place Hold"

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@WebServlet("/catalog/*")
public class CatalogServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private BookDAO bookDAO;
    private CopyDAO copyDAO;
    private LocationDAO locationDAO;
    private BookCollectionDAO bookCollectionDAO;
    // DAOs for facet data
    private AuthorDAO authorDAO;
    private CategoryDAO categoryDAO;
    private PublisherDAO publisherDAO;


    @Override
    public void init() throws ServletException {
        // Initialize DAOs - consider dependency injection for a larger application
        this.bookDAO = new BookDAO();
        this.copyDAO = new CopyDAO();
        this.locationDAO = new LocationDAO();
        this.bookCollectionDAO = new BookCollectionDAO();
        this.authorDAO = new AuthorDAO();
        this.categoryDAO = new CategoryDAO();
        this.publisherDAO = new PublisherDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getPathInfo();
        if (action == null) {
            action = "/home"; // Default to catalog home
        }

        try {
            switch (action) {
                case "/search":
                    handleSearch(req, resp);
                    break;
                case "/book":
                    viewBookDetail(req, resp);
                    break;
                case "/collection":
                    viewCollection(req, resp);
                    break;
                case "/home":
                default:
                    viewCatalogHome(req, resp);
                    break;
            }
        } catch (SQLException e) {
            // Log error properly in a real app
            e.printStackTrace();
            req.setAttribute("errorMessage", "Database error: " + e.getMessage());
            // Forward to a generic error page or catalog home with error
            req.getRequestDispatcher("/WEB-INF/jsp/error/500.jsp").forward(req, resp);
        }
    }

    private void viewCatalogHome(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        // Fetch featured collections or new arrivals, etc.
        List<BookCollection> featuredCollections = bookCollectionDAO.getAllBookCollections(true); // Get public collections
        // For simplicity, just pass collections for now. Could also fetch some "new" or "popular" books.
        req.setAttribute("featuredCollections", featuredCollections);
        req.getRequestDispatcher("/WEB-INF/jsp/catalog/home.jsp").forward(req, resp);
    }

    private void handleSearch(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        String searchText = req.getParameter("q");
        String pageParam = req.getParameter("page");
        int currentPage = (pageParam != null && !pageParam.isEmpty()) ? Integer.parseInt(pageParam) : 1;
        int pageSize = 10; // Configurable

        // Extract filters from request parameters
        // Example: ?q=java&category=Programming&author=Bloch&year=2018&format=Paperback
        Map<String, List<String>> filters = new HashMap<>();
        req.getParameterMap().forEach((key, values) -> {
            if (!key.equals("q") && !key.equals("page") && !key.equals("sortBy") && values.length > 0 && !values[0].isEmpty()) {
                filters.put(key, Arrays.asList(values));
            }
        });

        String sortBy = req.getParameter("sortBy"); // e.g., "Title ASC", "PublicationYear DESC"

        List<Book> searchResults = bookDAO.searchBooks(searchText, filters, sortBy, currentPage, pageSize);
        int totalResults = bookDAO.countSearchBooks(searchText, filters); // TODO: Implement countSearchBooks in BookDAO
        int totalPages = (int) Math.ceil((double) totalResults / pageSize);
        if (totalResults == 0 && searchResults.size() > 0) { // Temp fix if countSearchBooks is placeholder
            totalPages = 1; // Assume one page if results found but count is 0
        }


        req.setAttribute("searchText", searchText);
        req.setAttribute("searchResults", searchResults);
        req.setAttribute("currentPage", currentPage);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("totalResults", totalResults);
        req.setAttribute("appliedFilters", filters); // Pass applied filters back to JSP for display

        // Load data for facets
        loadFacetData(req);

        req.getRequestDispatcher("/WEB-INF/jsp/catalog/search.jsp").forward(req, resp);
    }

    private void loadFacetData(HttpServletRequest req) throws SQLException {
        req.setAttribute("allAuthors", authorDAO.getAllAuthors());
        req.setAttribute("allCategories", categoryDAO.getAllCategories()); // Could be hierarchical
        req.setAttribute("allPublishers", publisherDAO.getAllPublishers());
        req.setAttribute("allLanguages", bookDAO.getAllLanguages());
        req.setAttribute("allFormats", bookDAO.getAllFormats());
        req.setAttribute("allPublicationYears", bookDAO.getDistinctPublicationYears());
        req.setAttribute("allLocations", locationDAO.getAllLocations());
    }


    private void viewBookDetail(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        String bookIdParam = req.getParameter("id");
        if (bookIdParam == null || bookIdParam.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Book ID is required.");
            return;
        }
        try {
            int bookId = Integer.parseInt(bookIdParam);
            Optional<Book> bookOpt = bookDAO.getBookById(bookId); // This should fetch authors, categories

            if (bookOpt.isPresent()) {
                Book book = bookOpt.get();
                req.setAttribute("book", book);

                // Fetch copy availability per location
                Map<Integer, Integer> availabilityPerLocation = copyDAO.getAvailableCopiesCountPerLocation(bookId);
                Map<Location, Integer> locationAvailability = new HashMap<>();
                List<Copy> allCopiesOfBook = copyDAO.getCopiesByBookId(bookId); // Get all copies for detailed status

                for (Map.Entry<Integer, Integer> entry : availabilityPerLocation.entrySet()) {
                    locationDAO.getLocationById(entry.getKey()).ifPresent(loc ->
                        locationAvailability.put(loc, entry.getValue())
                    );
                }
                req.setAttribute("locationAvailability", locationAvailability);
                req.setAttribute("allCopiesOfBook", allCopiesOfBook); // For showing detailed status of each copy

                // Check if user can place a hold (logged in, item is loanable, etc.)
                HttpSession session = req.getSession(false);
                User currentUser = (session != null) ? (User) session.getAttribute("user") : null;
                boolean canPlaceHold = false;
                if (currentUser != null) { // And other conditions like item type is loanable
                    // Simple check: if no copies are available at any location
                    // More complex: check member's existing holds, loan limits etc.
                    if (locationAvailability.values().stream().mapToInt(Integer::intValue).sum() == 0 && allCopiesOfBook.stream().allMatch(c -> "Checked Out".equals(c.getStatus()))) {
                         canPlaceHold = true; // Simplified logic
                    }
                     // Also consider if the book format itself is holdable
                }
                req.setAttribute("canPlaceHold", canPlaceHold);


                req.getRequestDispatcher("/WEB-INF/jsp/catalog/bookDetail.jsp").forward(req, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Book not found.");
            }
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Book ID format.");
        }
    }

    private void viewCollection(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        String collectionIdParam = req.getParameter("id");
        if (collectionIdParam == null || collectionIdParam.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Collection ID is required.");
            return;
        }
        try {
            int collectionId = Integer.parseInt(collectionIdParam);
            Optional<BookCollection> collectionOpt = bookCollectionDAO.getBookCollectionById(collectionId); // This should fetch books

            if (collectionOpt.isPresent()) {
                BookCollection collection = collectionOpt.get();
                if (!collection.isPublic()) {
                    // TODO: Add role check if user is staff/admin to view non-public collections
                    HttpSession session = req.getSession(false);
                    User currentUser = (session != null) ? (User) session.getAttribute("user") : null;
                    if (currentUser == null ||
                        !(currentUser.getRole().getRoleName().equals("ROLE_STAFF") || currentUser.getRole().getRoleName().equals("ROLE_ADMIN"))) {
                         resp.sendError(HttpServletResponse.SC_FORBIDDEN, "This collection is not public.");
                         return;
                    }
                }
                req.setAttribute("collection", collection);
                req.getRequestDispatcher("/WEB-INF/jsp/catalog/viewCollection.jsp").forward(req, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Collection not found.");
            }
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Collection ID format.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Currently, catalog is mostly GET. POST might be used for advanced search forms
        // or actions like "add to cart/list" if implemented.
        // For now, delegate to doGet or send error.
        // doGet(req, resp);
         resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "POST method not supported for this catalog action.");
    }
}
