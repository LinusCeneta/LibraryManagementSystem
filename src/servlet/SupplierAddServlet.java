package servlet;

import dao.SupplierDAO;
import model.Supplier;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/suppliers/add")
public class SupplierAddServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(SupplierAddServlet.class.getName());
    private SupplierDAO supplierDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        supplierDAO = new SupplierDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        generateAndSetCSRFToken(request);
        request.getRequestDispatcher("/supplierAdd.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logRequestParameters(request);
        
        if (!validateCSRFToken(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF Token");
            return;
        }

        try {
            Supplier supplier = createSupplierFromRequest(request);
            validateSupplier(supplier);
            
            boolean success = supplierDAO.create(supplier);
            if (!success) {
                throw new ServletException("Failed to create supplier");
            }

            request.getSession().setAttribute("successMessage", "Supplier added successfully!");
            response.sendRedirect(request.getContextPath() + "/suppliers");
            
        } catch (ValidationException e) {
            request.setAttribute("error", e.getMessage());
            prepareFormForRedisplay(request, request.getParameterMap());
            request.getRequestDispatcher("/supplierAdd.jsp").forward(request, response);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in SupplierAddServlet", e);
            request.setAttribute("error", "System error: " + e.getMessage());
            prepareFormForRedisplay(request, request.getParameterMap());
            request.getRequestDispatcher("/supplierAdd.jsp").forward(request, response);
        }
    }

    private void prepareFormForRedisplay(HttpServletRequest request, java.util.Map<String, String[]> params) {
        params.forEach((key, values) -> {
            if (values.length > 0) {
                request.setAttribute(key, values[0]);
            }
        });
        generateAndSetCSRFToken(request);
    }

    private Supplier createSupplierFromRequest(HttpServletRequest request) {
        Supplier supplier = new Supplier();
        supplier.setName(request.getParameter("name"));
        supplier.setContactPerson(request.getParameter("contactPerson"));
        supplier.setAddress(request.getParameter("address"));
        supplier.setPhone(request.getParameter("phone"));
        supplier.setEmail(request.getParameter("email"));
        supplier.setPaymentTerms(request.getParameter("paymentTerms"));
        return supplier;
    }

    private void validateSupplier(Supplier supplier) throws ValidationException {
        if (supplier.getName() == null || supplier.getName().trim().isEmpty()) {
            throw new ValidationException("Supplier name is required");
        }
    }

    private void generateAndSetCSRFToken(HttpServletRequest request) {
        String csrfToken = UUID.randomUUID().toString();
        request.getSession().setAttribute("csrfToken", csrfToken);
        request.setAttribute("csrfToken", csrfToken);
    }

    private boolean validateCSRFToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String sessionToken = session != null ? (String) session.getAttribute("csrfToken") : null;
        String requestToken = request.getParameter("csrfToken");

        return sessionToken != null && sessionToken.equals(requestToken);
    }

    private void logRequestParameters(HttpServletRequest request) {
        if (logger.isLoggable(Level.FINE)) {
            request.getParameterMap().forEach((key, values) -> {
                for (String value : values) {
                    logger.fine(key + ": " + value);
                }
            });
        }
    }

    private static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }
}