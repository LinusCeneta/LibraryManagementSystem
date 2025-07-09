<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>
<head>
    <title>View Inventory Adjustment - ID: ${adjustment.adjustmentID}</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <h1>Inventory Adjustment Details</h1>

        <c:if test="${adjustment == null}">
            <p class="error-message">Inventory Adjustment not found.</p>
            <p><a href="${pageContext.request.contextPath}/inventory/adjustments/list">Back to Adjustments List</a></p>
            <c:redirect url="/inventory/adjustments/list"/>
        </c:if>

        <table class="details-table">
            <tr>
                <th>Adjustment ID:</th>
                <td>${adjustment.adjustmentID}</td>
            </tr>
            <tr>
                <th>Copy ID:</th>
                <td>
                    ${adjustment.copyID}
                    <%-- TODO: Display link to Copy details page and Book Title/Barcode if Copy & Book objects are available --%>
                    <%-- Example: <a href="${pageContext.request.contextPath}/catalog/copy/view?id=${adjustment.copyID}">${adjustment.copyID}</a> --%>
                    <%-- <c:if test="${not empty copy && not empty book}">
                        (<c:out value="${book.title}"/> - Barcode: <c:out value="${copy.copyBarcode}"/>)
                    </c:if> --%>
                </td>
            </tr>
            <tr>
                <th>Adjustment Date:</th>
                <td><fmt:formatDate value="${adjustment.adjustmentDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
            </tr>
            <tr>
                <th>Reason:</th>
                <td><c:out value="${adjustment.reason}"/></td>
            </tr>
            <tr>
                <th>Quantity Change:</th>
                <td>${adjustment.quantityChange > 0 ? '+' : ''}${adjustment.quantityChange}</td>
            </tr>
            <tr>
                <th>Adjusted By (User ID):</th>
                <td>${adjustment.adjustedBy}</td> <%-- TODO: Display username --%>
            </tr>
            <tr>
                <th>Notes:</th>
                <td><c:out value="${adjustment.notes}" escapeXml="false"/></td>
            </tr>
        </table>

        <p style="margin-top: 20px;">
            Inventory adjustments are typically not modified or deleted to preserve the audit trail.
            If a mistake was made, a new counter-adjustment should be created.
        </p>

        <p>
            <a href="${pageContext.request.contextPath}/inventory/adjustments/list">Back to Adjustments List</a>
        </p>
    </div>
</body>
</html>
