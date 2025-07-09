<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>User Login</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
     <style>
        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; margin-bottom: 5px; }
        .form-group input[type="text"],
        .form-group input[type="password"] {
            width: 100%;
            padding: 8px;
            box-sizing: border-box;
        }
    </style>
</head>
<body>
    <div class="container" style="max-width: 400px;">
        <h1>User Login</h1>

        <c:if test="${not empty param.message}">
            <p class="message">
                <c:choose>
                    <c:when test="${param.message == 'logoutSuccess'}">You have been logged out successfully.</c:when>
                    <c:when test="${param.message == 'loginRequired'}">Please login to continue.</c:when>
                    <c:when test="${param.message == 'userNotFound'}">User account not found. Please check your credentials or register.</c:when>
                    <c:when test="${param.message == 'passwordResetSuccess'}">Password reset successfully. Please login with your new password.</c:when>
                    <c:otherwise><c:out value="${param.message}"/></c:otherwise>
                </c:choose>
            </p>
        </c:if>
        <c:if test="${not empty message}"> <%-- For messages set by servlet directly --%>
             <p class="message"><c:out value="${message}"/></p>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <p class="error-message"><c:out value="${errorMessage}"/></p>
        </c:if>

        <form action="${pageContext.request.contextPath}/user/login" method="post">
            <input type="hidden" name="_csrf" value="${_csrf}">
            <div class="form-group">
                <label for="usernameOrEmail">Username or Email:</label>
                <input type="text" id="usernameOrEmail" name="usernameOrEmail" value="<c:out value="${param.usernameOrEmail}"/>" required autofocus>
            </div>
            <div class="form-group">
                <label for="password">Password:</label>
                <input type="password" id="password" name="password" required>
            </div>
            <%-- Optional "Remember Me" checkbox
            <div class="form-group">
                <input type="checkbox" id="rememberMe" name="rememberMe">
                <label for="rememberMe" style="display:inline;">Remember Me</label>
            </div>
            --%>
            <button type="submit">Login</button>
        </form>
        <p><a href="${pageContext.request.contextPath}/user/forgotPassword">Forgot Password?</a></p>
        <p>Don't have an account? <a href="${pageContext.request.contextPath}/user/register">Register here</a>.</p>
        <p><a href="${pageContext.request.contextPath}/index.jsp">Back to Home</a></p>
    </div>
</body>
</html>
