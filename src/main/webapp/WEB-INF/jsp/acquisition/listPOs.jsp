<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>
<head>
    <title>Purchase Orders</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <h1>Purchase Orders</h1>
        <p><a href="${pageContext.request.contextPath}/acquisition/po/new">Create New Purchase Order</a></p>

        <c:if test="${not empty message}">
            <p class="message">${message}</p>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <p class="error-message">${errorMessage}</p>
        </c:if>

        <table border="1">
            <thead>
                <tr>
                    <th>PO ID</th>
                    <th>PO Number</th>
                    <th>Supplier ID</th> <%-- TODO: Display Supplier Name --%>
                    <th>Order Date</th>
                    <th>Expected Delivery</th>
                    <th>Total Amount</th>
                    <th>Status</th>
                    <th>Created By (ID)</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="po" items="${purchaseOrders}">
                    <tr>
                        <td>${po.poID}</td>
                        <td><c:out value="${po.poNumber}"/></td>
                        <td>${po.supplierID}</td>
                        <td><fmt:formatDate value="${po.orderDate}" pattern="yyyy-MM-dd"/></td>
                        <td><fmt:formatDate value="${po.expectedDeliveryDate}" pattern="yyyy-MM-dd"/></td>
                        <td><fmt:formatNumber value="${po.totalAmount}" type="currency"/></td>
                        <td><c:out value="${po.status}"/></td>
                        <td>${po.createdBy}</td>
                        <td>
                            <a href="${pageContext.request.contextPath}/acquisition/po/view?id=${po.poID}">View/Details</a>
                            <c:if test="${po.status != 'Closed' && po.status != 'Fully Received' && po.status != 'Cancelled'}">
                                <a href="${pageContext.request.contextPath}/acquisition/po/receive?poId=${po.poID}">Receive Items</a>
                            </c:if>
                             <%-- More actions like 'Edit PO' (if status allows), 'Cancel PO' --%>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty purchaseOrders}">
                    <tr>
                        <td colspan="9">No purchase orders found.</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
        <p><a href="${pageContext.request.contextPath}/index.jsp">Back to Home</a></p>
    </div>
</body>
</html>
