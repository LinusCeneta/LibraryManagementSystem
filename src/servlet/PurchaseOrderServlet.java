package servlet;

import dao.PurchaseOrderDAO;
import dao.PurchaseRequestDAO;
import model.PurchaseOrder;
import model.PurchaseRequest;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/po/purchaseorder")  
public class PurchaseOrderServlet extends HttpServlet {
    private PurchaseOrderDAO poDAO;
    private PurchaseRequestDAO prDAO;

    @Override
    public void init() throws ServletException {
        System.out.println("======== INITIALIZING " + getClass().getSimpleName() + " ========");
        System.out.println("Servlet mapped to: /po/purchaseorder");
        poDAO = new PurchaseOrderDAO();
        prDAO = new PurchaseRequestDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("=== REQUEST START ===");
        System.out.println("Path: " + request.getServletPath());

        String action = request.getParameter("action");
        
        try {
            if (action == null) {
                // Handle main PO list
                List<PurchaseOrder> pos = poDAO.getAllPurchaseOrders();
                request.setAttribute("purchaseOrders", pos);
                System.out.println("Before forward to poForm.jsp");
                request.getRequestDispatcher("/poForm.jsp").forward(request, response);
                
            } else if ("view".equals(action)) {
                // Handle single PO view
                int poId = Integer.parseInt(request.getParameter("poId"));
                PurchaseOrder po = poDAO.getPurchaseOrderById(poId);
                
                if (po == null) {
                    request.setAttribute("error", "Purchase order not found");
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                    return;
                }
                
                request.setAttribute("purchaseOrder", po);
                System.out.println("Before forward to poView.jsp");
                request.getRequestDispatcher("/poView.jsp").forward(request, response);
                
            } else if ("convert".equals(action)) {
                // Handle conversion form
                int requestId = Integer.parseInt(request.getParameter("requestId"));
                PurchaseRequest pr = prDAO.getRequestById(requestId);
                
                if (pr == null || !"Approved".equals(pr.getStatus())) {
                    request.setAttribute("error", "Request not found or not approved");
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                    return;
                }
                
                request.setAttribute("request", pr);
                System.out.println("Before forward to poConvert.jsp");
                request.getRequestDispatcher("/poConvert.jsp").forward(request, response);
            }
            
        } catch (NumberFormatException | SQLException e) {
            System.out.println("ERROR: " + e.getMessage());
            request.setAttribute("error", "Error processing request: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("=== REQUEST START ===");
        System.out.println("Path: " + request.getServletPath());

        String action = request.getParameter("action");
        
        try {
            if ("convert".equals(action)) {
                // Handle PO conversion
                int requestId = Integer.parseInt(request.getParameter("requestId"));
                PurchaseRequest pr = prDAO.getRequestById(requestId);
                
                if (pr == null || !"Approved".equals(pr.getStatus())) {
                    request.setAttribute("error", "Request not found or not approved");
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                    return;
                }
                
                PurchaseOrder po = new PurchaseOrder();
                po.setPoNumber(request.getParameter("poNumber"));
                po.setSupplierId(Integer.parseInt(request.getParameter("supplierId")));
                po.setCreatedDate(new Date(System.currentTimeMillis()));
                po.setExpectedDeliveryDate(Date.valueOf(request.getParameter("expectedDeliveryDate")));
                po.setStatus("Created");
                po.setRequestId(requestId);
                
                int poId = poDAO.createPurchaseOrder(po);
                
                if (poId > 0) {
                    prDAO.updateStatus(requestId, "Converted to PO");
                    response.sendRedirect(request.getContextPath() + "/po/purchaseorder?action=view&poId=" + poId);
                } else {
                    request.setAttribute("error", "Failed to create purchase order");
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                }
            }
            
        } catch (IllegalArgumentException | SQLException e) {
            System.out.println("ERROR: " + e.getMessage());
            request.setAttribute("error", "Error processing request: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }
}