<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>
<head>
    <title>View Acquisition Request - ID: ${request.requestID}</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <h1>Acquisition Request Details</h1>

        <c:if test="${request == null}">
            <p class="error-message">Acquisition Request not found.</p>
            <p><a href="${pageContext.request.contextPath}/acquisition/requests/list">Back to Requests List</a></p>
            <c:redirect url="/acquisition/requests/list"/>
        </c:if>

        <c:if test="${not empty message}">
            <p class="message">${message}</p>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <p class="error-message">${errorMessage}</p>
        </c:if>

        <table class="details-table">
            <tr>
                <th>Request ID:</th>
                <td>${request.requestID}</td>
            </tr>
            <tr>
                <th>Book Title:</th>
                <td><c:out value="${request.bookTitle}"/></td>
            </tr>
            <tr>
                <th>Author:</th>
                <td><c:out value="${request.author}"/></td>
            </tr>
            <tr>
                <th>ISBN:</th>
                <td><c:out value="${request.isbn}"/></td>
            </tr>
            <tr>
                <th>Publisher:</th>
                <td><c:out value="${request.publisher}"/></td>
            </tr>
            <tr>
                <th>Publication Year:</th>
                <td><c:out value="${request.publicationYear}"/></td>
            </tr>
            <tr>
                <th>Requested By (User ID):</th>
                <td>${request.requestedBy}</td> <%-- TODO: Display username if possible --%>
            </tr>
            <tr>
                <th>Request Date:</th>
                <td><fmt:formatDate value="${request.requestDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
            </tr>
            <tr>
                <th>Status:</th>
                <td><c:out value="${request.status}"/></td>
            </tr>
            <tr>
                <th>Notes:</th>
                <td><c:out value="${request.notes}" escapeXml="false"/></td> <%-- Consider if HTML in notes is allowed/needed --%>
            </tr>
        </table>

        <h2>Actions</h2>
        <c:if test="${request.status == 'Pending'}">
            <form action="${pageContext.request.contextPath}/acquisition/requests/updateStatus" method="post" style="display:inline-block; margin-right: 10px;">
                <input type="hidden" name="requestId" value="${request.requestID}">
                <input type="hidden" name="status" value="Approved">
                <button type="submit">Approve Request</button>
            </form>
            <form action="${pageContext.request.contextPath}/acquisition/requests/updateStatus" method="post" style="display:inline-block; margin-right: 10px;">
                <input type="hidden" name="requestId" value="${request.requestID}">
                <input type="hidden" name="status" value="Rejected">
                <button type="submit">Reject Request</button>
            </form>
        </c:if>
        <c:if test="${request.status == 'Approved'}">
            <a href="${pageContext.request.contextPath}/acquisition/po/new?requestId=${request.requestID}" class="button">Create Purchase Order</a>
        </c:if>
        <br><br>
        <form action="${pageContext.request.contextPath}/acquisition/requests/delete?id=${request.requestID}" method="post" style="display:inline-block;" onsubmit="return confirm('Are you sure you want to delete this request? This action cannot be undone.');">
            <input type="hidden" name="id" value="${request.requestID}">
            <button type="submit" class="button-danger">Delete Request</button>
        </form>

        <p style="margin-top: 20px;">
            <a href="${pageContext.request.contextPath}/acquisition/requests/list">Back to Requests List</a>
        </p>
    </div>
</body>
</html>
