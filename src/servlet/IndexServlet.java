package servlet;

import dao.IndexDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

public class IndexServlet extends HttpServlet {
    private IndexDAO indexDAO;

    @Override
    public void init() throws ServletException {
        indexDAO = new IndexDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int totalUsers = indexDAO.getTotalUsers();
            request.setAttribute("totalUsers", totalUsers);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error loading homepage data.");
        }

        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}
