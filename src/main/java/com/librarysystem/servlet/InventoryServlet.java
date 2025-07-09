package com.librarysystem.servlet;

import com.librarysystem.dao.InventoryAdjustmentDAO;
// Assuming BookDAO and CopyDAO exist for fetching details for forms/views
// import com.librarysystem.dao.BookDAO;
// import com.librarysystem.dao.CopyDAO;
import com.librarysystem.model.InventoryAdjustment;
import com.librarysystem.model.User; // Assuming User model for AdjustedBy

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/inventory/*")
public class InventoryServlet extends HttpServlet {
    private InventoryAdjustmentDAO adjustmentDAO;
    // private CopyDAO copyDAO; // For fetching copy details
    // private BookDAO bookDAO; // For fetching book details related to a copy

    @Override
    public void init() throws ServletException {
        adjustmentDAO = new InventoryAdjustmentDAO();
        // copyDAO = new CopyDAO();
        // bookDAO = new BookDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getPathInfo();
        if (action == null) {
            action = "/adjustments/list"; // Default action
        }

        try {
            switch (action) {
                case "/adjustments/list":
                    listAdjustments(req, resp);
                    break;
                case "/adjustments/new":
                    showNewAdjustmentForm(req, resp);
                    break;
                case "/adjustments/view":
                    viewAdjustment(req, resp);
                    break;
                // case "/audits/new": // For cycle counts or physical inventory
                //    showNewAuditForm(req, resp);
                //    break;
                // case "/audits/list":
                //    listAudits(req, resp);
                //    break;
                default:
                    // req.getRequestDispatcher("/WEB-INF/jsp/inventory/inventoryDashboard.jsp").forward(req, resp);
                     resp.sendRedirect(req.getContextPath() + "/inventory/adjustments/list");
                    break;
            }
        } catch (SQLException e) {
            throw new ServletException("Database error in InventoryServlet GET", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getPathInfo();
         if (action == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No action specified.");
            return;
        }

        try {
            switch (action) {
                case "/adjustments/new":
                    createAdjustment(req, resp);
                    break;
                // case "/audits/start":
                //    startNewAudit(req, resp);
                //    break;
                // case "/audits/recordCount":
                //    recordAuditCount(req, resp);
                //    break;
                default:
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Inventory POST action not found.");
                    break;
            }
        } catch (SQLException e) {
            throw new ServletException("Database error in InventoryServlet POST", e);
        }
    }

    // --- Inventory Adjustment Methods ---
    private void listAdjustments(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        List<InventoryAdjustment> adjustments = adjustmentDAO.getAllAdjustments();
        req.setAttribute("adjustments", adjustments);
        // TODO: For display, you might want to fetch Copy and Book details for each adjustment's CopyID
        // This can be done here or optimized in the DAO/Service layer.
        req.getRequestDispatcher("/WEB-INF/jsp/inventory/listAdjustments.jsp").forward(req, resp);
    }

    private void showNewAdjustmentForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, SQLException {
        // For the form, you might need to allow searching for a CopyID or Barcode.
        // If a copyId is passed (e.g. from a copy's detail page), pre-fill it.
        String copyIdParam = req.getParameter("copyId");
        if (copyIdParam != null) {
            // Copy copy = copyDAO.getCopyById(Integer.parseInt(copyIdParam));
            // req.setAttribute("copy", copy); // And potentially book details
            req.setAttribute("selectedCopyId", copyIdParam);
        }
        req.getRequestDispatcher("/WEB-INF/jsp/inventory/newAdjustment.jsp").forward(req, resp);
    }

    private void viewAdjustment(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        int adjId = Integer.parseInt(req.getParameter("id"));
        InventoryAdjustment adj = adjustmentDAO.getAdjustmentById(adjId);
        if (adj != null) {
            req.setAttribute("adjustment", adj);
            // Fetch Copy and Book details for display
            // Copy copy = copyDAO.getCopyById(adj.getCopyID());
            // if(copy != null) {
            //    req.setAttribute("copy", copy);
            //    Book book = bookDAO.getBookById(copy.getBookID());
            //    req.setAttribute("book", book);
            // }
            req.getRequestDispatcher("/WEB-INF/jsp/inventory/viewAdjustment.jsp").forward(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Inventory Adjustment not found.");
        }
    }

    private void createAdjustment(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException, ServletException {
        InventoryAdjustment adj = new InventoryAdjustment();

        String copyIdParam = req.getParameter("copyID");
        String quantityChangeParam = req.getParameter("quantityChange");

        if (copyIdParam == null || copyIdParam.trim().isEmpty() ||
            quantityChangeParam == null || quantityChangeParam.trim().isEmpty()) {
            req.setAttribute("errorMessage", "Copy ID and Quantity Change are required.");
            showNewAdjustmentForm(req, resp);
            return;
        }

        try {
            adj.setCopyID(Integer.parseInt(copyIdParam));
            adj.setQuantityChange(Integer.parseInt(quantityChangeParam));
        } catch (NumberFormatException e) {
            req.setAttribute("errorMessage", "Invalid number for Copy ID or Quantity Change.");
            showNewAdjustmentForm(req, resp);
            return;
        }

        // Validate Copy exists (using CopyDAO - placeholder)
        // Copy copy = copyDAO.getCopyById(adj.getCopyID());
        // if (copy == null) {
        //     req.setAttribute("errorMessage", "Copy with ID " + adj.getCopyID() + " not found.");
        //     showNewAdjustmentForm(req, resp); // Repopulate other fields if possible
        //     return;
        // }


        adj.setAdjustmentDate(Date.valueOf(req.getParameter("adjustmentDate"))); // Consider defaulting to today
        adj.setReason(req.getParameter("reason"));
        adj.setNotes(req.getParameter("notes"));

        HttpSession session = req.getSession();
        User currentUser = (User) session.getAttribute("user"); // Adjust as per your User session attribute
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/user/login?message=loginRequiredForAdjustment");
            return;
        }
        adj.setAdjustedBy(currentUser.getUserID()); // Assuming User model has getUserID()

        // The InventoryAdjustmentDAO's createAdjustment method should handle updating the Copy's status/quantity.
        // That logic is currently a placeholder in the DAO.
        adjustmentDAO.createAdjustment(adj);

        resp.sendRedirect(req.getContextPath() + "/inventory/adjustments/view?id=" + adj.getAdjustmentID());
    }

    // --- Placeholder for Audit/Cycle Count Methods ---
    /*
    private void showNewAuditForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Form to start a new physical inventory or cycle count session
        // Might involve selecting location, categories, etc.
        req.getRequestDispatcher("/WEB-INF/jsp/inventory/newAudit.jsp").forward(req, resp);
    }

    private void listAudits(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        // List past and ongoing audits, their status, variances
        // List<InventoryAudit> audits = auditDAO.getAllAudits();
        // req.setAttribute("audits", audits);
        req.getRequestDispatcher("/WEB-INF/jsp/inventory/listAudits.jsp").forward(req, resp);
    }

    private void startNewAudit(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        // Create an InventoryAudit record
        // Redirect to a page for recording counts for this audit session
    }

    private void recordAuditCount(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        // For a specific audit session, record the counted quantity for a CopyID/Barcode
        // This would update a CycleCountResults table or similar
        // After counting, a process would compare system vs counted and generate adjustments.
    }
    */

}
