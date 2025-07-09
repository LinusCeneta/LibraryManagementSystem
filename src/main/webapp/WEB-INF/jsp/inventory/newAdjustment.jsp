<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>
<head>
    <title>New Inventory Adjustment</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <h1>Create New Inventory Adjustment</h1>

        <c:if test="${not empty errorMessage}">
            <p class="error-message"><c:out value="${errorMessage}"/></p>
        </c:if>
         <c:if test="${sessionScope.user == null}">
             <p class="error-message">You must be logged in to make an inventory adjustment. <a href="${pageContext.request.contextPath}/user/login">Login here</a>.</p>
        </c:if>

        <form action="${pageContext.request.contextPath}/inventory/adjustments/new" method="post">
            <%-- CSRF token if implemented --%>
            <p>
                <label for="copyID">Copy ID or Barcode:</label><br>
                <%-- TODO: Implement a search/lookup for Copy ID by barcode or title --%>
                <input type="number" id="copyID" name="copyID" value="<c:out value="${selectedCopyId != null ? selectedCopyId : param.copyID}"/>" required>
                <%-- <button type="button" id="lookupCopyBtn">Lookup Copy</button> --%>
                <br><small>Enter the unique ID of the library item copy.</small>
            </p>
            <%-- Placeholder to display details of the looked-up copy --%>
            <%-- <div id="copyDetails">
                <c:if test="${not empty copy}">
                    Selected Copy: <c:out value="${copy.copyBarcode}"/> - Title: <c:out value="${book.title}"/> (Current Status: <c:out value="${copy.status}"/>)
                </c:if>
            </div> --%>

            <p>
                <label for="adjustmentDate">Adjustment Date:</label><br>
                <input type="date" id="adjustmentDate" name="adjustmentDate" value="<fmt:formatDate value="<%=new java.util.Date()%>" pattern="yyyy-MM-dd"/>" required>
            </p>
            <p>
                <label for="reason">Reason for Adjustment:</label><br>
                <select id="reason" name="reason" required>
                    <option value="">-- Select Reason --</option>
                    <option value="Lost" ${param.reason == 'Lost' ? 'selected' : ''}>Item Lost</option>
                    <option value="Damaged" ${param.reason == 'Damaged' ? 'selected' : ''}>Item Damaged (Beyond Repair/Use)</option>
                    <option value="Found" ${param.reason == 'Found' ? 'selected' : ''}>Item Found (Previously Lost/Missing)</option>
                    <option value="Transfer Out" ${param.reason == 'Transfer Out' ? 'selected' : ''}>Transfer to Another Branch/System</option>
                    <option value="Transfer In" ${param.reason == 'Transfer In' ? 'selected' : ''}>Transfer from Another Branch/System</option>
                    <option value="Withdrawn" ${param.reason == 'Withdrawn' ? 'selected' : ''}>Withdrawn from Collection (Old/Obsolete)</option>
                    <option value="Donation" ${param.reason == 'Donation' ? 'selected' : ''}>Item Donated (Added to Collection)</option>
                    <option value="Initial Stock" ${param.reason == 'Initial Stock' ? 'selected' : ''}>Initial Stock Entry</option>
                    <option value="Cycle Count Adjustment" ${param.reason == 'Cycle Count Adjustment' ? 'selected' : ''}>Cycle Count Adjustment</option>
                    <option value="Other" ${param.reason == 'Other' ? 'selected' : ''}>Other (Specify in Notes)</option>
                </select>
            </p>
            <p>
                <label for="quantityChange">Quantity Change:</label><br>
                <input type="number" id="quantityChange" name="quantityChange" value="${param.quantityChange}" required>
                <br><small>Enter a positive number to increase stock (e.g., 1 for 'Found'), or a negative number to decrease stock (e.g., -1 for 'Lost'). For items tracked individually, this is usually 1 or -1.</small>
            </p>
            <p>
                <label for="notes">Notes (optional, required if reason is 'Other'):</label><br>
                <textarea id="notes" name="notes" rows="4" cols="50">${param.notes}</textarea>
            </p>

            <%-- AdjustedBy will be set by the servlet based on logged-in user --%>

            <p>
                <button type="submit" <c:if test="${sessionScope.user == null}">disabled</c:if>>Record Adjustment</button>
                <a href="${pageContext.request.contextPath}/inventory/adjustments/list">Cancel</a>
            </p>
        </form>
    </div>
    <%-- Script for copy lookup can be added here later --%>
</body>
</html>
