package servlet;

import model.Member;
import dao.MemberDAO;
import utils.CSRFTokenManager;
import utils.SecurityUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "RegisterMemberServlet", 
           urlPatterns = "/register-member",
           loadOnStartup = 1)
public class RegisterMemberServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(RegisterMemberServlet.class.getName());
    private static final Set<String> ALLOWED_STATUSES = Set.of("Active", "Inactive", "Pending");
    private static final Set<String> ALLOWED_MEMBERSHIP_TYPES = Set.of("Member", "Staff", "Admin");

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        logger.log(Level.INFO, "Entering doPost() method");

        // Debug logging
        System.out.println("=== FORM SUBMISSION DATA ===");
        Collections.list(request.getParameterNames())
                  .forEach(name -> System.out.println(name + ": " + request.getParameter(name)));
        
        // CSRF validation
        String sessionToken = (String) request.getSession().getAttribute("csrfToken");
        String requestToken = request.getParameter("csrfToken");
        System.out.println("CSRF Validation - Session: " + sessionToken + " vs Request: " + requestToken);
        
        if (!validateCsrfToken(request)) {
            logger.log(Level.WARNING, "CSRF token validation failed");
            request.setAttribute("errorMessage", "Invalid CSRF token. Please try again.");
            forwardToForm(request, response);
            return;
        }

        // Create member from parameters
        Member member = new Member();
        member.setName(request.getParameter("name"));
        member.setEmail(request.getParameter("email"));
        member.setPhone(request.getParameter("phone"));
        member.setMemberId(request.getParameter("memberId"));
        member.setMembershipType(request.getParameter("membershipType"));
        member.setStatus(request.getParameter("status"));
        
        System.out.println("Member object created: " + member.toString());

        // Validate
        Map<String, String> errors = new HashMap<>();
        validateMember(member, errors);
        
        if (!errors.isEmpty()) {
            System.out.println("Validation errors: " + errors);
            request.setAttribute("fieldErrors", errors);
            request.setAttribute("submittedValues", createSubmittedValuesMap(member));
            forwardToForm(request, response);
            return;
        }

        // Save to database
            try {
                MemberDAO memberDAO = new MemberDAO();
                if (memberDAO.registerMember(member)) {
                    logger.log(Level.INFO, "Member registration successful for {0}", member.getEmail());
                    
                    // Store member in session to display in profile
                    request.getSession().setAttribute("currentMember", member);
                    request.getSession().setAttribute("successMessage", "Member registered successfully!");
                    
                    // Redirect to profile page
                    response.sendRedirect(request.getContextPath() + "/memberProfile.jsp");
                    return;
                } else {
                    request.setAttribute("errorMessage", "Failed to register member in database");
                    forwardToForm(request, response);
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Database error", e);
                request.setAttribute("errorMessage", "System error: " + e.getMessage());
                forwardToForm(request, response);
            }
        
        if (!errors.isEmpty()) {
            logger.log(Level.WARNING, "Validation failed with {0} errors", errors.size());
            request.setAttribute("fieldErrors", errors);
            request.setAttribute("submittedValues", createSubmittedValuesMap(member));
            forwardToForm(request, response);
            return;
        }

    }

    private void validateMember(Member member, Map<String, String> errors) {
        // Name validation
        if (member.getName() == null || member.getName().trim().isEmpty()) {
            errors.put("name", "Name is required");
        } else if (member.getName().length() < 2 || member.getName().length() > 100) {
            errors.put("name", "Name must be 2-100 characters");
        }

        // Email validation
        if (member.getEmail() == null || member.getEmail().trim().isEmpty()) {
            errors.put("email", "Email is required");
        } else if (!SecurityUtils.isValidEmail(member.getEmail())) {
            errors.put("email", "Invalid email format");
        }

        // Phone validation
        if (member.getPhone() == null || member.getPhone().trim().isEmpty()) {
            errors.put("phone", "Phone number is required");
        } else if (!SecurityUtils.isValidPhone(member.getPhone())) {
            errors.put("phone", "Invalid phone number");
        }

        // Member ID validation
        if (member.getMemberId() == null || member.getMemberId().trim().isEmpty()) {
            errors.put("memberId", "Member ID is required");
        } else if (!member.getMemberId().matches("^[a-zA-Z0-9]{5,20}$")) {
            errors.put("memberId", "Member ID must be 5-20 alphanumeric characters");
        }

        // Status validation
        if (member.getStatus() == null || !ALLOWED_STATUSES.contains(member.getStatus())) {
            errors.put("status", "Invalid status selected");
        }

        // Membership type validation
        if (member.getMembershipType() == null || !ALLOWED_MEMBERSHIP_TYPES.contains(member.getMembershipType())) {
            errors.put("membershipType", "Invalid membership type selected");
        }
    }

    private Map<String, String> createSubmittedValuesMap(Member member) {
        Map<String, String> values = new HashMap<>();
        values.put("name", member.getName());
        values.put("memberId", member.getMemberId());
        values.put("email", member.getEmail());
        values.put("phone", member.getPhone());
        values.put("status", member.getStatus());
        values.put("membershipType", member.getMembershipType());
        return values;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.log(Level.INFO, "Entering doGet() method");
        
        // Generate CSRF token
        CSRFTokenManager.setToken(request.getSession());
        
        // Set form options
        request.setAttribute("statusOptions", ALLOWED_STATUSES);
        request.setAttribute("membershipTypeOptions", ALLOWED_MEMBERSHIP_TYPES);
        
        request.getRequestDispatcher("/registerMember.jsp").forward(request, response);
    }

    private boolean validateCsrfToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }

        String sessionToken = (String) session.getAttribute("csrfToken");
        String requestToken = request.getParameter("csrfToken");

        return sessionToken != null && requestToken != null && sessionToken.equals(requestToken);
    }

    private void forwardToForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Regenerate CSRF token for the form
        CSRFTokenManager.setToken(request.getSession());
        request.getRequestDispatcher("/member-profile").forward(request, response);
    }
}