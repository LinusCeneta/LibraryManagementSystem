<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>
<head>
    <title>View Goods Receipt Note - ID: ${grn.grnID}</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .section { margin-bottom: 20px; padding: 15px; border: 1px solid #eee; border-radius: 5px; }
        .section h2 { margin-top: 0; }
    </style>
</head>
<body>
    <div class="container">
        <h1>Goods Receipt Note Details - ID: ${grn.grnID}</h1>

        <c:if test="${grn == null}">
            <p class="error-message">Goods Receipt Note not found.</p>
            <p><a href="${pageContext.request.contextPath}/acquisition/grn/list">Back to GRN List</a></p>
            <c:redirect url="/acquisition/grn/list"/>
        </c:if>

        <c:if test="${not empty message}">
            <p class="message"><c:out value="${message}"/></p>
        </c:if>

        <div class="section">
            <h2>GRN Header Information</h2>
            <table class="details-table">
                <tr><th>GRN ID:</th><td>${grn.grnID}</td></tr>
                <tr>
                    <th>Purchase Order:</th>
                    <td>
                        <a href="${pageContext.request.contextPath}/acquisition/po/view?id=${grn.poID}">
                            PO ID: ${grn.poID}
                            <c:if test="${not empty purchaseOrder}">
                                (Number: <c:out value="${purchaseOrder.poNumber}"/>)
                            </c:if>
                        </a>
                    </td>
                </tr>
                <tr>
                    <th>Supplier:</th>
                    <td>
                        <c:if test="${not empty supplier}">
                            <c:out value="${supplier.supplierName}"/> (ID: ${grn.supplierID})
                        </c:if>
                        <c:if test="${empty supplier}">
                            Supplier ID: ${grn.supplierID}
                        </c:if>
                    </td>
                </tr>
                <tr><th>Invoice Number:</th><td><c:out value="${grn.invoiceNumber}"/></td></tr>
                <tr><th>Invoice Date:</th><td><fmt:formatDate value="${grn.invoiceDate}" pattern="yyyy-MM-dd"/></td></tr>
                <tr><th>Received Date:</th><td><fmt:formatDate value="${grn.receivedDate}" pattern="yyyy-MM-dd"/></td></tr>
                <tr><th>Received By (User ID):</th><td>${grn.receivedBy}</td></tr> <%-- TODO: Display username --%>
                <tr><th>GRN Notes:</th><td><c:out value="${grn.notes}" escapeXml="false"/></td></tr>
            </table>
        </div>

        <div class="section">
            <h2>Received Items</h2>
            <table border="1" style="width:100%;">
                <thead>
                    <tr>
                        <th>GRN Item ID</th>
                        <th>PO Line ID</th>
                        <th>Book ID (Catalog)</th> <%-- TODO: Display Book Title --%>
                        <th>Received Qty</th>
                        <th>Accepted Qty</th>
                        <th>Condition</th>
                        <th>Item Notes</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="item" items="${grn.items}">
                        <tr>
                            <td>${item.grnItemID}</td>
                            <td>${not empty item.poLineID ? item.poLineID : 'N/A'}</td>
                            <td>${item.bookID}</td>
                            <td>${item.receivedQuantity}</td>
                            <td>${item.acceptedQuantity}</td>
                            <td><c:out value="${item.condition}"/></td>
                            <td><c:out value="${item.notes}"/></td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty grn.items}">
                        <tr><td colspan="7">No items found for this Goods Receipt Note.</td></tr>
                    </c:if>
                </tbody>
            </table>
        </div>

        <div class="section">
            <h2>Actions</h2>
            <%-- GRNs are typically not directly editable once copies are processed into inventory. --%>
            <%-- Actions might include "Print GRN", "Report Discrepancy", "Return to Supplier" (which would trigger other processes) --%>
            <p><em>Goods Receipt Notes are generally read-only after creation due to inventory impact. For corrections, a new transaction (e.g., Return to Vendor, Inventory Adjustment) is usually required.</em></p>
            <button onclick="window.print();">Print GRN</button>
        </div>

        <p style="margin-top: 20px;">
            <a href="${pageContext.request.contextPath}/acquisition/grn/list">Back to GRN List</a><br>
            <a href="${pageContext.request.contextPath}/acquisition/po/view?id=${grn.poID}">View Associated Purchase Order</a>
        </p>
    </div>
</body>
</html>
