<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Add New Supplier</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <h1>Add New Supplier</h1>

        <c:if test="${not empty errorMessage}">
            <p class="error-message">${errorMessage}</p>
        </c:if>

        <form action="${pageContext.request.contextPath}/acquisition/suppliers/add" method="post">
            <%-- Add CSRF token here if implemented --%>
            <p>
                <label for="supplierName">Supplier Name:</label><br>
                <input type="text" id="supplierName" name="supplierName" required>
            </p>
            <p>
                <label for="contactPerson">Contact Person:</label><br>
                <input type="text" id="contactPerson" name="contactPerson">
            </p>
            <p>
                <label for="address">Address:</label><br>
                <textarea id="address" name="address" rows="3"></textarea>
            </p>
            <p>
                <label for="phoneNumber">Phone Number:</label><br>
                <input type="tel" id="phoneNumber" name="phoneNumber">
            </p>
            <p>
                <label for="email">Email:</label><br>
                <input type="email" id="email" name="email">
            </p>
            <p>
                <label for="paymentTerms">Payment Terms:</label><br>
                <input type="text" id="paymentTerms" name="paymentTerms">
            </p>
            <p>
                <button type="submit">Save Supplier</button>
                <a href="${pageContext.request.contextPath}/acquisition/suppliers/list">Cancel</a>
            </p>
        </form>
    </div>
</body>
</html>
