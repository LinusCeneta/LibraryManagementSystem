<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Suppliers List</title>
    <%-- Add your CSS links here --%>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <h1>Suppliers</h1>
        <p><a href="${pageContext.request.contextPath}/acquisition/suppliers/add">Add New Supplier</a></p>

        <c:if test="${not empty message}">
            <p class="message">${message}</p>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <p class="error-message">${errorMessage}</p>
        </c:if>

        <table border="1">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Contact Person</th>
                    <th>Email</th>
                    <th>Phone</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="supplier" items="${suppliers}">
                    <tr>
                        <td>${supplier.supplierID}</td>
                        <td><c:out value="${supplier.supplierName}"/></td>
                        <td><c:out value="${supplier.contactPerson}"/></td>
                        <td><c:out value="${supplier.email}"/></td>
                        <td><c:out value="${supplier.phoneNumber}"/></td>
                        <td>
                            <a href="${pageContext.request.contextPath}/acquisition/suppliers/edit?id=${supplier.supplierID}">Edit</a>
                            <%-- Add delete confirmation later --%>
                            <form action="${pageContext.request.contextPath}/acquisition/suppliers/delete?id=${supplier.supplierID}" method="post" style="display:inline;" onsubmit="return confirm('Are you sure you want to delete this supplier?');">
                                <input type="hidden" name="_csrf" value="${_csrf}">
                                <input type="hidden" name="id" value="${supplier.supplierID}">
                                <button type="submit">Delete</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty suppliers}">
                    <tr>
                        <td colspan="6">No suppliers found.</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
        <p><a href="${pageContext.request.contextPath}/index.jsp">Back to Home</a></p>
    </div>
</body>
</html>
