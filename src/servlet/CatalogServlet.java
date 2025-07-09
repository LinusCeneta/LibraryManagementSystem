package servlet;

import dao.CatalogDAO;
import model.Item;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class CatalogServlet extends HttpServlet {
    private CatalogDAO catalogDAO;

    @Override
    public void init() throws ServletException {
        try {
            catalogDAO = new CatalogDAO();
        } catch (SQLException e) {
            throw new ServletException("Error initializing CatalogDAO", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Forward to JSP with empty search results by default
        request.getRequestDispatcher("/catalog.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        String format = request.getParameter("format");
        String genre = request.getParameter("genre");
        String language = request.getParameter("language");
        String availability = request.getParameter("availability");

        try {
            List<Item> items = catalogDAO.searchCatalog(keyword, format, genre, language, availability);
            request.setAttribute("items", items);
            request.getRequestDispatcher("/catalog.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error searching catalog.");
            request.getRequestDispatcher("/catalog.jsp").forward(request, response);
        }
    }
}
