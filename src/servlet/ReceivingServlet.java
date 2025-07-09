package servlet;

import dao.ReceivingDAO;
import model.GoodsReceiptNote;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;

public class ReceivingServlet extends HttpServlet {
    private ReceivingDAO receivingDAO;

    @Override
    public void init() {
        receivingDAO = new ReceivingDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            GoodsReceiptNote grn = new GoodsReceiptNote();
            grn.setPoId(Integer.parseInt(request.getParameter("poId")));
            grn.setReceiptDate(Date.valueOf(request.getParameter("receiptDate")));
            grn.setInvoiceNumber(request.getParameter("invoiceNumber"));
            grn.setInvoiceDate(Date.valueOf(request.getParameter("invoiceDate")));
            grn.setNotes(request.getParameter("notes"));
            receivingDAO.recordReceipt(grn);
            response.sendRedirect("PurchaseOrderServlet");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
