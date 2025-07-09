package servlet;

import dao.PurchaseRequestDAO;
import model.PurchaseRequest;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/purchase-request")
public class PurchaseRequestServlet extends HttpServlet {
    private PurchaseRequestDAO requestDAO;

    @Override
    public void init() {
        requestDAO = new PurchaseRequestDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<PurchaseRequest> requests = requestDAO.getAllRequests();
            request.setAttribute("requests", requests);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/purchaseRequest.jsp");
            dispatcher.forward(request, response);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}