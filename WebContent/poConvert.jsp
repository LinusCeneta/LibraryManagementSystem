<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Convert Request to PO</title>
    <style>
        .error { color: red; }
        form { margin: 20px; padding: 20px; border: 1px solid #ddd; width: 300px; }
        input, button { margin: 5px 0; width: 100%; }
    </style>
</head>
<body>
<h2>Convert Request to Purchase Order</h2>

<c:if test="${not empty error}">
    <p class="error">${error}</p>
</c:if>

<p>Request: ${request.title} (ID: ${request.requestId})</p>

<form action="${pageContext.request.contextPath}/purchase-order/convert" method="post">
    <input type="hidden" name="requestId" value="${request.requestId}" />
    
    PO Number: <input type="text" name="poNumber" required /><br/>
    Supplier ID: <input type="number" name="supplierId" required min="1" /><br/>
    Expected Delivery: <input type="date" name="expectedDeliveryDate" required /><br/>
    
    <button type="submit">Create PO</button>
</form>

<a href="${pageContext.request.contextPath}/purchase-request">Back to Requests</a>
</body>
</html>