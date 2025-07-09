<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Edit Supplier</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <h1>Edit Supplier: <c:out value="${supplier.supplierName}"/></h1>

        <c:if test="${not empty errorMessage}">
            <p class="error-message">${errorMessage}</p>
        </c:if>
        <c:if test="${supplier == null}">
            <p class="error-message">Supplier not found or could not be loaded.</p>
            <p><a href="${pageContext.request.contextPath}/acquisition/suppliers/list">Back to Suppliers List</a></p>
            <%-- Stop further processing of the page if supplier is null --%>
            <c:redirect url="/acquisition/suppliers/list"/>
        </c:if>

        <form action="${pageContext.request.contextPath}/acquisition/suppliers/edit" method="post">
            <%-- Add CSRF token here if implemented --%>
            <input type="hidden" name="supplierID" value="${supplier.supplierID}">
            <p>
                <label for="supplierName">Supplier Name:</label><br>
                <input type="text" id="supplierName" name="supplierName" value="<c:out value="${supplier.supplierName}"/>" required>
            </p>
            <p>
                <label for="contactPerson">Contact Person:</label><br>
                <input type="text" id="contactPerson" name="contactPerson" value="<c:out value="${supplier.contactPerson}"/>">
            </p>
            <p>
                <label for="address">Address:</label><br>
                <textarea id="address" name="address" rows="3"><c:out value="${supplier.address}"/></textarea>
            </p>
            <p>
                <label for="phoneNumber">Phone Number:</label><br>
                <input type="tel" id="phoneNumber" name="phoneNumber" value="<c:out value="${supplier.phoneNumber}"/>">
            </p>
            <p>
                <label for="email">Email:</label><br>
                <input type="email" id="email" name="email" value="<c:out value="${supplier.email}"/>">
            </p>
            <p>
                <label for="paymentTerms">Payment Terms:</label><br>
                <input type="text" id="paymentTerms" name="paymentTerms" value="<c:out value="${supplier.paymentTerms}"/>">
            </p>
            <p>
                <button type="submit">Update Supplier</button>
                <a href="${pageContext.request.contextPath}/acquisition/suppliers/list">Cancel</a>
            </p>
        </form>
    </div>
</body>
</html>
