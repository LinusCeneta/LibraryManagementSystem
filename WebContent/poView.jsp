<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <title>View Purchase Order</title>
</head>
<body>
<h2>Purchase Order Details</h2>

<c:if test="${not empty error}">
    <div style="color: red;">${error}</div>
</c:if>

<table border="1">
    <tr>
        <th>PO ID</th>
        <td>${purchaseOrder.poId}</td>
    </tr>
    <tr>
        <th>PO Number</th>
        <td>${purchaseOrder.poNumber}</td>
    </tr>
    <tr>
        <th>Supplier ID</th>
        <td>${purchaseOrder.supplierId}</td>
    </tr>
    <tr>
        <th>Created Date</th>
        <td><fmt:formatDate value="${purchaseOrder.createdDate}" pattern="yyyy-MM-dd" /></td>
    </tr>
    <tr>
        <th>Expected Delivery</th>
        <td><fmt:formatDate value="${purchaseOrder.expectedDeliveryDate}" pattern="yyyy-MM-dd" /></td>
    </tr>
    <tr>
        <th>Status</th>
        <td>${purchaseOrder.status}</td>
    </tr>
    <tr>
        <th>Original Request ID</th>
        <td>
            <c:if test="${purchaseOrder.requestId > 0}">
                <a href="${pageContext.request.contextPath}/purchase-request/view?id=${purchaseOrder.requestId}">
                    ${purchaseOrder.requestId}
                </a>
            </c:if>
        </td>
    </tr>
</table>

<a href="${pageContext.request.contextPath}/purchase-order">Back to PO List</a>
</body>
</html>