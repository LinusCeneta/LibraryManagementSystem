<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="navbar">
    <a href="${pageContext.request.contextPath}/index.jsp">Library Home</a>
    <a href="${pageContext.request.contextPath}/catalog/home">Catalog</a>

    <%-- Links for Staff/Admin --%>
    <c:if test="${sessionScope.user.role.roleName == 'ROLE_STAFF' || sessionScope.user.role.roleName == 'ROLE_ADMIN'}">
        <a href="${pageContext.request.contextPath}/acquisition/po/list">Acquisitions</a>
        <a href="${pageContext.request.contextPath}/inventory/adjustments/list">Inventory</a>
        <%-- Add link to Circulation module once JSPs are ready --%>
        <%-- <a href="${pageContext.request.contextPath}/circulation/checkout">Circulation Desk</a> --%>
    </c:if>
    <c:if test="${sessionScope.user.role.roleName == 'ROLE_ADMIN'}">
         <a href="${pageContext.request.contextPath}/user/admin/listUsers">Manage Users</a>
    </c:if>

    <%-- Links for Members --%>
     <c:if test="${not empty sessionScope.user && sessionScope.user.role.roleName == 'ROLE_MEMBER'}">
        <%-- Add links like "My Loans", "My Holds", "My Fines" once available --%>
        <%-- <a href="${pageContext.request.contextPath}/circulation/myLoans">My Loans</a> --%>
    </c:if>


    <div style="float:right;">
        <c:choose>
            <c:when test="${not empty sessionScope.user}">
                <span style="color:white; margin-right:10px;">
                    Welcome, <a href="${pageContext.request.contextPath}/user/profile" style="color: #ddd;"><c:out value="${sessionScope.user.username}"/></a>
                    (<c:out value="${sessionScope.user.role.roleName}"/>)
                </span>
                <a href="${pageContext.request.contextPath}/user/logout">Logout</a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/user/login">Login</a>
                <a href="${pageContext.request.contextPath}/user/register">Register</a>
            </c:otherwise>
        </c:choose>
    </div>
</div>
