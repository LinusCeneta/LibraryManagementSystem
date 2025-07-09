package servlet;

import utils.CSRFTokenManager;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;

public class AdminServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        CSRFTokenManager.setToken(request.getSession());
        request.getRequestDispatcher("adminDashboard.jsp").forward(request, response);
    }
}
