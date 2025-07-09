<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>
<head>
    <title>Goods Receipt Notes (GRNs)</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <h1>Goods Receipt Notes (GRNs)</h1>
        <%-- Typically GRNs are created from a PO, so no direct "Add New GRN" link here --%>
        <%-- <p><a href="${pageContext.request.contextPath}/acquisition/grn/new">Add New GRN (Manual)</a></p> --%>

        <c:if test="${not empty message}">
            <p class="message">${message}</p>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <p class="error-message">${errorMessage}</p>
        </c:if>

        <table border="1">
            <thead>
                <tr>
                    <th>GRN ID</th>
                    <th>PO ID</th> <%-- TODO: Display PO Number --%>
                    <th>Supplier ID</th> <%-- TODO: Display Supplier Name --%>
                    <th>Invoice Number</th>
                    <th>Received Date</th>
                    <th>Received By (ID)</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="grn" items="${goodsReceiptNotes}">
                    <tr>
                        <td>${grn.grnID}</td>
                        <td>
                            <a href="${pageContext.request.contextPath}/acquisition/po/view?id=${grn.poID}">${grn.poID}</a>
                        </td>
                        <td>${grn.supplierID}</td>
                        <td><c:out value="${grn.invoiceNumber}"/></td>
                        <td><fmt:formatDate value="${grn.receivedDate}" pattern="yyyy-MM-dd"/></td>
                        <td>${grn.receivedBy}</td>
                        <td>
                            <a href="${pageContext.request.contextPath}/acquisition/grn/view?id=${grn.grnID}">View Details</a>
                            <%-- GRNs are generally not editable/deletable once processed due to inventory impact --%>
                            <%-- Actions like "Print GRN", "Return Items" might go here later --%>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty goodsReceiptNotes}">
                    <tr>
                        <td colspan="7">No Goods Receipt Notes found.</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
        <p><a href="${pageContext.request.contextPath}/index.jsp">Back to Home</a></p>
        <p><a href="${pageContext.request.contextPath}/acquisition/po/list">Back to Purchase Orders</a></p>
    </div>
</body>
</html>
