<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>
<head>
    <title>New Purchase Order</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .item-line { border: 1px solid #ccc; padding: 10px; margin-bottom: 10px; }
        .item-line label { display: inline-block; width: 120px; }
    </style>
</head>
<body>
    <div class="container">
        <h1>Create New Purchase Order</h1>

        <c:if test="${not empty errorMessage}">
            <p class="error-message"><c:out value="${errorMessage}"/></p>
        </c:if>
        <c:if test="${sessionScope.user == null}">
             <p class="error-message">You must be logged in to create a Purchase Order. <a href="${pageContext.request.contextPath}/user/login">Login here</a>.</p>
        </c:if>

        <form action="${pageContext.request.contextPath}/acquisition/po/new" method="post" id="poForm">
            <%-- CSRF token if implemented --%>
            <c:if test="${not empty sourceRequest}">
                <input type="hidden" name="sourceRequestId" value="${sourceRequest.requestID}">
                <p><em>Creating Purchase Order based on Acquisition Request ID: ${sourceRequest.requestID} - <c:out value="${sourceRequest.bookTitle}"/></em></p>
            </c:if>

            <fieldset>
                <legend>Purchase Order Header</legend>
                <p>
                    <label for="poNumber">PO Number:</label>
                    <input type="text" id="poNumber" name="poNumber" value="<c:out value="${suggestedPONumber}"/>" required>
                </p>
                <p>
                    <label for="supplierID">Supplier:</label>
                    <select id="supplierID" name="supplierID" required>
                        <option value="">-- Select Supplier --</option>
                        <c:forEach var="supplier" items="${suppliers}">
                            <option value="${supplier.supplierID}" ${not empty sourceRequest && sourceRequest.publisher != null && fn:containsIgnoreCase(supplier.supplierName, sourceRequest.publisher) ? 'selected' : ''}>
                                <c:out value="${supplier.supplierName}"/>
                            </option>
                        </c:forEach>
                    </select>
                </p>
                <p>
                    <label for="orderDate">Order Date:</label>
                    <input type="date" id="orderDate" name="orderDate" value="<fmt:formatDate value="<%=new java.util.Date()%>" pattern="yyyy-MM-dd"/>" required>
                </p>
                <p>
                    <label for="expectedDeliveryDate">Expected Delivery Date (optional):</label>
                    <input type="date" id="expectedDeliveryDate" name="expectedDeliveryDate">
                </p>
            </fieldset>

            <fieldset>
                <legend>Purchase Order Lines</legend>
                <div id="poLinesContainer">
                    <%-- Initial line, potentially pre-filled if from an Acquisition Request --%>
                    <div class="item-line" id="line_0">
                        <label for="line_bookTitle_0">Book Title:</label>
                        <input type="text" name="line_bookTitle" id="line_bookTitle_0" value="<c:out value="${sourceRequest.bookTitle}"/>" required size="40">

                        <label for="line_bookId_0">Book ID (if known):</label>
                        <input type="number" name="line_bookId" id="line_bookId_0" min="1" style="width: 80px;">
                        <br>
                        <label for="line_quantity_0">Quantity:</label>
                        <input type="number" name="line_quantity" id="line_quantity_0" value="1" min="1" required style="width: 80px;">

                        <label for="line_unitPrice_0">Unit Price:</label>
                        <input type="number" name="line_unitPrice" id="line_unitPrice_0" step="0.01" min="0.01" required style="width: 100px;">
                        <button type="button" onclick="removeLine(this)" class="remove-line-btn" style="display:none;">Remove</button>
                    </div>
                </div>
                <button type="button" id="addLineBtn">Add Another Item</button>
            </fieldset>

            <p style="margin-top: 20px;">
                <button type="submit" <c:if test="${sessionScope.user == null}">disabled</c:if>>Create Purchase Order</button>
                <a href="${pageContext.request.contextPath}/acquisition/po/list">Cancel</a>
            </p>
        </form>
    </div>

<script>
    document.addEventListener('DOMContentLoaded', function () {
        const container = document.getElementById('poLinesContainer');
        const addLineBtn = document.getElementById('addLineBtn');
        let lineIndex = container.children.length; // Start index based on existing lines

        // Show remove button for all but the first line initially
        updateRemoveButtons();

        addLineBtn.addEventListener('click', function () {
            const newLine = document.createElement('div');
            newLine.classList.add('item-line');
            newLine.id = 'line_' + lineIndex;
            newLine.innerHTML = `
                <label for="line_bookTitle_${lineIndex}">Book Title:</label>
                <input type="text" name="line_bookTitle" id="line_bookTitle_${lineIndex}" required size="40">

                <label for="line_bookId_${lineIndex}">Book ID (if known):</label>
                <input type="number" name="line_bookId" id="line_bookId_${lineIndex}" min="1" style="width: 80px;">
                <br>
                <label for="line_quantity_${lineIndex}">Quantity:</label>
                <input type="number" name="line_quantity" id="line_quantity_${lineIndex}" value="1" min="1" required style="width: 80px;">

                <label for="line_unitPrice_${lineIndex}">Unit Price:</label>
                <input type="number" name="line_unitPrice" id="line_unitPrice_${lineIndex}" step="0.01" min="0.01" required style="width: 100px;">
                <button type="button" onclick="removeLine(this)" class="remove-line-btn">Remove</button>
            `;
            container.appendChild(newLine);
            lineIndex++;
            updateRemoveButtons();
        });
    });

    function removeLine(button) {
        const lineToRemove = button.parentElement;
        lineToRemove.remove();
        updateRemoveButtons();
    }

    function updateRemoveButtons() {
        const container = document.getElementById('poLinesContainer');
        const lines = container.getElementsByClassName('item-line');
        for (let i = 0; i < lines.length; i++) {
            const removeBtn = lines[i].querySelector('.remove-line-btn');
            if (removeBtn) {
                removeBtn.style.display = (lines.length > 1) ? 'inline-block' : 'none';
            }
        }
    }
</script>

</body>
</html>
