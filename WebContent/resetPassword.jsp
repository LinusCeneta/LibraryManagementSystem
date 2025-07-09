<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="java.util.UUID" %>
<%
    String csrfToken = UUID.randomUUID().toString();
    session.setAttribute("csrfToken", csrfToken);
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Reset Password</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        :root {
            --primary: #5d4e6d;       /* Deep purple */
            --primary-dark: #423a4d;  /* Darker purple */
            --accent: #c8a97e;       /* Warm gold */
            --text-dark: #2a2630;     /* Dark grayish purple */
            --text-light: #857e8f;    /* Light grayish purple */
            --white: #ffffff;
            --off-white: #f9f7fa;
            --radius: 8px;
            --shadow: 0 4px 12px rgba(93, 78, 109, 0.1);
            --transition: all 0.3s ease;
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: 'Segoe UI', 'Georgia', sans-serif;
        }

        body {
            background-color: var(--off-white);
            color: var(--text-dark);
            padding: 20px;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
        }

        .container {
            max-width: 500px;
            width: 100%;
        }

        h2 {
            color: var(--primary);
            font-size: 1.8rem;
            border-bottom: 2px solid var(--accent);
            padding-bottom: 0.5rem;
            margin-bottom: 1.5rem;
            text-align: center;
        }

        .form-container {
            background-color: var(--white);
            padding: 2rem;
            border-radius: var(--radius);
            box-shadow: var(--shadow);
        }

        .form-group {
            margin-bottom: 1.5rem;
        }

        label {
            display: block;
            margin-bottom: 0.5rem;
            font-weight: 500;
            color: var(--primary-dark);
        }

        input[type="password"] {
            width: 100%;
            padding: 0.8rem 1rem;
            border: 1px solid var(--text-light);
            border-radius: var(--radius);
            font-size: 1rem;
            transition: var(--transition);
        }

        input[type="password"]:focus {
            border-color: var(--accent);
            outline: none;
            box-shadow: 0 0 0 2px rgba(200, 169, 126, 0.3);
        }

        .submit-btn {
            width: 100%;
            padding: 0.8rem;
            background-color: var(--accent);
            color: var(--text-dark);
            border: none;
            border-radius: var(--radius);
            cursor: pointer;
            font-weight: 500;
            transition: var(--transition);
            font-size: 1rem;
            margin-top: 0.5rem;
        }

        .submit-btn:hover {
            background-color: #b8975e;
            transform: translateY(-2px);
        }

        .status-message {
            padding: 0.8rem;
            border-radius: var(--radius);
            margin-bottom: 1rem;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        .error {
            background-color: rgba(231, 76, 60, 0.1);
            border-left: 4px solid #E74C3C;
            color: #E74C3C;
        }

        .success {
            background-color: rgba(39, 174, 96, 0.1);
            border-left: 4px solid #27AE60;
            color: #27AE60;
        }

        .password-requirements {
            font-size: 0.85rem;
            color: var(--text-light);
            margin-top: 0.5rem;
        }
    </style>
</head>
<body>
    <div class="container">
        <h2><i class="fas fa-key"></i> Reset Your Password</h2>

        <c:if test="${not empty error}">
            <div class="status-message error">
                <i class="fas fa-exclamation-circle"></i> ${error}
            </div>
        </c:if>

        <c:if test="${not empty message}">
            <div class="status-message success">
                <i class="fas fa-check-circle"></i> ${message}
            </div>
        </c:if>

        <div class="form-container">
            <form method="post" action="ResetPasswordServlet">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />
                <input type="hidden" name="userId" value="${userId}" />
                <input type="hidden" name="token" value="${token}" />
                
                <div class="form-group">
                    <label for="newPassword"><i class="fas fa-lock"></i> New Password</label>
                    <input type="password" id="newPassword" name="newPassword" required />
                    <div class="password-requirements">
                        <i class="fas fa-info-circle"></i> Must be at least 8 characters long
                    </div>
                </div>

                <button type="submit" class="submit-btn">
                    <i class="fas fa-sync-alt"></i> Reset Password
                </button>
            </form>
        </div>
    </div>
</body>
</html>
