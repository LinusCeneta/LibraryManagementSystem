<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>
<head>
    <title>Inventory Adjustments</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <h1>Inventory Adjustments</h1>
        <p><a href="${pageContext.request.contextPath}/inventory/adjustments/new">Create New Adjustment</a></p>

        <c:if test="${not empty message}">
            <p class="message">${message}</p>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <p class="error-message">${errorMessage}</p>
        </c:if>

        <table border="1">
            <thead>
                <tr>
                    <th>Adjustment ID</th>
                    <th>Copy ID</th> <%-- TODO: Display Book Title & Copy Barcode --%>
                    <th>Adjustment Date</th>
                    <th>Reason</th>
                    <th>Quantity Change</th>
                    <th>Adjusted By (ID)</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="adj" items="${adjustments}">
                    <tr>
                        <td>${adj.adjustmentID}</td>
                        <td>${adj.copyID}</td>
                        <td><fmt:formatDate value="${adj.adjustmentDate}" pattern="yyyy-MM-dd"/></td>
                        <td><c:out value="${adj.reason}"/></td>
                        <td>${adj.quantityChange > 0 ? '+' : ''}${adj.quantityChange}</td>
                        <td>${adj.adjustedBy}</td>
                        <td>
                            <a href="${pageContext.request.contextPath}/inventory/adjustments/view?id=${adj.adjustmentID}">View Details</a>
                            <%-- Adjustments are usually not editable/deletable to maintain audit trail. Counter-adjustments are preferred. --%>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty adjustments}">
                    <tr>
                        <td colspan="7">No inventory adjustments found.</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
        <p><a href="${pageContext.request.contextPath}/index.jsp">Back to Home</a></p>
    </div>
</body>
</html>
