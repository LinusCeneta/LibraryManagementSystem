<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String role = (String) session.getAttribute("role");
    if (role == null || (!role.equals("STAFF") && !role.equals("ADMIN"))) {
        response.sendRedirect("login.jsp");
        return;
    }
%>

<!DOCTYPE html>
<html>
<head>
    <title>Receive Purchase Order</title>
</head>
<body>

<h2>Receiving Items</h2>

<form action="ReceiveServlet" method="post">
    <label for="poId">Select Purchase Order:</label>
    <select name="poId" id="poId" required>
        <option value="">-- Select PO --</option>
        <c:forEach var="po" items="${purchaseOrders}">
            <option value="${po.id}">PO#${po.id} - ${po.supplierName}</option>
        </c:forEach>
    </select>

    <div id="poLines">
        <c:if test="${not empty poLines}">
            <h3>Items in PO</h3>
            <table>
                <tr>
                    <th>Book Title</th>
                    <th>Ordered Quantity</th>
                    <th>Received Quantity</th>
                </tr>
                <c:forEach var="line" items="${poLines}">
                    <tr>
                        <td>${line.bookTitle}</td>
                        <td>${line.quantityOrdered}</td>
                        <td>
                            <input type="number" name="receivedQty_${line.lineId}" min="0"
                                   max="${line.quantityOrdered - line.quantityReceived}" required />
                        </td>
                    </tr>
                </c:forEach>
            </table>
            <br>
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />
            <button type="submit" class="btn">Submit Receiving</button>
        </c:if>
    </div>
</form>

<c:if test="${not empty message}">
    <p style="color: green;">${message}</p>
</c:if>
<c:if test="${not empty error}">
    <p style="color: red;">${error}</p>
</c:if>

</body>
</html>
