<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>
<head>
    <title>Admin - Manage Users</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <c:if test="${empty sessionScope.user || sessionScope.user.role.roleName != 'ROLE_ADMIN'}">
        <c:redirect url="${pageContext.request.contextPath}/user/login?message=adminLoginRequired"/>
    </c:if>

    <div class="container">
        <h1>Manage Users</h1>
        <p><a href="${pageContext.request.contextPath}/user/register?admin=true">Add New User (as Admin)</a></p> <%-- TODO: Handle admin registration flow --%>

        <c:if test="${not empty message}">
            <p class="message"><c:out value="${message}"/></p>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <p class="error-message"><c:out value="${errorMessage}"/></p>
        </c:if>

        <table border="1">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Username</th>
                    <th>Email</th>
                    <th>Full Name</th>
                    <th>Role</th>
                    <th>Active</th>
                    <th>Date Registered</th>
                    <th>Last Login</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="u" items="${users}"> <%-- Changed var name from "user" to "u" to avoid potential conflict with sessionScope.user --%>
                    <tr>
                        <td>${u.userID}</td>
                        <td><c:out value="${u.username}"/></td>
                        <td><c:out value="${u.email}"/></td>
                        <td><c:out value="${u.firstName} ${u.lastName}"/></td>
                        <td><c:out value="${u.role.roleName}"/></td>
                        <td>${u.active ? 'Yes' : 'No'}</td>
                        <td><fmt:formatDate value="${u.dateRegistered}" pattern="yyyy-MM-dd HH:mm"/></td>
                        <td><fmt:formatDate value="${u.lastLoginDate}" pattern="yyyy-MM-dd HH:mm"/></td>
                        <td>
                            <a href="${pageContext.request.contextPath}/user/admin/editUser?userId=${u.userID}">Edit</a>
                            <c:if test="${u.userID != sessionScope.user.userID}"> <%-- Admin cannot delete self --%>
                                <form action="${pageContext.request.contextPath}/user/admin/deleteUser?userId=${u.userID}" method="post" style="display:inline;" onsubmit="return confirm('Are you sure you want to delete user ${u.username}? This action CANNOT be undone.');">
                                    <input type="hidden" name="_csrf" value="${_csrf}">
                                    <input type="hidden" name="userId" value="${u.userID}">
                                    <button type="submit" class="button-danger">Delete</button>
                                </form>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty users}">
                    <tr>
                        <td colspan="9">No users found.</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
        <p><a href="${pageContext.request.contextPath}/index.jsp">Back to Dashboard</a></p>
    </div>
</body>
</html>
