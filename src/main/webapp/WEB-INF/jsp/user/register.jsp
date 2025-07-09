<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>User Registration</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; margin-bottom: 5px; }
        .form-group input[type="text"],
        .form-group input[type="email"],
        .form-group input[type="password"] {
            width: 100%;
            padding: 8px;
            box-sizing: border-box;
        }
        .password-strength-info { font-size: 0.9em; color: #666; margin-top: 5px;}
    </style>
</head>
<body>
    <div class="container">
        <h1>User Registration</h1>

        <c:if test="${not empty errorMessage}">
            <p class="error-message"><c:out value="${errorMessage}"/></p>
        </c:if>
        <c:if test="${not empty message}">
            <p class="message"><c:out value="${message}"/></p>
        </c:if>

        <form action="${pageContext.request.contextPath}/user/register" method="post">
            <input type="hidden" name="_csrf" value="${_csrf}">
            <div class="form-group">
                <label for="username">Username:</label>
                <input type="text" id="username" name="username" value="<c:out value="${user.username}"/>" required>
            </div>
            <div class="form-group">
                <label for="email">Email:</label>
                <input type="email" id="email" name="email" value="<c:out value="${user.email}"/>" required>
            </div>
            <div class="form-group">
                <label for="firstName">First Name:</label>
                <input type="text" id="firstName" name="firstName" value="<c:out value="${user.firstName}"/>" required>
            </div>
            <div class="form-group">
                <label for="lastName">Last Name:</label>
                <input type="text" id="lastName" name="lastName" value="<c:out value="${user.lastName}"/>" required>
            </div>
            <div class="form-group">
                <label for="password">Password:</label>
                <input type="password" id="password" name="password" required>
                <p class="password-strength-info">
                    Must be at least 8 characters, include uppercase, lowercase, digit, and special character.
                </p>
            </div>
            <div class="form-group">
                <label for="confirmPassword">Confirm Password:</label>
                <input type="password" id="confirmPassword" name="confirmPassword" required>
            </div>

            <%-- Optional UserProfile fields during registration --%>
            <fieldset>
                <legend>Optional Profile Information</legend>
                 <div class="form-group">
                    <label for="phoneNumber">Phone Number:</label>
                    <input type="tel" id="phoneNumber" name="phoneNumber" value="<c:out value="${user.userProfile.phoneNumber}"/>">
                </div>
                <div class="form-group">
                    <label for="addressLine1">Address Line 1:</label>
                    <input type="text" id="addressLine1" name="addressLine1" value="<c:out value="${user.userProfile.addressLine1}"/>">
                </div>
                <%-- Add more UserProfile fields here if needed: addressLine2, city, etc. --%>
            </fieldset>

            <button type="submit">Register</button>
        </form>
        <p>Already have an account? <a href="${pageContext.request.contextPath}/user/login">Login here</a>.</p>
        <p><a href="${pageContext.request.contextPath}/index.jsp">Back to Home</a></p>
    </div>

    <%-- Basic client-side validation for password match (strength check better on server or more robust JS) --%>
    <script>
        const form = document.querySelector('form[action$="/user/register"]');
        if (form) {
            form.addEventListener('submit', function(event) {
                const password = document.getElementById('password').value;
                const confirmPassword = document.getElementById('confirmPassword').value;
                if (password !== confirmPassword) {
                    alert("Passwords do not match.");
                    event.preventDefault();
                }
                // Basic client-side check for password length (more comprehensive checks on server)
                if (password.length < 8) {
                    alert("Password must be at least 8 characters long.");
                    event.preventDefault();
                }
            });
        }
    </script>
</body>
</html>
