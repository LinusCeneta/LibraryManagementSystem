package servlet;

import dao.InventoryAdjustmentDAO;
import model.InventoryAdjustment;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;

public class InventoryAdjustmentServlet extends HttpServlet {
    private InventoryAdjustmentDAO adjustmentDAO;

    @Override
    public void init() {
        adjustmentDAO = new InventoryAdjustmentDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            InventoryAdjustment adj = new InventoryAdjustment();
            adj.setCopyId(Integer.parseInt(request.getParameter("copyId")));
            adj.setAdjustmentType(request.getParameter("adjustmentType"));
            adj.setReason(request.getParameter("reason"));
            adj.setAdjustmentDate(Date.valueOf(request.getParameter("adjustmentDate")));
            adj.setUser(request.getParameter("user"));
            adjustmentDAO.addAdjustment(adj);
            response.sendRedirect("InventoryAdjustmentServlet");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("inventoryAdjustment.jsp");
        dispatcher.forward(request, response);
    }
}
