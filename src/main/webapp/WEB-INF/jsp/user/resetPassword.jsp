<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Reset Password</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; margin-bottom: 5px; }
        .form-group input[type="password"] {
            width: 100%;
            padding: 8px;
            box-sizing: border-box;
        }
        .password-strength-info { font-size: 0.9em; color: #666; margin-top: 5px;}
    </style>
</head>
<body>
    <div class="container" style="max-width: 500px;">
        <h1>Reset Your Password</h1>

        <c:if test="${not empty errorMessage}">
            <p class="error-message"><c:out value="${errorMessage}"/></p>
        </c:if>
        <c:if test="${not empty message}">
            <p class="message"><c:out value="${message}"/></p>
        </c:if>

        <c:if test="${empty token && empty errorMessage}">
            <p class="error-message">Invalid password reset link. Please request a new one if needed.</p>
            <p><a href="${pageContext.request.contextPath}/user/forgotPassword">Request New Reset Link</a></p>
        </c:if>

        <c:if test="${not empty token}">
            <form action="${pageContext.request.contextPath}/user/resetPassword" method="post">
                <input type="hidden" name="_csrf" value="${_csrf}">
                <input type="hidden" name="token" value="<c:out value="${token}"/>">

                <div class="form-group">
                    <label for="newPassword">New Password:</label>
                    <input type="password" id="newPassword" name="newPassword" required autofocus>
                     <p class="password-strength-info">
                        Must be at least 8 characters, include uppercase, lowercase, digit, and special character.
                    </p>
                </div>
                <div class="form-group">
                    <label for="confirmNewPassword">Confirm New Password:</label>
                    <input type="password" id="confirmNewPassword" name="confirmNewPassword" required>
                </div>
                <button type="submit">Reset Password</button>
            </form>
        </c:if>
        <p><a href="${pageContext.request.contextPath}/user/login">Back to Login</a></p>
    </div>
    <script>
        const form = document.querySelector('form[action$="/user/resetPassword"]');
        if (form) {
            form.addEventListener('submit', function(event) {
                const newPassword = document.getElementById('newPassword').value;
                const confirmNewPassword = document.getElementById('confirmNewPassword').value;
                if (newPassword !== confirmNewPassword) {
                    alert("Passwords do not match.");
                    event.preventDefault();
                }
                 if (newPassword.length < 8) { // Basic client side check
                    alert("New password must be at least 8 characters long.");
                    event.preventDefault();
                }
            });
        }
    </script>
</body>
</html>
