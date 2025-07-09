<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Forgot Password</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; margin-bottom: 5px; }
        .form-group input[type="email"] {
            width: 100%;
            padding: 8px;
            box-sizing: border-box;
        }
    </style>
</head>
<body>
    <div class="container" style="max-width: 450px;">
        <h1>Forgot Your Password?</h1>
        <p>Enter your email address below, and if an account exists, we'll send you a link to reset your password.</p>

        <c:if test="${not empty message}">
            <p class="message"><c:out value="${message}"/></p>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <p class="error-message"><c:out value="${errorMessage}"/></p>
        </c:if>

        <form action="${pageContext.request.contextPath}/user/forgotPassword" method="post">
            <input type="hidden" name="_csrf" value="${_csrf}">
            <div class="form-group">
                <label for="email">Your Email Address:</label>
                <input type="email" id="email" name="email" required autofocus>
            </div>
            <button type="submit">Send Reset Link</button>
        </form>
        <p>
            <a href="${pageContext.request.contextPath}/user/login">Back to Login</a><br>
            <a href="${pageContext.request.contextPath}/index.jsp">Back to Home</a>
        </p>
    </div>
</body>
</html>
