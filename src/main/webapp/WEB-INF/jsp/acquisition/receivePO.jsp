<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>
<head>
    <title>Receive Items for PO: ${purchaseOrder.poNumber}</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .item-to-receive { border: 1px solid #ccc; padding: 10px; margin-bottom: 10px; background-color: #f9f9f9; }
        .item-to-receive label { display: inline-block; min-width: 130px; margin-bottom: 5px;}
        .item-to-receive input[type="number"], .item-to-receive select { width: 100px; margin-right: 10px;}
        .grn-header p label {width: 150px; display: inline-block;}
    </style>
</head>
<body>
    <div class="container">
        <h1>Receive Items for Purchase Order: <c:out value="${purchaseOrder.poNumber}"/></h1>

        <c:if test="${purchaseOrder == null}">
            <p class="error-message">Purchase Order not found for receiving.</p>
            <p><a href="${pageContext.request.contextPath}/acquisition/po/list">Back to PO List</a></p>
            <c:redirect url="/acquisition/po/list"/>
        </c:if>

        <c:if test="${not empty message}">
            <p class="message"><c:out value="${message}"/></p>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <p class="error-message"><c:out value="${errorMessage}"/></p>
        </c:if>
         <c:if test="${sessionScope.user == null}">
             <p class="error-message">You must be logged in to receive items. <a href="${pageContext.request.contextPath}/user/login">Login here</a>.</p>
        </c:if>


        <p><strong>Supplier:</strong> <c:out value="${supplier.supplierName}"/></p>
        <p><strong>Order Date:</strong> <fmt:formatDate value="${purchaseOrder.orderDate}" pattern="yyyy-MM-dd"/></p>
        <p><strong>Status:</strong> <c:out value="${purchaseOrder.status}"/></p>

        <c:if test="${purchaseOrder.status == 'Closed' || purchaseOrder.status == 'Fully Received'}">
            <p class="message">This Purchase Order is already <c:out value="${purchaseOrder.status}"/>. No further items can be received unless it's reopened.</p>
             <p><a href="${pageContext.request.contextPath}/acquisition/po/view?id=${purchaseOrder.poID}">Back to PO Details</a></p>
        </c:if>

        <c:if test="${purchaseOrder.status != 'Closed' && purchaseOrder.status != 'Fully Received'}">
            <form action="${pageContext.request.contextPath}/acquisition/po/receive" method="post">
                <input type="hidden" name="_csrf" value="${_csrf}">
                <input type="hidden" name="poId" value="${purchaseOrder.poID}">

                <fieldset class="grn-header">
                    <legend>Goods Receipt Note (GRN) Details</legend>
                    <p>
                        <label for="invoiceNumber">Supplier Invoice Number:</label>
                        <input type="text" id="invoiceNumber" name="invoiceNumber" value="${param.invoiceNumber}">
                    </p>
                    <p>
                        <label for="invoiceDate">Invoice Date:</label>
                        <input type="date" id="invoiceDate" name="invoiceDate" value="${param.invoiceDate}">
                    </p>
                    <p>
                        <label for="receivedDate">Received Date:</label>
                        <input type="date" id="receivedDate" name="receivedDate" value="<fmt:formatDate value="<%=new java.util.Date()%>" pattern="yyyy-MM-dd"/>" required>
                    </p>
                    <p>
                        <label for="grnNotes">GRN Notes (optional):</label>
                        <textarea id="grnNotes" name="grnNotes" rows="3" cols="50">${param.grnNotes}</textarea>
                    </p>
                </fieldset>

                <h2>Items to Receive</h2>
                <c:if test="${empty purchaseOrder.orderLines}">
                    <p>This Purchase Order has no lines defined.</p>
                </c:if>

                <c:forEach var="line" items="${purchaseOrder.orderLines}" varStatus="loop">
                    <div class="item-to-receive">
                        <h4>Item: <c:out value="${line.requestedBookTitle}"/></h4>
                        <input type="hidden" name="item_poLineId" value="${line.poLineID}">
                        <input type="hidden" name="item_bookId" value="${line.bookID != null ? line.bookID : 0}"> <%-- BookID must be resolved or handled if 0 --%>

                        <p>
                            PO Line ID: ${line.poLineID} |
                            Book ID (Catalog): ${not empty line.bookID ? line.bookID : 'N/A'} <br>
                            Ordered Quantity: ${line.quantity}
                            <%-- TODO: Show already received quantity for this line from previous GRNs --%>
                        </p>

                        <p>
                            <label for="receivedQuantity_${loop.index}">Received Quantity:</label>
                            <input type="number" id="receivedQuantity_${loop.index}" name="item_receivedQuantity" value="${paramValues.item_receivedQuantity[loop.index] != null ? paramValues.item_receivedQuantity[loop.index] : 0}" min="0" max="${line.quantity}"> <%-- Max should be remaining to receive --%>
                        </p>
                        <p>
                            <label for="acceptedQuantity_${loop.index}">Accepted Quantity:</label>
                            <input type="number" id="acceptedQuantity_${loop.index}" name="item_acceptedQuantity" value="${paramValues.item_acceptedQuantity[loop.index] != null ? paramValues.item_acceptedQuantity[loop.index] : 0}" min="0"> <%-- Max should be received quantity --%>
                        </p>
                        <p>
                            <label for="condition_${loop.index}">Condition:</label>
                            <select id="condition_${loop.index}" name="item_condition">
                                <option value="New" ${paramValues.item_condition[loop.index] == 'New' ? 'selected' : ''}>New</option>
                                <option value="Good" ${paramValues.item_condition[loop.index] == 'Good' ? 'selected' : ''}>Good</option>
                                <option value="Fair" ${paramValues.item_condition[loop.index] == 'Fair' ? 'selected' : ''}>Fair</option>
                                <option value="Damaged" ${paramValues.item_condition[loop.index] == 'Damaged' ? 'selected' : ''}>Damaged</option>
                            </select>
                        </p>
                        <p>
                            <label for="itemNotes_${loop.index}">Item Notes (e.g., damage details):</label>
                            <input type="text" id="itemNotes_${loop.index}" name="item_notes" value="${paramValues.item_notes[loop.index]}" size="40">
                        </p>
                    </div>
                </c:forEach>

                <c:if test="${not empty purchaseOrder.orderLines}">
                <p style="margin-top: 20px;">
                    <button type="submit" <c:if test="${sessionScope.user == null}">disabled</c:if>>Record Received Items</button>
                </p>
                </c:if>
            </form>
        </c:if> <%-- End check for PO status --%>

        <p style="margin-top: 30px;">
            <a href="${pageContext.request.contextPath}/acquisition/po/view?id=${purchaseOrder.poID}">Cancel / Back to PO Details</a>
        </p>
    </div>
<script>
    // Basic client-side validation to ensure accepted <= received
    document.addEventListener('DOMContentLoaded', function() {
        const form = document.querySelector('form[action$="/po/receive"]');
        if (form) {
            form.addEventListener('submit', function(event) {
                const items = document.querySelectorAll('.item-to-receive');
                let valid = true;
                items.forEach(function(item, index) {
                    const receivedQtyInput = item.querySelector('input[name="item_receivedQuantity"]');
                    const acceptedQtyInput = item.querySelector('input[name="item_acceptedQuantity"]');

                    if (receivedQtyInput && acceptedQtyInput) {
                        const received = parseInt(receivedQtyInput.value, 10);
                        const accepted = parseInt(acceptedQtyInput.value, 10);
                        const bookTitle = item.querySelector('h4').textContent;

                        if (isNaN(received) || received < 0) {
                             alert('Please enter a valid non-negative Received Quantity for ' + bookTitle);
                             event.preventDefault();
                             valid = false;
                             return;
                        }
                         if (isNaN(accepted) || accepted < 0) {
                             alert('Please enter a valid non-negative Accepted Quantity for ' + bookTitle);
                             event.preventDefault();
                             valid = false;
                             return;
                        }

                        if (accepted > received) {
                            alert('Accepted Quantity (' + accepted + ') cannot be greater than Received Quantity (' + received + ') for ' + bookTitle);
                            event.preventDefault(); // Stop form submission
                            valid = false;
                        }
                    }
                });
                return valid;
            });
        }
    });
</script>
</body>
</html>
