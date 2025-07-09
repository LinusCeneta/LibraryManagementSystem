package servlet;

import dao.CopyDAO;
import model.Copy;
import model.Condition;
import model.Status;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/copies/edit")
public class CopyEditServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(CopyEditServlet.class.getName());
    private final CopyDAO copyDao = new CopyDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        System.out.println("[DEBUG] CopyEditServlet - doGet() called");
        String copyId = request.getParameter("copyId");
        System.out.println("[DEBUG] Editing copy with ID: " + copyId);
        
        try {
            Copy copy = copyDao.findById(copyId);
            System.out.println("[DEBUG] Found copy: " + (copy != null ? copy.getCopyId() : "null"));
            
            if (copy == null) {
                System.out.println("[ERROR] Copy not found with ID: " + copyId);
                throw new ServletException("Copy not found with ID: " + copyId);
            }
            
            request.setAttribute("copy", copy);
            System.out.println("[DEBUG] Forwarding to editCopy.jsp");
            request.getRequestDispatcher("/editCopy.jsp").forward(request, response);
        } catch (Exception e) {
            System.out.println("[ERROR] Error in CopyEditServlet: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error editing copy: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("[DEBUG] CopyEditServlet - doPost() called");
        
        try {
            Copy copy = createCopyFromRequest(request);
            boolean success = copyDao.update(copy);
            
            if (!success) {
                throw new ServletException("Failed to update copy");
            }
            
            // Add success message and redirect
            request.getSession().setAttribute("message", "Copy updated successfully");
            response.sendRedirect(request.getContextPath() + "/copies");
        } catch (Exception e) {
            System.out.println("[ERROR] Error updating copy: " + e.getMessage());
            // Get the copy ID from the request to repopulate the form
            String copyId = request.getParameter("copyId");
            Copy copy = copyDao.findById(copyId); // Re-fetch the copy
            
            request.setAttribute("copy", copy); // Fixed - now properly passing the copy object
            request.setAttribute("error", "Error updating copy: " + e.getMessage());
            request.getRequestDispatcher("/editCopy.jsp").forward(request, response);
        }
    }


    private Copy createCopyFromRequest(HttpServletRequest request) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        return new Copy(
            request.getParameter("copyId"),
            request.getParameter("isbn"),
            sdf.parse(request.getParameter("acquisitionDate")),
            Double.parseDouble(request.getParameter("cost")),
            Condition.valueOf(request.getParameter("condition")),
            request.getParameter("location"),
            Status.valueOf(request.getParameter("status"))
        );
    }
}