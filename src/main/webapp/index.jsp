<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Library Management System - Home</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .module-links { list-style: none; padding: 0; }
        .module-links li { margin-bottom: 10px; }
    </style>
</head>
<body>
    <div class="navbar">
        <a href="${pageContext.request.contextPath}/index.jsp">Home</a>
        <%-- Basic User Info/Login Link --%>
        <c:choose>
            <c:when test="${not empty sessionScope.user}">
                <span>Welcome, <c:out value="${sessionScope.user.username}"/> (<c:out value="${sessionScope.user.role}"/>)</span>
                <a href="${pageContext.request.contextPath}/user/logout">Logout</a> <%-- Assuming /user/logout path --%>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/user/login">Login</a> <%-- Assuming /user/login path --%>
            </c:otherwise>
        </c:choose>
    </div>

    <div class="container">
        <h1>Welcome to the Library Management System</h1>

        <c:if test="${not empty message}">
            <p class="message"><c:out value="${message}"/></p>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <p class="error-message"><c:out value="${errorMessage}"/></p>
        </c:if>

        <p>This is the central hub for all library operations. Please select a module to continue.</p>

        <h2>Available Modules</h2>
        <ul class="module-links">
            <%-- Links to other modules would go here --%>
            <%-- Example: <li><a href="${pageContext.request.contextPath}/catalog/search">Catalog Search</a></li> --%>

            <c:if test="${sessionScope.user.role == 'ROLE_STAFF' || sessionScope.user.role == 'ROLE_ADMIN'}">
                <li>
                    <h3>Acquisitions Management</h3>
                    <ul class="sub-module-links">
                        <li><a href="${pageContext.request.contextPath}/acquisition/suppliers/list">Manage Suppliers</a></li>
                        <li><a href="${pageContext.request.contextPath}/acquisition/requests/list">Manage Acquisition Requests</a></li>
                        <li><a href="${pageContext.request.contextPath}/acquisition/po/list">Manage Purchase Orders</a></li>
                        <li><a href="${pageContext.request.contextPath}/acquisition/grn/list">Manage Goods Receipt Notes</a></li>
                    </ul>
                </li>
                <li>
                    <h3>Inventory Management</h3>
                     <ul class="sub-module-links">
                        <li><a href="${pageContext.request.contextPath}/inventory/adjustments/list">Manage Inventory Adjustments</a></li>
                        <%-- <li><a href="${pageContext.request.contextPath}/inventory/audits/list">Manage Inventory Audits</a></li> --%>
                    </ul>
                </li>
            </c:if>

            <%-- Add more module links based on user role and system features --%>
            <%-- <li><a href="#">Member Management</a></li> --%>
            <%-- <li><a href="#">Circulation Desk</a></li> --%>
            <%-- <li><a href="#">Reports & Analytics</a></li> --%>
        </ul>

    </div>

    <div class="footer">
        <p>&copy; <%= new java.text.SimpleDateFormat("yyyy").format(new java.util.Date()) %> Library Management System. All rights reserved.</p>
    </div>
</body>
</html>
