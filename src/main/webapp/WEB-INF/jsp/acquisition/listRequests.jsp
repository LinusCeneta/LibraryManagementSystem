<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>
<head>
    <title>Acquisition Requests</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <h1>Acquisition Requests</h1>
        <p><a href="${pageContext.request.contextPath}/acquisition/requests/new">Submit New Request</a></p>

        <c:if test="${not empty message}">
            <p class="message">${message}</p>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <p class="error-message">${errorMessage}</p>
        </c:if>

        <table border="1">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Title</th>
                    <th>Author</th>
                    <th>ISBN</th>
                    <th>Requested By (ID)</th>
                    <th>Request Date</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="request" items="${requests}">
                    <tr>
                        <td>${request.requestID}</td>
                        <td><c:out value="${request.bookTitle}"/></td>
                        <td><c:out value="${request.author}"/></td>
                        <td><c:out value="${request.isbn}"/></td>
                        <td>${request.requestedBy}</td>
                        <td><fmt:formatDate value="${request.requestDate}" pattern="yyyy-MM-dd"/></td>
                        <td><c:out value="${request.status}"/></td>
                        <td>
                            <a href="${pageContext.request.contextPath}/acquisition/requests/view?id=${request.requestID}">View</a>
                            <c:if test="${request.status == 'Pending'}">
                                <%-- Form to Approve Request --%>
                                <form action="${pageContext.request.contextPath}/acquisition/requests/updateStatus" method="post" style="display:inline;">
                                    <input type="hidden" name="requestId" value="${request.requestID}">
                                    <input type="hidden" name="status" value="Approved">
                                    <button type="submit">Approve</button>
                                </form>
                                <%-- Form to Reject Request --%>
                                <form action="${pageContext.request.contextPath}/acquisition/requests/updateStatus" method="post" style="display:inline;">
                                    <input type="hidden" name="requestId" value="${request.requestID}">
                                    <input type="hidden" name="status" value="Rejected">
                                    <button type="submit">Reject</button>
                                </form>
                            </c:if>
                            <c:if test="${request.status == 'Approved'}">
                                <a href="${pageContext.request.contextPath}/acquisition/po/new?requestId=${request.requestID}">Create PO</a>
                            </c:if>
                             <form action="${pageContext.request.contextPath}/acquisition/requests/delete?id=${request.requestID}" method="post" style="display:inline;" onsubmit="return confirm('Are you sure you want to delete this request?');">
                                <input type="hidden" name="id" value="${request.requestID}">
                                <button type="submit">Delete</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty requests}">
                    <tr>
                        <td colspan="8">No acquisition requests found.</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
        <p><a href="${pageContext.request.contextPath}/index.jsp">Back to Home</a></p>
    </div>
</body>
</html>
