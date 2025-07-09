package servlet;

import dao.BookDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

public class BookDeleteServlet extends HttpServlet {
    private BookDAO bookDAO = new BookDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String isbn = request.getParameter("isbn");
        if (isbn != null) {
            bookDAO.delete(isbn);
        }
        response.sendRedirect(request.getContextPath() + "/book-list");
    }
}
