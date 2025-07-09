<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>New Acquisition Request</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <h1>Submit New Acquisition Request</h1>
        <p>Please provide details for the book you would like to request.</p>

        <c:if test="${not empty errorMessage}">
            <p class="error-message">${errorMessage}</p>
        </c:if>
        <c:if test="${sessionScope.user == null}">
             <p class="error-message">You must be logged in to submit a request. <a href="${pageContext.request.contextPath}/user/login">Login here</a>.</p>
             <%-- Optionally prevent form display or disable submit if user not logged in --%>
        </c:if>


        <form action="${pageContext.request.contextPath}/acquisition/requests/new" method="post">
            <input type="hidden" name="_csrf" value="${_csrf}">
            <p>
                <label for="bookTitle">Book Title:</label><br>
                <input type="text" id="bookTitle" name="bookTitle" required size="50">
            </p>
            <p>
                <label for="author">Author(s):</label><br>
                <input type="text" id="author" name="author" size="50">
            </p>
            <p>
                <label for="isbn">ISBN (if known):</label><br>
                <input type="text" id="isbn" name="isbn" size="20">
            </p>
            <p>
                <label for="publisher">Publisher (if known):</label><br>
                <input type="text" id="publisher" name="publisher" size="30">
            </p>
            <p>
                <label for="publicationYear">Publication Year (if known):</label><br>
                <input type="number" id="publicationYear" name="publicationYear" min="1000" max="2100">
            </p>
            <p>
                <label for="notes">Notes (optional, e.g., reason for request, specific edition):</label><br>
                <textarea id="notes" name="notes" rows="4" cols="50"></textarea>
            </p>

            <%-- RequestedBy and RequestDate will be set by the servlet --%>
            <%-- Status will be defaulted by the servlet --%>

            <p>
                <button type="submit" <c:if test="${sessionScope.user == null}">disabled</c:if>>Submit Request</button>
                <a href="${pageContext.request.contextPath}/acquisition/requests/list">Cancel</a>
            </p>
        </form>
    </div>
</body>
</html>
