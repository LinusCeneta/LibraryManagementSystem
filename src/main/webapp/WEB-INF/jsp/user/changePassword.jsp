<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Change Password</title>
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
    <c:if test="${empty sessionScope.user}">
        <c:redirect url="${pageContext.request.contextPath}/user/login?message=loginRequired"/>
    </c:if>

    <div class="container" style="max-width: 500px;">
        <h1>Change Your Password</h1>

        <c:if test="${not empty errorMessage}">
            <p class="error-message"><c:out value="${errorMessage}"/></p>
        </c:if>
        <c:if test="${not empty message}">
            <p class="message"><c:out value="${message}"/></p>
        </c:if>

        <form action="${pageContext.request.contextPath}/user/changePassword" method="post">
            <input type="hidden" name="_csrf" value="${_csrf}">
            <div class="form-group">
                <label for="currentPassword">Current Password:</label>
                <input type="password" id="currentPassword" name="currentPassword" required>
            </div>
            <div class="form-group">
                <label for="newPassword">New Password:</label>
                <input type="password" id="newPassword" name="newPassword" required>
                <p class="password-strength-info">
                    Must be at least 8 characters, include uppercase, lowercase, digit, and special character.
                </p>
            </div>
            <div class="form-group">
                <label for="confirmNewPassword">Confirm New Password:</label>
                <input type="password" id="confirmNewPassword" name="confirmNewPassword" required>
            </div>
            <p>
                <button type="submit">Change Password</button>
                <a href="${pageContext.request.contextPath}/user/profile" class="button">Cancel</a>
            </p>
        </form>
    </div>
    <script>
        const form = document.querySelector('form[action$="/user/changePassword"]');
        if (form) {
            form.addEventListener('submit', function(event) {
                const newPassword = document.getElementById('newPassword').value;
                const confirmNewPassword = document.getElementById('confirmNewPassword').value;
                if (newPassword !== confirmNewPassword) {
                    alert("New passwords do not match.");
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
