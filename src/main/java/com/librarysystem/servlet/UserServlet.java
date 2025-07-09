package com.librarysystem.servlet;

import com.librarysystem.dao.AuditLogDAO;
import com.librarysystem.dao.PasswordResetTokenDAO;
import com.librarysystem.dao.RoleDAO;
import com.librarysystem.dao.UserDAO;
import com.librarysystem.dao.UserProfileDAO;
import com.librarysystem.model.AuditLogEntry;
import com.librarysystem.model.PasswordResetToken;
import com.librarysystem.model.Role;
import com.librarysystem.model.User;
import com.librarysystem.model.UserProfile;
import com.librarysystem.util.PasswordUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@WebServlet("/user/*")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
    maxFileSize = 1024 * 1024 * 5,   // 5 MB
    maxRequestSize = 1024 * 1024 * 10 // 10 MB
)
public class UserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L; // Recommended for servlets

    private UserDAO userDAO;
    private RoleDAO roleDAO;
    private UserProfileDAO userProfileDAO;
    private PasswordResetTokenDAO passwordResetTokenDAO;
    private AuditLogDAO auditLogDAO;

    // Define a path for profile photo uploads. This should be configurable.
    // IMPORTANT: This path must be writable by the Tomcat process.
    // For production, this should be outside the webapp deployment directory.
    private String UPLOAD_DIR;


    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
        roleDAO = new RoleDAO();
        userProfileDAO = new UserProfileDAO();
        passwordResetTokenDAO = new PasswordResetTokenDAO();
        auditLogDAO = new AuditLogDAO();

        // Initialize UPLOAD_DIR. For a real app, read from web.xml <context-param> or properties file.
        // Example: Relative to the application context's real path.
        // String contextPath = getServletContext().getRealPath("/");
        // UPLOAD_DIR = contextPath + File.separator + "profile_uploads";
        // For simplicity in this example, let's use a fixed path (adjust for your system if testing locally)
        // THIS IS NOT PRODUCTION-READY for upload path.
        String catalinaHome = System.getProperty("catalina.home");
        if (catalinaHome != null) {
             UPLOAD_DIR = catalinaHome + File.separator + "library_uploads" + File.separator + "profile_photos";
        } else {
            // Fallback for environments where catalina.home might not be set (e.g. embedded tomcat during tests)
            // This path might not be ideal or writable.
            UPLOAD_DIR = System.getProperty("java.io.tmpdir") + File.separator + "library_uploads" + File.separator + "profile_photos";
            System.out.println("Warning: 'catalina.home' not set. Using temporary directory for uploads: " + UPLOAD_DIR);
        }

        File uploadDirFile = new File(UPLOAD_DIR);
        if (!uploadDirFile.exists()) {
            if (uploadDirFile.mkdirs()) {
                System.out.println("Upload directory created: " + UPLOAD_DIR);
            } else {
                System.err.println("Error: Could not create upload directory: " + UPLOAD_DIR);
                // Consider throwing ServletException if upload is critical
            }
        }
         System.out.println("Profile photo upload directory set to: " + UPLOAD_DIR);


        // Ensure default roles exist on application startup
        try {
            roleDAO.ensureDefaultRolesExist();
        } catch (SQLException e) {
            throw new ServletException("Failed to ensure default roles exist", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getPathInfo();
        if (action == null) action = "/profile"; // Default action for logged-in user

        try {
            switch (action) {
                case "/register":
                    showRegisterForm(req, resp);
                    break;
                case "/login":
                    showLoginForm(req, resp);
                    break;
                case "/logout":
                    logoutUser(req, resp);
                    break;
                case "/profile":
                    viewUserProfile(req, resp);
                    break;
                case "/editProfile":
                    showEditProfileForm(req, resp);
                    break;
                case "/changePassword":
                    showChangePasswordForm(req, resp);
                    break;
                case "/forgotPassword":
                    showForgotPasswordForm(req, resp);
                    break;
                case "/resetPassword": // Handles link from email
                    showResetPasswordForm(req, resp);
                    break;
                // Admin actions
                case "/admin/listUsers":
                    listUsers(req, resp);
                    break;
                case "/admin/editUser": // Show form to edit user (admin)
                    showAdminEditUserForm(req, resp);
                    break;
                default:
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "User action not found.");
            }
        } catch (SQLException e) {
            throw new ServletException("Database error in UserServlet GET", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getPathInfo();
         if (action == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No POST action specified.");
            return;
        }

        try {
            // TODO: Implement CSRF token validation for all POST requests here or in a filter

            switch (action) {
                case "/register":
                    registerUser(req, resp);
                    break;
                case "/login":
                    loginUser(req, resp);
                    break;
                case "/editProfile":
                    updateUserProfile(req, resp);
                    break;
                case "/changePassword":
                    changePassword(req, resp);
                    break;
                case "/forgotPassword":
                    processForgotPasswordRequest(req, resp);
                    break;
                case "/resetPassword":
                    processPasswordReset(req, resp);
                    break;
                // Admin actions
                case "/admin/editUser": // Process admin edit user form
                    adminUpdateUser(req, resp);
                    break;
                 case "/admin/deleteUser":
                    adminDeleteUser(req, resp);
                    break;
                default:
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "User POST action not found.");
            }
        } catch (SQLException e) {
            throw new ServletException("Database error in UserServlet POST", e);
        }
    }

    // --- Helper to get client IP ---
    private String getClientIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    // --- Show form methods (GET) ---
    private void showRegisterForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/jsp/user/register.jsp").forward(req, resp);
    }

    private void showLoginForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/jsp/user/login.jsp").forward(req, resp);
    }

    private void viewUserProfile(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, SQLException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/user/login?message=loginRequired");
            return;
        }
        User sessionUser = (User) session.getAttribute("user");
        // Reload user data from DB to ensure it's fresh, including profile
        Optional<User> userOpt = userDAO.findUserById(sessionUser.getUserID());
        if(userOpt.isPresent()){
            User user = userOpt.get();
            Optional<UserProfile> profileOpt = userProfileDAO.getUserProfileByUserId(user.getUserID());
            profileOpt.ifPresent(user::setUserProfile); // Attach profile to user object
            req.setAttribute("currentUser", user); // Use "currentUser" to avoid conflict with session "user"
            req.getRequestDispatcher("/WEB-INF/jsp/user/profile.jsp").forward(req, resp);
        } else {
            session.invalidate(); // User not found, invalidate session
            resp.sendRedirect(req.getContextPath() + "/user/login?message=userNotFound");
        }
    }

    private void showEditProfileForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, SQLException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/user/login?message=loginRequired");
            return;
        }
        User sessionUser = (User) session.getAttribute("user");
        Optional<User> userOpt = userDAO.findUserById(sessionUser.getUserID());
         if(userOpt.isPresent()){
            User user = userOpt.get();
            Optional<UserProfile> profileOpt = userProfileDAO.getUserProfileByUserId(user.getUserID());
            user.setUserProfile(profileOpt.orElse(new UserProfile())); // Ensure profile object exists
            req.setAttribute("currentUser", user);
            req.getRequestDispatcher("/WEB-INF/jsp/user/editProfile.jsp").forward(req, resp);
        } else {
            session.invalidate();
            resp.sendRedirect(req.getContextPath() + "/user/login?message=userNotFound");
        }
    }

    private void showChangePasswordForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
         HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/user/login?message=loginRequired");
            return;
        }
        req.getRequestDispatcher("/WEB-INF/jsp/user/changePassword.jsp").forward(req, resp);
    }

    private void showForgotPasswordForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/jsp/user/forgotPassword.jsp").forward(req, resp);
    }

    private void showResetPasswordForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, SQLException {
        String token = req.getParameter("token");
        if (token == null || token.isEmpty()) {
            req.setAttribute("errorMessage", "Invalid or missing password reset token.");
            req.getRequestDispatcher("/WEB-INF/jsp/user/forgotPassword.jsp").forward(req, resp);
            return;
        }
        Optional<PasswordResetToken> tokenOpt = passwordResetTokenDAO.findByToken(token);
        if (tokenOpt.isPresent() && !tokenOpt.get().isUsed() && !tokenOpt.get().isExpired()) {
            req.setAttribute("token", token);
            req.getRequestDispatcher("/WEB-INF/jsp/user/resetPassword.jsp").forward(req, resp);
        } else {
            req.setAttribute("errorMessage", "Password reset token is invalid, expired, or already used.");
             if(tokenOpt.isPresent()) auditLogDAO.createLogEntry(new AuditLogEntry(tokenOpt.get().getUserID(), null, "PW_RESET_LINK_FAIL", getClientIpAddr(req), "Token invalid/expired/used: "+token));
            req.getRequestDispatcher("/WEB-INF/jsp/user/forgotPassword.jsp").forward(req, resp);
        }
    }


    // --- Process form methods (POST) ---
    private void registerUser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, SQLException {
        User user = new User();
        user.setUsername(req.getParameter("username"));
        user.setEmail(req.getParameter("email"));
        user.setFirstName(req.getParameter("firstName"));
        user.setLastName(req.getParameter("lastName"));

        String plainPassword = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");

        if (!PasswordUtil.isPasswordStrong(plainPassword)) {
            req.setAttribute("errorMessage", PasswordUtil.getPasswordStrengthCriteriaMessage());
            req.setAttribute("user", user); // Repopulate form
            showRegisterForm(req, resp);
            return;
        }
        if (!plainPassword.equals(confirmPassword)) {
            req.setAttribute("errorMessage", "Passwords do not match.");
            req.setAttribute("user", user);
            showRegisterForm(req, resp);
            return;
        }

        // Check if username or email already exists
        if (userDAO.findUserByUsername(user.getUsername()).isPresent()) {
            req.setAttribute("errorMessage", "Username already exists.");
            req.setAttribute("user", user);
            showRegisterForm(req, resp);
            return;
        }
        if (userDAO.findUserByEmail(user.getEmail()).isPresent()) {
            req.setAttribute("errorMessage", "Email already registered.");
            req.setAttribute("user", user);
            showRegisterForm(req, resp);
            return;
        }

        // Default role for new registration (e.g., ROLE_MEMBER)
        Optional<Role> defaultRole = roleDAO.getRoleByName("ROLE_MEMBER");
        if (!defaultRole.isPresent()) {
            // This should not happen if ensureDefaultRolesExist() ran
            throw new ServletException("Default role 'ROLE_MEMBER' not found.");
        }
        user.setRole(defaultRole.get());
        user.setActive(true); // New users are active by default

        // Optional: UserProfile details from registration form
        UserProfile profile = new UserProfile();
        profile.setPhoneNumber(req.getParameter("phoneNumber"));
        profile.setAddressLine1(req.getParameter("addressLine1"));
        // ... (set other profile fields if they are on the registration form)
        user.setUserProfile(profile); // UserDAO will save this if present

        User createdUser = userDAO.createUser(user, plainPassword);
        auditLogDAO.createLogEntry(new AuditLogEntry(createdUser.getUserID(), createdUser.getUsername(), "USER_REGISTER_SUCCESS", getClientIpAddr(req), "Role: " + defaultRole.get().getRoleName()));

        req.setAttribute("message", "Registration successful! Please login.");
        showLoginForm(req, resp);
    }

    private void loginUser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, SQLException {
        String usernameOrEmail = req.getParameter("usernameOrEmail");
        String plainPassword = req.getParameter("password");

        Optional<User> userOpt = userDAO.findUserByUsername(usernameOrEmail);
        if (!userOpt.isPresent()) {
            userOpt = userDAO.findUserByEmail(usernameOrEmail);
        }

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (!user.isActive()) {
                auditLogDAO.createLogEntry(new AuditLogEntry(user.getUserID(), user.getUsername(), "LOGIN_FAIL_INACTIVE", getClientIpAddr(req), null));
                req.setAttribute("errorMessage", "Account is inactive. Please contact support.");
                showLoginForm(req, resp);
                return;
            }

            // Fetch full user details including Role object
            User fullUser = userDAO.findUserById(user.getUserID()).orElse(user); // Fallback to basic user if full fetch fails

            if (PasswordUtil.checkPassword(plainPassword, fullUser.getPasswordHash())) {
                HttpSession session = req.getSession(true); // Create new session
                session.setAttribute("user", fullUser); // Store full User object

                userDAO.recordLogin(fullUser.getUserID());
                auditLogDAO.createLogEntry(new AuditLogEntry(fullUser.getUserID(), fullUser.getUsername(), "LOGIN_SUCCESS", getClientIpAddr(req), "Role: " + fullUser.getRole().getRoleName()));

                // Optional "Remember Me" cookie
                // ...

                resp.sendRedirect(req.getContextPath() + "/index.jsp"); // Redirect to dashboard or home
            } else {
                auditLogDAO.createLogEntry(new AuditLogEntry(fullUser.getUserID(), fullUser.getUsername(), "LOGIN_FAIL_PASSWORD", getClientIpAddr(req), null));
                req.setAttribute("errorMessage", "Invalid username/email or password.");
                showLoginForm(req, resp);
            }
        } else {
             auditLogDAO.createLogEntry(new AuditLogEntry(null, usernameOrEmail, "LOGIN_FAIL_NOUSER", getClientIpAddr(req), null));
            req.setAttribute("errorMessage", "Invalid username/email or password.");
            showLoginForm(req, resp);
        }
    }

    private void logoutUser(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                 auditLogDAO.createLogEntry(new AuditLogEntry(user.getUserID(), user.getUsername(), "LOGOUT_SUCCESS", getClientIpAddr(req), null));
            }
            session.invalidate();
        }
        resp.sendRedirect(req.getContextPath() + "/user/login?message=logoutSuccess");
    }

    private void updateUserProfile(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, SQLException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/user/login?message=loginRequired");
            return;
        }
        User sessionUser = (User) session.getAttribute("user");

        // Fetch the existing user and profile to update
        Optional<User> userOpt = userDAO.findUserById(sessionUser.getUserID());
        if (!userOpt.isPresent()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "User not found.");
            return;
        }
        User userToUpdate = userOpt.get();

        // Update core User fields (if they are editable on this form)
        userToUpdate.setFirstName(req.getParameter("firstName"));
        userToUpdate.setLastName(req.getParameter("lastName"));
        // Email might need special handling (verification if changed)
        // userToUpdate.setEmail(req.getParameter("email"));

        // Handle profile photo upload
        Part filePart = req.getPart("profilePhoto");
        if (filePart != null && filePart.getSize() > 0) {
            String originalFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            String fileExtension = "";
            int i = originalFileName.lastIndexOf('.');
            if (i > 0) {
                fileExtension = originalFileName.substring(i); // .jpg, .png
            }
            // Create a unique filename to avoid collisions
            String uniqueFileName = sessionUser.getUserID() + "_" + UUID.randomUUID().toString() + fileExtension;
            File uploads = new File(UPLOAD_DIR);
            if (!uploads.exists()) {
                uploads.mkdirs(); // Ensure directory exists
            }
            File file = new File(uploads, uniqueFileName);

            try {
                filePart.write(file.getAbsolutePath());
                // Store relative path or full URL depending on how you serve images
                userToUpdate.setProfilePhotoURL("profile_photos/" + uniqueFileName); // Example relative path
            } catch (IOException e) {
                req.setAttribute("errorMessage", "Error uploading profile photo: " + e.getMessage());
                // Allow profile update to continue without photo if other fields are valid
                 System.err.println("File upload error: " + e.getMessage());
            }
        }


        // Update UserProfile fields
        UserProfile profile = userProfileDAO.getUserProfileByUserId(sessionUser.getUserID()).orElse(new UserProfile());
        profile.setUserID(sessionUser.getUserID()); // Ensure UserID is set
        profile.setAddressLine1(req.getParameter("addressLine1"));
        profile.setAddressLine2(req.getParameter("addressLine2"));
        profile.setCity(req.getParameter("city"));
        profile.setStateProvince(req.getParameter("stateProvince"));
        profile.setPostalCode(req.getParameter("postalCode"));
        profile.setCountry(req.getParameter("country"));
        profile.setPhoneNumber(req.getParameter("phoneNumber"));
        String birthDateStr = req.getParameter("birthDate");
        if (birthDateStr != null && !birthDateStr.isEmpty()) {
            profile.setBirthDate(Date.valueOf(birthDateStr));
        } else {
            profile.setBirthDate(null);
        }
        profile.setEmergencyContactName(req.getParameter("emergencyContactName"));
        profile.setEmergencyContactPhone(req.getParameter("emergencyContactPhone"));

        userToUpdate.setUserProfile(profile); // Attach profile to user for DAO to handle

        userDAO.updateUserCoreDetails(userToUpdate); // This method in DAO should also handle profile update/create

        // Update session user object if details changed
        User updatedSessionUser = userDAO.findUserById(sessionUser.getUserID()).orElse(sessionUser); // Re-fetch
        userProfileDAO.getUserProfileByUserId(updatedSessionUser.getUserID()).ifPresent(updatedSessionUser::setUserProfile);
        session.setAttribute("user", updatedSessionUser);

        auditLogDAO.createLogEntry(new AuditLogEntry(sessionUser.getUserID(), sessionUser.getUsername(), "PROFILE_UPDATE_SUCCESS", getClientIpAddr(req), null));
        req.setAttribute("message", "Profile updated successfully.");
        viewUserProfile(req, resp); // Forward to profile view
    }

    private void changePassword(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, SQLException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/user/login?message=loginRequired");
            return;
        }
        User sessionUser = (User) session.getAttribute("user");

        String currentPassword = req.getParameter("currentPassword");
        String newPassword = req.getParameter("newPassword");
        String confirmNewPassword = req.getParameter("confirmNewPassword");

        Optional<String> currentHashOpt = userDAO.getPasswordHash(sessionUser.getUserID());
        if (!currentHashOpt.isPresent() || !PasswordUtil.checkPassword(currentPassword, currentHashOpt.get())) {
            req.setAttribute("errorMessage", "Incorrect current password.");
            showChangePasswordForm(req, resp);
            return;
        }

        if (!PasswordUtil.isPasswordStrong(newPassword)) {
            req.setAttribute("errorMessage", "New password is not strong enough. " + PasswordUtil.getPasswordStrengthCriteriaMessage());
            showChangePasswordForm(req, resp);
            return;
        }
        if (!newPassword.equals(confirmNewPassword)) {
            req.setAttribute("errorMessage", "New passwords do not match.");
            showChangePasswordForm(req, resp);
            return;
        }

        userDAO.updateUserPassword(sessionUser.getUserID(), newPassword);
        auditLogDAO.createLogEntry(new AuditLogEntry(sessionUser.getUserID(), sessionUser.getUsername(), "PASSWORD_CHANGE_SUCCESS", getClientIpAddr(req), null));
        req.setAttribute("message", "Password changed successfully.");
        viewUserProfile(req, resp); // Or redirect to login if re-authentication is desired
    }

    private void processForgotPasswordRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, SQLException {
        String email = req.getParameter("email");
        Optional<User> userOpt = userDAO.findUserByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Invalidate any existing valid tokens for this user
            Optional<PasswordResetToken> existingTokenOpt = passwordResetTokenDAO.findByUserIdAndValid(user.getUserID());
            if(existingTokenOpt.isPresent()){
                passwordResetTokenDAO.markTokenAsUsed(existingTokenOpt.get().getToken()); // Mark old one used
            }

            PasswordResetToken prToken = passwordResetTokenDAO.createPasswordResetToken(user.getUserID());

            // Simulate sending email
            String resetLink = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath() + "/user/resetPassword?token=" + prToken.getToken();
            System.out.println("Password Reset Link for " + user.getEmail() + ": " + resetLink); // Log for now
            // In a real app, use JavaMail API to send the email.

            auditLogDAO.createLogEntry(new AuditLogEntry(user.getUserID(), user.getUsername(), "PW_RESET_REQUEST_SUCCESS", getClientIpAddr(req), "Token: " + prToken.getToken().substring(0,8) + "..."));
            req.setAttribute("message", "If an account with that email exists, a password reset link has been sent (check server console for link).");
        } else {
            // Generic message to avoid confirming if email exists
            auditLogDAO.createLogEntry(new AuditLogEntry(null, email, "PW_RESET_REQUEST_FAIL_NOEMAIL", getClientIpAddr(req), null));
            req.setAttribute("message", "If an account with that email exists, a password reset link has been sent (check server console for link).");
        }
        showForgotPasswordForm(req, resp);
    }

    private void processPasswordReset(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, SQLException {
        String token = req.getParameter("token");
        String newPassword = req.getParameter("newPassword");
        String confirmNewPassword = req.getParameter("confirmNewPassword");

        Optional<PasswordResetToken> tokenOpt = passwordResetTokenDAO.findByToken(token);

        if (!tokenOpt.isPresent() || tokenOpt.get().isUsed() || tokenOpt.get().isExpired()) {
            req.setAttribute("errorMessage", "Password reset token is invalid, expired, or already used.");
            auditLogDAO.createLogEntry(new AuditLogEntry(tokenOpt.map(PasswordResetToken::getUserID).orElse(null), null, "PW_RESET_SUBMIT_FAIL_TOKEN", getClientIpAddr(req), "Token: "+token));
            showForgotPasswordForm(req, resp); // Redirect to request new token
            return;
        }

        if (!PasswordUtil.isPasswordStrong(newPassword)) {
            req.setAttribute("errorMessage", "New password is not strong enough. " + PasswordUtil.getPasswordStrengthCriteriaMessage());
            req.setAttribute("token", token); // Keep token for the form
            showResetPasswordForm(req, resp);
            return;
        }
        if (!newPassword.equals(confirmNewPassword)) {
            req.setAttribute("errorMessage", "Passwords do not match.");
            req.setAttribute("token", token);
            showResetPasswordForm(req, resp);
            return;
        }

        PasswordResetToken prToken = tokenOpt.get();
        userDAO.updateUserPassword(prToken.getUserID(), newPassword);
        passwordResetTokenDAO.markTokenAsUsed(prToken.getToken());

        auditLogDAO.createLogEntry(new AuditLogEntry(prToken.getUserID(), null, "PW_RESET_SUCCESS", getClientIpAddr(req), "Token: "+token));
        req.setAttribute("message", "Password has been reset successfully. Please login with your new password.");
        showLoginForm(req, resp);
    }

    // --- Admin specific methods ---
    private void listUsers(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, SQLException {
        // Role check is now primarily handled by AuthenticationFilter and web.xml security constraints.
        // This explicit check can remain as a defense-in-depth measure or be removed if filter setup is robust.
        // HttpSession session = req.getSession(false);
        // if (session == null || session.getAttribute("user") == null ||
        //     !((User)session.getAttribute("user")).getRole().getRoleName().equals("ROLE_ADMIN")) {
        //     resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied.");
        //     return;
        // }
        List<User> users = userDAO.findAllUsers();
        req.setAttribute("users", users);
        req.getRequestDispatcher("/WEB-INF/jsp/admin/listUsers.jsp").forward(req, resp);
    }

    private void showAdminEditUserForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, SQLException {
        // Role check handled by filter/web.xml
        // HttpSession session = req.getSession(false);
        // if (session == null || session.getAttribute("user") == null ||
        //     !((User)session.getAttribute("user")).getRole().getRoleName().equals("ROLE_ADMIN")) {
        //     resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied.");
        //     return;
        // }

        int userIdToEdit = Integer.parseInt(req.getParameter("userId"));
        Optional<User> userOpt = userDAO.findUserById(userIdToEdit);
        if (userOpt.isPresent()) {
            User userToEdit = userOpt.get();
            userProfileDAO.getUserProfileByUserId(userIdToEdit).ifPresent(userToEdit::setUserProfile);

            req.setAttribute("userToEdit", userToEdit);
            req.setAttribute("allRoles", roleDAO.getAllRoles()); // For role dropdown
            req.getRequestDispatcher("/WEB-INF/jsp/admin/editUser.jsp").forward(req, resp);
        } else {
            req.setAttribute("errorMessage", "User not found.");
            listUsers(req, resp);
        }
    }

    private void adminUpdateUser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, SQLException {
        // Role check handled by filter/web.xml
        HttpSession session = req.getSession(false); // Still need session for audit log
        User sessionUser = (session != null) ? (User) session.getAttribute("user") : null;
        // if (sessionUser == null || !sessionUser.getRole().getRoleName().equals("ROLE_ADMIN")) {
        //     resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied.");
        //     return;
        // }

        int userIdToEdit = Integer.parseInt(req.getParameter("userID"));
        Optional<User> userOpt = userDAO.findUserById(userIdToEdit);
        if (!userOpt.isPresent()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "User to update not found.");
            return;
        }
        User userToUpdate = userOpt.get();

        // Update core details
        userToUpdate.setUsername(req.getParameter("username"));
        userToUpdate.setEmail(req.getParameter("email"));
        userToUpdate.setFirstName(req.getParameter("firstName"));
        userToUpdate.setLastName(req.getParameter("lastName"));
        userToUpdate.setActive(Boolean.parseBoolean(req.getParameter("isActive")));

        int newRoleId = Integer.parseInt(req.getParameter("roleID"));
        // Fetch the Role object for the new RoleID to ensure it's valid
        Role newRole = roleDAO.getRoleById(newRoleId)
                            .orElseThrow(() -> new SQLException("Invalid Role ID: " + newRoleId));
        userToUpdate.setRole(newRole); // Set the Role object
        userToUpdate.setRoleID(newRoleId); // Also ensure RoleID is set directly if mapRowToUser uses it

        // Update profile (if fields are on admin form)
        UserProfile profile = userProfileDAO.getUserProfileByUserId(userIdToEdit).orElse(new UserProfile());
        profile.setUserID(userIdToEdit);
        profile.setPhoneNumber(req.getParameter("phoneNumber")); // Example
        // ... set other profile fields from request ...
        userToUpdate.setUserProfile(profile);

        userDAO.updateUserCoreDetails(userToUpdate); // Updates user and its profile
        userDAO.updateUserRole(userIdToEdit, newRoleId); // Separate call to update role if not part of core details update

        // Optional: Admin can reset password
        String newPassword = req.getParameter("newPassword");
        if (newPassword != null && !newPassword.isEmpty()) {
            if (!PasswordUtil.isPasswordStrong(newPassword)){
                 req.setAttribute("errorMessage", "New password not strong enough. " + PasswordUtil.getPasswordStrengthCriteriaMessage());
                 req.setAttribute("userToEdit", userToUpdate); // Repopulate
                 req.setAttribute("allRoles", roleDAO.getAllRoles());
                 req.getRequestDispatcher("/WEB-INF/jsp/admin/editUser.jsp").forward(req, resp);
                 return;
            }
            userDAO.updateUserPassword(userIdToEdit, newPassword);
             auditLogDAO.createLogEntry(new AuditLogEntry(sessionUser.getUserID(), sessionUser.getUsername(), "ADMIN_PW_RESET_USER", getClientIpAddr(req), "TargetUserID: " + userIdToEdit));
        }

        auditLogDAO.createLogEntry(new AuditLogEntry(sessionUser.getUserID(), sessionUser.getUsername(), "ADMIN_UPDATE_USER", getClientIpAddr(req), "TargetUserID: " + userIdToEdit));
        req.setAttribute("message", "User updated successfully.");
        resp.sendRedirect(req.getContextPath() + "/user/admin/listUsers");
    }

    private void adminDeleteUser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, SQLException {
        // Role check handled by filter/web.xml
        HttpSession session = req.getSession(false); // Still need session for audit log
        User sessionUser = (session != null) ? (User) session.getAttribute("user") : null;
        // if (sessionUser == null || !sessionUser.getRole().getRoleName().equals("ROLE_ADMIN")) {
        //     resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied.");
        //     return;
        // }
        int userIdToDelete = Integer.parseInt(req.getParameter("userId"));

        // It's critical that an admin cannot delete themselves.
        // This check should remain even with filters, as a crucial business rule.
        if (sessionUser != null && userIdToDelete == sessionUser.getUserID()) {
            req.setAttribute("errorMessage", "Admin cannot delete their own account.");
            // Forward back to listUsers, which itself is protected.
            // Ensure auditLogDAO and userDAO are initialized if this method is called without full init.
            if (auditLogDAO == null) auditLogDAO = new AuditLogDAO();
            if (userDAO == null) userDAO = new UserDAO();
            auditLogDAO.createLogEntry(new AuditLogEntry(sessionUser.getUserID(), sessionUser.getUsername(), "ADMIN_DELETE_SELF_ATTEMPT", getClientIpAddr(req), "TargetUserID: " + userIdToDelete));
            listUsers(req, resp);
            return;
        }

        // Consider additional checks, e.g., cannot delete if user has active loans, etc.
        // For now, DAO handles cascade for UserProfile and PasswordResetTokens.
        userDAO.deleteUser(userIdToDelete);
        if (sessionUser != null) { // sessionUser might be null if filter somehow failed but execution proceeded.
             auditLogDAO.createLogEntry(new AuditLogEntry(sessionUser.getUserID(), sessionUser.getUsername(), "ADMIN_DELETE_USER", getClientIpAddr(req), "TargetUserID: " + userIdToDelete));
        } else {
            // Log with null userID if sessionUser is unexpectedly null
             auditLogDAO.createLogEntry(new AuditLogEntry(null, "UNKNOWN_ADMIN?", "ADMIN_DELETE_USER_NO_SESSION_USER", getClientIpAddr(req), "TargetUserID: " + userIdToDelete));
        }
        req.setAttribute("message", "User deleted successfully.");
        resp.sendRedirect(req.getContextPath() + "/user/admin/listUsers");
    }

}
