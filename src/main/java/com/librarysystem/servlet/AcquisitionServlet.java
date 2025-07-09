package com.librarysystem.servlet;

import com.librarysystem.dao.AcquisitionRequestDAO;
import com.librarysystem.dao.PurchaseOrderDAO;
import com.librarysystem.dao.SupplierDAO;
import com.librarysystem.dao.GoodsReceiptNoteDAO;
import com.librarysystem.model.AcquisitionRequest;
import com.librarysystem.model.PurchaseOrder;
import com.librarysystem.model.PurchaseOrderLine;
import com.librarysystem.model.Supplier;
import com.librarysystem.model.User; // Assuming User model for CreatedBy/RequestedBy
import com.librarysystem.model.GoodsReceiptNote;
import com.librarysystem.model.GoodsReceiptNoteItem;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@WebServlet("/acquisition/*")
public class AcquisitionServlet extends HttpServlet {
    private SupplierDAO supplierDAO;
    private AcquisitionRequestDAO acquisitionRequestDAO;
    private PurchaseOrderDAO purchaseOrderDAO;
    private GoodsReceiptNoteDAO goodsReceiptNoteDAO;


    @Override
    public void init() throws ServletException {
        supplierDAO = new SupplierDAO();
        acquisitionRequestDAO = new AcquisitionRequestDAO();
        purchaseOrderDAO = new PurchaseOrderDAO();
        goodsReceiptNoteDAO = new GoodsReceiptNoteDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getPathInfo();
        if (action == null) {
            action = "/dashboard"; // Default action
        }

        try {
            switch (action) {
                // Supplier actions
                case "/suppliers/list":
                    listSuppliers(req, resp);
                    break;
                case "/suppliers/add":
                    showAddSupplierForm(req, resp);
                    break;
                case "/suppliers/edit":
                    showEditSupplierForm(req, resp);
                    break;
                // Acquisition Request actions
                case "/requests/list":
                    listAcquisitionRequests(req, resp);
                    break;
                case "/requests/new":
                    showNewAcquisitionRequestForm(req, resp);
                    break;
                case "/requests/view":
                     viewAcquisitionRequest(req, resp);
                    break;
                // Purchase Order actions
                case "/po/list":
                    listPurchaseOrders(req, resp);
                    break;
                case "/po/new":
                    showNewPurchaseOrderForm(req, resp);
                    break;
                case "/po/view":
                    viewPurchaseOrder(req, resp);
                    break;
                case "/po/receive":
                    showReceivePurchaseOrderForm(req, resp);
                    break;
                // Goods Receipt Note actions
                case "/grn/list":
                    listGoodsReceiptNotes(req, resp);
                    break;
                case "/grn/view":
                    viewGoodsReceiptNote(req, resp);
                    break;
                default:
                    // req.getRequestDispatcher("/WEB-INF/jsp/acquisition/acquisitionDashboard.jsp").forward(req, resp);
                    resp.sendRedirect(req.getContextPath() + "/acquisition/requests/list"); // Default to requests list
                    break;
            }
        } catch (SQLException e) {
            throw new ServletException("Database error in AcquisitionServlet GET", e);
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
                // Supplier actions
                case "/suppliers/add":
                    addSupplier(req, resp);
                    break;
                case "/suppliers/edit":
                    updateSupplier(req, resp);
                    break;
                case "/suppliers/delete":
                    deleteSupplier(req, resp);
                    break;
                // Acquisition Request actions
                case "/requests/new":
                    createAcquisitionRequest(req, resp);
                    break;
                case "/requests/updateStatus": // e.g. approve/reject
                    updateAcquisitionRequestStatus(req, resp);
                    break;
                 case "/requests/delete":
                    deleteAcquisitionRequest(req, resp);
                    break;
                // Purchase Order actions
                case "/po/new":
                    createPurchaseOrder(req, resp);
                    break;
                case "/po/updateStatus":
                     updatePurchaseOrderStatus(req, resp);
                    break;
                case "/po/receive":
                    receivePurchaseOrder(req, resp);
                    break;
                default:
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Acquisition POST action not found.");
                    break;
            }
        } catch (SQLException e) {
            // Consider setting an error attribute and forwarding to an error page
            throw new ServletException("Database error in AcquisitionServlet POST", e);
        }
    }

    // --- Supplier Methods ---
    private void listSuppliers(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        List<Supplier> suppliers = supplierDAO.getAllSuppliers();
        req.setAttribute("suppliers", suppliers);
        req.getRequestDispatcher("/WEB-INF/jsp/acquisition/listSuppliers.jsp").forward(req, resp);
    }

    private void showAddSupplierForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/jsp/acquisition/addSupplier.jsp").forward(req, resp);
    }

    private void showEditSupplierForm(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        Supplier supplier = supplierDAO.getSupplierById(id);
        if (supplier != null) {
            req.setAttribute("supplier", supplier);
            req.getRequestDispatcher("/WEB-INF/jsp/acquisition/editSupplier.jsp").forward(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Supplier not found.");
        }
    }

    private void addSupplier(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        Supplier supplier = new Supplier();
        supplier.setSupplierName(req.getParameter("supplierName"));
        supplier.setContactPerson(req.getParameter("contactPerson"));
        supplier.setAddress(req.getParameter("address"));
        supplier.setPhoneNumber(req.getParameter("phoneNumber"));
        supplier.setEmail(req.getParameter("email"));
        supplier.setPaymentTerms(req.getParameter("paymentTerms"));
        Supplier createdSupplier = supplierDAO.addSupplier(supplier);
        // Audit Log
        User sessionUser = (User) req.getSession().getAttribute("user");
        if(sessionUser != null && auditLogDAO != null) {
            auditLogDAO.createLogEntry(new AuditLogEntry(sessionUser.getUserID(), sessionUser.getUsername(), "SUPPLIER_ADD", getClientIpAddr(req), "SupplierID: " + createdSupplier.getSupplierID() + ", Name: " + createdSupplier.getSupplierName()));
        }
        resp.sendRedirect(req.getContextPath() + "/acquisition/suppliers/list");
    }

    private void updateSupplier(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        Supplier supplier = new Supplier();
        supplier.setSupplierID(Integer.parseInt(req.getParameter("supplierID")));
        supplier.setSupplierName(req.getParameter("supplierName"));
        supplier.setContactPerson(req.getParameter("contactPerson"));
        supplier.setAddress(req.getParameter("address"));
        supplier.setPhoneNumber(req.getParameter("phoneNumber"));
        supplier.setEmail(req.getParameter("email"));
        supplier.setPaymentTerms(req.getParameter("paymentTerms"));
        boolean updated = supplierDAO.updateSupplier(supplier);
        // Audit Log
        User sessionUser = (User) req.getSession().getAttribute("user");
        if(updated && sessionUser != null && auditLogDAO != null) {
            auditLogDAO.createLogEntry(new AuditLogEntry(sessionUser.getUserID(), sessionUser.getUsername(), "SUPPLIER_UPDATE", getClientIpAddr(req), "SupplierID: " + supplier.getSupplierID() + ", Name: " + supplier.getSupplierName()));
        }
        resp.sendRedirect(req.getContextPath() + "/acquisition/suppliers/list");
    }

    private void deleteSupplier(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        Optional<Supplier> supplierOpt = supplierDAO.getSupplierById(id); // For logging name before delete

        // Add check for dependencies (POs) before deleting - IMPORTANT
        // For now, proceeding with delete for audit log example
        boolean deleted = supplierDAO.deleteSupplier(id);

        // Audit Log
        User sessionUser = (User) req.getSession().getAttribute("user");
        if(deleted && sessionUser != null && auditLogDAO != null && supplierOpt.isPresent()) {
            auditLogDAO.createLogEntry(new AuditLogEntry(sessionUser.getUserID(), sessionUser.getUsername(), "SUPPLIER_DELETE", getClientIpAddr(req), "SupplierID: " + id + ", Name: " + supplierOpt.get().getSupplierName()));
        } else if (deleted && sessionUser != null && auditLogDAO != null) {
            auditLogDAO.createLogEntry(new AuditLogEntry(sessionUser.getUserID(), sessionUser.getUsername(), "SUPPLIER_DELETE", getClientIpAddr(req), "SupplierID: " + id));
        }
        resp.sendRedirect(req.getContextPath() + "/acquisition/suppliers/list");
    }

    // --- Acquisition Request Methods ---
    private void listAcquisitionRequests(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        List<AcquisitionRequest> requests = acquisitionRequestDAO.getAllRequests();
        req.setAttribute("requests", requests);
        req.getRequestDispatcher("/WEB-INF/jsp/acquisition/listRequests.jsp").forward(req, resp);
    }

    private void showNewAcquisitionRequestForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/jsp/acquisition/newRequest.jsp").forward(req, resp);
    }

    private void viewAcquisitionRequest(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        AcquisitionRequest request = acquisitionRequestDAO.getRequestById(id);
        if (request != null) {
            req.setAttribute("request", request);
            req.getRequestDispatcher("/WEB-INF/jsp/acquisition/viewRequest.jsp").forward(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Acquisition Request not found.");
        }
    }

    private void createAcquisitionRequest(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        AcquisitionRequest ar = new AcquisitionRequest();
        ar.setBookTitle(req.getParameter("bookTitle"));
        ar.setAuthor(req.getParameter("author"));
        ar.setIsbn(req.getParameter("isbn"));
        ar.setPublisher(req.getParameter("publisher"));
        String pubYear = req.getParameter("publicationYear");
        if (pubYear != null && !pubYear.isEmpty()) {
            ar.setPublicationYear(Integer.parseInt(pubYear));
        }
        // Assuming user is logged in and User object is in session
        HttpSession session = req.getSession();
        User currentUser = (User) session.getAttribute("user"); // Adjust attribute name as needed
        if (currentUser == null) {
             // Handle not logged in user - redirect to login or show error
            resp.sendRedirect(req.getContextPath() + "/user/login?message=loginRequired");
            return;
        }
        ar.setRequestedBy(currentUser.getUserID()); // Assuming User model has getUserID()
        ar.setRequestDate(new Date(System.currentTimeMillis())); // Current date
        ar.setStatus("Pending"); // Default status
        ar.setNotes(req.getParameter("notes"));

        AcquisitionRequest createdAR = acquisitionRequestDAO.createRequest(ar);
        // Audit Log
        if(sessionUser != null && auditLogDAO != null && createdAR != null) {
            auditLogDAO.createLogEntry(new AuditLogEntry(sessionUser.getUserID(), sessionUser.getUsername(), "ACQ_REQUEST_CREATE", getClientIpAddr(req), "ReqID: " + createdAR.getRequestID() + ", Title: " + createdAR.getBookTitle()));
        }
        resp.sendRedirect(req.getContextPath() + "/acquisition/requests/list");
    }

    private void updateAcquisitionRequestStatus(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        int requestId = Integer.parseInt(req.getParameter("requestId"));
        String status = req.getParameter("status");
        // Add validation for status values
        boolean updated = acquisitionRequestDAO.updateRequestStatus(requestId, status);
        // Audit Log
        User sessionUser = (User) req.getSession().getAttribute("user");
        if(updated && sessionUser != null && auditLogDAO != null) {
             Optional<AcquisitionRequest> arOpt = acquisitionRequestDAO.getRequestById(requestId);
            auditLogDAO.createLogEntry(new AuditLogEntry(sessionUser.getUserID(), sessionUser.getUsername(), "ACQ_REQUEST_STATUS_UPDATE", getClientIpAddr(req), "ReqID: " + requestId + ", NewStatus: " + status + arOpt.map(ar -> ", Title: " + ar.getBookTitle()).orElse("")));
        }
        // Potentially redirect to the request view or list
        resp.sendRedirect(req.getContextPath() + "/acquisition/requests/view?id=" + requestId);
    }

    private void deleteAcquisitionRequest(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        Optional<AcquisitionRequest> arOpt = acquisitionRequestDAO.getRequestById(id); // For logging
        // Add validation, e.g., can only delete if status is 'Pending' or 'Rejected'
        boolean deleted = acquisitionRequestDAO.deleteRequest(id);
         // Audit Log
        User sessionUser = (User) req.getSession().getAttribute("user");
        if(deleted && sessionUser != null && auditLogDAO != null && arOpt.isPresent()) {
            auditLogDAO.createLogEntry(new AuditLogEntry(sessionUser.getUserID(), sessionUser.getUsername(), "ACQ_REQUEST_DELETE", getClientIpAddr(req), "ReqID: " + id + ", Title: " + arOpt.get().getBookTitle()));
        } else if (deleted && sessionUser != null && auditLogDAO != null) {
            auditLogDAO.createLogEntry(new AuditLogEntry(sessionUser.getUserID(), sessionUser.getUsername(), "ACQ_REQUEST_DELETE", getClientIpAddr(req), "ReqID: " + id));
        }
        resp.sendRedirect(req.getContextPath() + "/acquisition/requests/list");
    }


    // --- Purchase Order Methods ---
    private void listPurchaseOrders(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        List<PurchaseOrder> pos = purchaseOrderDAO.getAllPurchaseOrders();
        req.setAttribute("purchaseOrders", pos);
        req.getRequestDispatcher("/WEB-INF/jsp/acquisition/listPOs.jsp").forward(req, resp);
    }

    private void showNewPurchaseOrderForm(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        // Optionally, pass an AcquisitionRequest ID if creating PO from a request
        String requestIdParam = req.getParameter("requestId");
        if (requestIdParam != null) {
            AcquisitionRequest ar = acquisitionRequestDAO.getRequestById(Integer.parseInt(requestIdParam));
            req.setAttribute("sourceRequest", ar);
        }
        List<Supplier> suppliers = supplierDAO.getAllSuppliers();
        req.setAttribute("suppliers", suppliers);
        // Generate a unique PO number suggestion (can be overridden by user if needed)
        req.setAttribute("suggestedPONumber", "PO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        req.getRequestDispatcher("/WEB-INF/jsp/acquisition/newPO.jsp").forward(req, resp);
    }

    private void viewPurchaseOrder(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        int poId = Integer.parseInt(req.getParameter("id"));
        PurchaseOrder po = purchaseOrderDAO.getPurchaseOrderById(poId);
        if (po != null) {
            req.setAttribute("purchaseOrder", po);
            Supplier supplier = supplierDAO.getSupplierById(po.getSupplierID());
            req.setAttribute("supplier", supplier);
            List<GoodsReceiptNote> grns = goodsReceiptNoteDAO.getGoodsReceiptNotesByPOId(poId);
            req.setAttribute("grns", grns);
            req.getRequestDispatcher("/WEB-INF/jsp/acquisition/viewPO.jsp").forward(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Purchase Order not found.");
        }
    }

    private void createPurchaseOrder(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException, ServletException {
        PurchaseOrder po = new PurchaseOrder();
        po.setPoNumber(req.getParameter("poNumber"));
        po.setSupplierID(Integer.parseInt(req.getParameter("supplierID")));
        po.setOrderDate(Date.valueOf(req.getParameter("orderDate")));
        if(req.getParameter("expectedDeliveryDate") != null && !req.getParameter("expectedDeliveryDate").isEmpty()){
            po.setExpectedDeliveryDate(Date.valueOf(req.getParameter("expectedDeliveryDate")));
        }
        po.setStatus("Created"); // Initial status

        HttpSession session = req.getSession();
        User currentUser = (User) session.getAttribute("user");
         if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/user/login?message=loginRequiredForPO");
            return;
        }
        po.setCreatedBy(currentUser.getUserID());


        List<PurchaseOrderLine> lines = new ArrayList<>();
        String[] bookTitles = req.getParameterValues("line_bookTitle");
        String[] bookIds = req.getParameterValues("line_bookId"); // Optional, if linking to existing books
        String[] quantities = req.getParameterValues("line_quantity");
        String[] unitPrices = req.getParameterValues("line_unitPrice");

        if (bookTitles != null) {
            for (int i = 0; i < bookTitles.length; i++) {
                if (bookTitles[i] == null || bookTitles[i].trim().isEmpty()) continue; // Skip empty lines

                PurchaseOrderLine line = new PurchaseOrderLine();
                line.setRequestedBookTitle(bookTitles[i]);
                if (bookIds != null && bookIds[i] != null && !bookIds[i].isEmpty()) {
                    try {
                        line.setBookID(Integer.parseInt(bookIds[i]));
                    } catch (NumberFormatException e) { /* ignore if not a valid int */ }
                }
                try {
                    line.setQuantity(Integer.parseInt(quantities[i]));
                    line.setUnitPrice(new BigDecimal(unitPrices[i]));
                } catch (NumberFormatException e) {
                    // Handle error: invalid number format for quantity or price
                    req.setAttribute("errorMessage", "Invalid quantity or price for item: " + bookTitles[i]);
                    // Repopulate form with entered data and show error
                    // This part needs more robust error handling and form repopulation
                    showNewPurchaseOrderForm(req, resp);
                    return;
                }
                 if(line.getQuantity() <=0 || line.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0){
                    req.setAttribute("errorMessage", "Quantity and price must be positive for item: " + bookTitles[i]);
                    showNewPurchaseOrderForm(req, resp);
                    return;
                }
                lines.add(line);
            }
        }
        if(lines.isEmpty()){
            req.setAttribute("errorMessage", "A purchase order must have at least one item.");
            showNewPurchaseOrderForm(req, resp);
            return;
        }

        po.setOrderLines(lines);
        po.calculateTotalAmount(); // Calculate total based on lines

        PurchaseOrder createdPO = purchaseOrderDAO.createPurchaseOrder(po);
        // Audit Log
        if(sessionUser != null && auditLogDAO != null && createdPO != null) {
            auditLogDAO.createLogEntry(new AuditLogEntry(sessionUser.getUserID(), sessionUser.getUsername(), "PO_CREATE", getClientIpAddr(req), "PO_ID: " + createdPO.getPoID() + ", PONumber: " + createdPO.getPoNumber() + ", SupplierID: " + createdPO.getSupplierID()));
        }

        // If PO was created from an Acquisition Request, update request status
        String sourceRequestId = req.getParameter("sourceRequestId");
        if (sourceRequestId != null && !sourceRequestId.isEmpty()) {
            try {
                int reqId = Integer.parseInt(sourceRequestId);
                acquisitionRequestDAO.updateRequestStatus(reqId, "Ordered");
            } catch (NumberFormatException | SQLException e) {
                // Log error, but PO creation is successful
                System.err.println("Failed to update source acquisition request status: " + e.getMessage());
            }
        }

        resp.sendRedirect(req.getContextPath() + "/acquisition/po/view?id=" + po.getPoID());
    }

    private void updatePurchaseOrderStatus(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        int poId = Integer.parseInt(req.getParameter("poId"));
        String status = req.getParameter("status");
        // Add validation for status values
        boolean updated = purchaseOrderDAO.updatePurchaseOrderStatus(poId, status);
        // Audit Log
        User sessionUser = (User) req.getSession().getAttribute("user");
        if(updated && sessionUser != null && auditLogDAO != null) {
            auditLogDAO.createLogEntry(new AuditLogEntry(sessionUser.getUserID(), sessionUser.getUsername(), "PO_STATUS_UPDATE", getClientIpAddr(req), "PO_ID: " + poId + ", NewStatus: " + status));
        }
        resp.sendRedirect(req.getContextPath() + "/acquisition/po/view?id=" + poId);
    }

    // --- Goods Receipt Note (Receiving) Methods ---
    private void showReceivePurchaseOrderForm(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        int poId = Integer.parseInt(req.getParameter("poId"));
        PurchaseOrder po = purchaseOrderDAO.getPurchaseOrderById(poId);
        if (po == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Purchase Order not found for receiving.");
            return;
        }
        if ("Closed".equals(po.getStatus()) || "Fully Received".equals(po.getStatus())) {
             req.setAttribute("message", "This Purchase Order is already fully received or closed.");
             // Potentially redirect to PO view
        }
        req.setAttribute("purchaseOrder", po);
        // Fetch supplier details for display
        Supplier supplier = supplierDAO.getSupplierById(po.getSupplierID());
        req.setAttribute("supplier", supplier);
        req.getRequestDispatcher("/WEB-INF/jsp/acquisition/receivePO.jsp").forward(req, resp);
    }

    private void receivePurchaseOrder(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException, ServletException {
        GoodsReceiptNote grn = new GoodsReceiptNote();
        int poId = Integer.parseInt(req.getParameter("poId"));
        PurchaseOrder po = purchaseOrderDAO.getPurchaseOrderById(poId); // For supplier ID and validation
        if (po == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Associated PO not found for GRN.");
            return;
        }

        grn.setPoID(poId);
        grn.setSupplierID(po.getSupplierID()); // Get from PO
        grn.setInvoiceNumber(req.getParameter("invoiceNumber"));
        if (req.getParameter("invoiceDate") != null && !req.getParameter("invoiceDate").isEmpty()) {
            grn.setInvoiceDate(Date.valueOf(req.getParameter("invoiceDate")));
        }
        grn.setReceivedDate(Date.valueOf(req.getParameter("receivedDate"))); // Should default to today

        HttpSession session = req.getSession();
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
             resp.sendRedirect(req.getContextPath() + "/user/login?message=loginRequiredForGRN");
            return;
        }
        grn.setReceivedBy(currentUser.getUserID());
        grn.setNotes(req.getParameter("grnNotes"));

        List<GoodsReceiptNoteItem> grnItems = new ArrayList<>();
        // Parameters will be like: items[0].poLineId, items[0].bookId, items[0].receivedQuantity, etc.
        // This requires careful parsing based on how the form is structured.
        // Assuming form submits arrays: poLineIds[], bookIds[], receivedQuantities[], acceptedQuantities[], conditions[]

        String[] poLineIds = req.getParameterValues("item_poLineId");
        String[] bookIds = req.getParameterValues("item_bookId");
        String[] receivedQuantities = req.getParameterValues("item_receivedQuantity");
        String[] acceptedQuantities = req.getParameterValues("item_acceptedQuantity");
        String[] conditions = req.getParameterValues("item_condition");
        String[] itemNotes = req.getParameterValues("item_notes");

        if (poLineIds != null) {
            for (int i = 0; i < poLineIds.length; i++) {
                 if (receivedQuantities[i] == null || receivedQuantities[i].trim().isEmpty() || Integer.parseInt(receivedQuantities[i]) == 0) {
                    // Skip if no quantity received for this line
                    continue;
                }
                GoodsReceiptNoteItem item = new GoodsReceiptNoteItem();
                if (poLineIds[i] != null && !poLineIds[i].isEmpty()) {
                    item.setPoLineID(Integer.parseInt(poLineIds[i]));
                }
                item.setBookID(Integer.parseInt(bookIds[i])); // Book ID must be present
                item.setReceivedQuantity(Integer.parseInt(receivedQuantities[i]));
                item.setAcceptedQuantity(Integer.parseInt(acceptedQuantities[i]));
                item.setCondition(conditions[i]);
                item.setNotes(itemNotes[i]);

                if (item.getAcceptedQuantity() > item.getReceivedQuantity()) {
                     req.setAttribute("errorMessage", "Accepted quantity cannot exceed received quantity for Book ID: " + item.getBookID());
                     // Need to repopulate the form and show error
                     req.setAttribute("purchaseOrder", po);
                     req.setAttribute("supplier", supplierDAO.getSupplierById(po.getSupplierID()));
                     // TODO: Repopulate GRN fields and item lines from request parameters
                     req.getRequestDispatcher("/WEB-INF/jsp/acquisition/receivePO.jsp").forward(req, resp);
                     return;
                }
                grnItems.add(item);
            }
        }
         if(grnItems.isEmpty()){
            req.setAttribute("errorMessage", "At least one item must be marked as received to create a Goods Receipt Note.");
            req.setAttribute("purchaseOrder", po);
            req.setAttribute("supplier", supplierDAO.getSupplierById(po.getSupplierID()));
            req.getRequestDispatcher("/WEB-INF/jsp/acquisition/receivePO.jsp").forward(req, resp);
            return;
        }

        grn.setItems(grnItems);

        // This is where the GoodsReceiptNoteDAO would also handle creating Copy entries.
        // The current DAO stub has a placeholder for this.
        GoodsReceiptNote createdGrn = goodsReceiptNoteDAO.createGoodsReceiptNote(grn);
        // Audit Log
        if(sessionUser != null && auditLogDAO != null && createdGrn != null) {
            auditLogDAO.createLogEntry(new AuditLogEntry(sessionUser.getUserID(), sessionUser.getUsername(), "GRN_CREATE", getClientIpAddr(req), "GRN_ID: " + createdGrn.getGrnID() + ", PO_ID: " + createdGrn.getPoID() + ", Invoice: " + createdGrn.getInvoiceNumber()));
        }

        // Update PO Status based on received items (Partial or Full)
        // This logic needs to compare total ordered vs total received across all GRNs for this PO.
        // For simplicity, if any item is received, mark as 'Partially Received'.
        // A more robust check would be needed for 'Fully Received'.
        if (!"Fully Received".equals(po.getStatus())) {
            // Simplified: check if all ordered quantities are now met by accepted quantities across all GRNs for this PO.
            // This is complex and better handled by a service layer or a dedicated PO status update method.
            // For now, if items were received, and it's not yet fully received, mark as partially.
            // A dedicated method in PurchaseOrderDAO or a service could check if it's now fully received.
            boolean nowFullyReceived = checkPOFullyReceived(poId);
            if (nowFullyReceived) {
                 purchaseOrderDAO.updatePurchaseOrderStatus(poId, "Fully Received");
            } else {
                 purchaseOrderDAO.updatePurchaseOrderStatus(poId, "Partially Received");
            }
        }

        resp.sendRedirect(req.getContextPath() + "/acquisition/grn/view?id=" + grn.getGrnID());
    }

    private boolean checkPOFullyReceived(int poId) throws SQLException {
        PurchaseOrder po = purchaseOrderDAO.getPurchaseOrderById(poId);
        if (po == null || po.getOrderLines().isEmpty()) return false;

        List<GoodsReceiptNote> grnsForPo = goodsReceiptNoteDAO.getGoodsReceiptNotesByPOId(poId);

        for (PurchaseOrderLine poLine : po.getOrderLines()) {
            int totalAcceptedForLine = 0;
            for (GoodsReceiptNote grn : grnsForPo) {
                for (GoodsReceiptNoteItem grnItem : grn.getItems()) {
                    if (grnItem.getPoLineID() != null && grnItem.getPoLineID().equals(poLine.getPoLineID())) {
                        totalAcceptedForLine += grnItem.getAcceptedQuantity();
                    }
                }
            }
            if (totalAcceptedForLine < poLine.getQuantity()) {
                return false; // At least one line is not fully received
            }
        }
        return true; // All lines are fully received
    }


    private void listGoodsReceiptNotes(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        List<GoodsReceiptNote> grns = goodsReceiptNoteDAO.getAllGoodsReceiptNotes();
        req.setAttribute("goodsReceiptNotes", grns);
        req.getRequestDispatcher("/WEB-INF/jsp/acquisition/listGRNs.jsp").forward(req, resp);
    }

    private void viewGoodsReceiptNote(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        int grnId = Integer.parseInt(req.getParameter("id"));
        GoodsReceiptNote grn = goodsReceiptNoteDAO.getGoodsReceiptNoteById(grnId);
        if (grn != null) {
            req.setAttribute("grn", grn);
            PurchaseOrder po = purchaseOrderDAO.getPurchaseOrderById(grn.getPoID());
            req.setAttribute("purchaseOrder", po);
            Supplier supplier = supplierDAO.getSupplierById(grn.getSupplierID());
            req.setAttribute("supplier", supplier);
            req.getRequestDispatcher("/WEB-INF/jsp/acquisition/viewGRN.jsp").forward(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Goods Receipt Note not found.");
        }
    }

}
