<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>
<head>
    <title>View Purchase Order - ${purchaseOrder.poNumber}</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .section { margin-bottom: 20px; padding: 15px; border: 1px solid #eee; border-radius: 5px; }
        .section h2 { margin-top: 0; }
    </style>
</head>
<body>
    <div class="container">
        <h1>Purchase Order Details: <c:out value="${purchaseOrder.poNumber}"/></h1>

        <c:if test="${purchaseOrder == null}">
            <p class="error-message">Purchase Order not found.</p>
            <p><a href="${pageContext.request.contextPath}/acquisition/po/list">Back to PO List</a></p>
            <c:redirect url="/acquisition/po/list"/>
        </c:if>

        <c:if test="${not empty message}">
            <p class="message"><c:out value="${message}"/></p>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <p class="error-message"><c:out value="${errorMessage}"/></p>
        </c:if>

        <div class="section">
            <h2>Header Information</h2>
            <table class="details-table">
                <tr><th>PO ID:</th><td>${purchaseOrder.poID}</td></tr>
                <tr><th>PO Number:</th><td><c:out value="${purchaseOrder.poNumber}"/></td></tr>
                <tr><th>Supplier:</th><td><c:out value="${supplier.supplierName}"/> (ID: ${purchaseOrder.supplierID})</td></tr>
                <tr><th>Order Date:</th><td><fmt:formatDate value="${purchaseOrder.orderDate}" pattern="yyyy-MM-dd"/></td></tr>
                <tr><th>Expected Delivery:</th><td><fmt:formatDate value="${purchaseOrder.expectedDeliveryDate}" pattern="yyyy-MM-dd"/></td></tr>
                <tr><th>Total Amount:</th><td><fmt:formatNumber value="${purchaseOrder.totalAmount}" type="currency"/></td></tr>
                <tr><th>Status:</th><td><c:out value="${purchaseOrder.status}"/></td></tr>
                <tr><th>Created By (User ID):</th><td>${purchaseOrder.createdBy}</td></tr> <%-- TODO: Display username --%>
            </table>
        </div>

        <div class="section">
            <h2>Order Lines</h2>
            <table border="1" style="width:100%;">
                <thead>
                    <tr>
                        <th>Line ID</th>
                        <th>Book Title</th>
                        <th>Book ID (Catalog)</th>
                        <th>Quantity</th>
                        <th>Unit Price</th>
                        <th>Line Total</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="line" items="${purchaseOrder.orderLines}">
                        <tr>
                            <td>${line.poLineID}</td>
                            <td><c:out value="${line.requestedBookTitle}"/></td>
                            <td>${not empty line.bookID ? line.bookID : 'N/A'}</td>
                            <td>${line.quantity}</td>
                            <td><fmt:formatNumber value="${line.unitPrice}" type="currency"/></td>
                            <td><fmt:formatNumber value="${line.lineTotal}" type="currency"/></td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty purchaseOrder.orderLines}">
                        <tr><td colspan="6">No lines found for this purchase order.</td></tr>
                    </c:if>
                </tbody>
            </table>
        </div>

        <div class="section">
            <h2>Goods Receipt Notes (GRNs)</h2>
            <c:if test="${not empty grns}">
                <table border="1" style="width:100%;">
                    <thead>
                        <tr>
                            <th>GRN ID</th>
                            <th>Invoice Number</th>
                            <th>Received Date</th>
                            <th>Received By (ID)</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="grn" items="${grns}">
                            <tr>
                                <td>${grn.grnID}</td>
                                <td><c:out value="${grn.invoiceNumber}"/></td>
                                <td><fmt:formatDate value="${grn.receivedDate}" pattern="yyyy-MM-dd"/></td>
                                <td>${grn.receivedBy}</td>
                                <td><a href="${pageContext.request.contextPath}/acquisition/grn/view?id=${grn.grnID}">View GRN</a></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:if>
            <c:if test="${empty grns}">
                <p>No Goods Receipt Notes have been recorded for this Purchase Order yet.</p>
            </c:if>
        </div>


        <div class="section">
            <h2>Actions</h2>
            <c:if test="${purchaseOrder.status != 'Closed' && purchaseOrder.status != 'Fully Received' && purchaseOrder.status != 'Cancelled'}">
                <a href="${pageContext.request.contextPath}/acquisition/po/receive?poId=${purchaseOrder.poID}" class="button">Receive Items Against This PO</a>
            </c:if>

            <%-- Placeholder for other actions like updating status, cancelling PO --%>
            <c:if test="${purchaseOrder.status == 'Created' || purchaseOrder.status == 'Submitted'}">
                 <form action="${pageContext.request.contextPath}/acquisition/po/updateStatus" method="post" style="display:inline-block; margin: 5px;">
                    <input type="hidden" name="poId" value="${purchaseOrder.poID}">
                    <input type="hidden" name="status" value="Submitted">
                    <button type="submit" <c:if test="${purchaseOrder.status != 'Created'}">disabled</c:if>>Mark as Submitted</button>
                </form>
                <form action="${pageContext.request.contextPath}/acquisition/po/updateStatus" method="post" style="display:inline-block; margin: 5px;">
                    <input type="hidden" name="poId" value="${purchaseOrder.poID}">
                    <input type="hidden" name="status" value="Cancelled">
                    <button type="submit" class="button-danger" onclick="return confirm('Are you sure you want to cancel this PO?');">Cancel PO</button>
                </form>
            </c:if>
             <c:if test="${purchaseOrder.status == 'Partially Received' || purchaseOrder.status == 'Fully Received'}">
                 <form action="${pageContext.request.contextPath}/acquisition/po/updateStatus" method="post" style="display:inline-block; margin: 5px;">
                    <input type="hidden" name="poId" value="${purchaseOrder.poID}">
                    <input type="hidden" name="status" value="Closed">
                    <button type="submit" <c:if test="${purchaseOrder.status == 'Closed'}">disabled</c:if>>Close PO</button>
                </form>
            </c:if>
        </div>

        <p style="margin-top: 20px;">
            <a href="${pageContext.request.contextPath}/acquisition/po/list">Back to Purchase Orders List</a>
        </p>
    </div>
</body>
</html>
