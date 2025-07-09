<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <title>Purchase Orders</title>
    <style>
        .error { color: red; }
        .success { color: green; }
        table { border-collapse: collapse; width: 100%; }
        th, td { padding: 8px; text-align: left; border-bottom: 1px solid #ddd; }
        tr:hover { background-color: #f5f5f5; }
    </style>
</head>
<body>
<h2>Purchase Orders</h2>

<c:if test="${not empty error}">
    <p class="error">${error}</p>
</c:if>
<c:if test="${not empty successMessage}">
    <p class="success">${successMessage}</p>
    <c:remove var="successMessage" scope="session" />
</c:if>
<c:if test="${not empty errorMessage}">
    <p class="error">${errorMessage}</p>
    <c:remove var="errorMessage" scope="session" />
</c:if>

<a href="${pageContext.request.contextPath}/po/add">Add New PO</a>

<h3>PO List</h3>
<c:choose>
    <c:when test="${empty purchaseOrders}">
        <p>No purchase orders found.</p>
    </c:when>
    <c:otherwise>
        <table>
            <tr>
                <th>ID</th>
                <th>PO Number</th>
                <th>Supplier ID</th>
                <th>Created Date</th>
                <th>Expected Delivery</th>
                <th>Status</th>
                <th>Actions</th>
            </tr>
            <c:forEach var="po" items="${purchaseOrders}">
                <tr>
                    <td>${po.poId}</td>
                    <td>${po.poNumber}</td>
                    <td>${po.supplierId}</td>
                    <td><fmt:formatDate value="${po.createdDate}" pattern="yyyy-MM-dd" /></td>
                    <td><fmt:formatDate value="${po.expectedDeliveryDate}" pattern="yyyy-MM-dd" /></td>
                    <td>${po.status}</td>
                    <td>
                        <form action="${pageContext.request.contextPath}/po/update" method="post">
                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />
                            <input type="hidden" name="poId" value="${po.poId}" />
                            <select name="status">
                                <option value="Created" ${po.status == 'Created' ? 'selected' : ''}>Created</option>
                                <option value="Submitted" ${po.status == 'Submitted' ? 'selected' : ''}>Submitted</option>
                                <option value="Partially Received" ${po.status == 'Partially Received' ? 'selected' : ''}>Partially Received</option>
                                <option value="Fully Received" ${po.status == 'Fully Received' ? 'selected' : ''}>Fully Received</option>
                                <option value="Closed" ${po.status == 'Closed' ? 'selected' : ''}>Closed</option>
                            </select>
                            <input type="submit" value="Update" />
                        </form>
                        <form action="${pageContext.request.contextPath}/po/update" method="post" style="display:inline;">
                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />
                            <input type="hidden" name="action" value="delete" />
                            <input type="hidden" name="poId" value="${po.poId}" />
                            <input type="submit" value="Delete" onclick="return confirm('Are you sure you want to delete this PO?');" />
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:otherwise>
</c:choose>
</body>
</html>