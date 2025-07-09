package servlet;

import dao.BookDAO;
import model.Book;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

public class BookListServlet extends HttpServlet {
    private BookDAO bookDAO = new BookDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("[DEBUG] BookListServlet executed");
        
        List<Book> books = bookDAO.getAllBooks();
        System.out.println("[DEBUG] Number of books fetched: " + books.size());
        
        request.setAttribute("bookList", books);
        request.getRequestDispatcher("/bookList.jsp").forward(request, response);
    }
}