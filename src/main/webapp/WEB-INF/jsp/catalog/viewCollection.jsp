<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html>
<head>
    <title>Collection: <c:out value="${collection.collectionName}"/></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .collection-header { margin-bottom: 20px; padding-bottom:10px; border-bottom: 1px solid #eee; }
        .collection-header p { font-size: 0.9em; color: #666; }
        .book-grid-item {
            border: 1px solid #ddd;
            padding: 10px;
            margin-bottom: 15px;
            border-radius: 4px;
            min-height: 200px; /* Adjust as needed */
            display: flex;
            flex-direction: column;
            justify-content: space-between;
        }
        .book-grid-item img { max-width: 80px; height: auto; float:left; margin-right:10px; }
        .book-grid-item h4 { margin-top:0; }
         /* Basic grid for books, can be enhanced with CSS Grid or Flexbox for better layout */
        .books-in-collection {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); /* Responsive grid */
            gap: 20px;
        }
    </style>
</head>
<body>
    <c:import url="/WEB-INF/jsp/common/navbar.jsp"/>

    <div class="container">
        <c:if test="${empty collection}">
            <p class="error-message">Collection not found.</p>
            <p><a href="${pageContext.request.contextPath}/catalog/home">Back to Catalog Home</a></p>
            <c:redirect url="${pageContext.request.contextPath}/catalog/home?message=CollectionNotFound"/>
        </c:if>

        <div class="collection-header">
            <h1><c:out value="${collection.collectionName}"/></h1>
            <p>Curated by: <c:out value="${collection.createdByStaff.username}"/> | Created on: <fmt:formatDate value="${collection.dateCreated}" pattern="yyyy-MM-dd"/></p>
            <c:if test="${not empty collection.description}">
                <p><c:out value="${collection.description}"/></p>
            </c:if>
        </div>

        <c:if test="${not empty message}">
            <p class="message"><c:out value="${message}"/></p>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <p class="error-message"><c:out value="${errorMessage}"/></p>
        </c:if>

        <div class="books-in-collection">
            <c:if test="${empty collection.books}">
                <p>This collection currently has no books.</p>
            </c:if>

            <c:forEach var="book" items="${collection.books}">
                <div class="book-grid-item">
                    <div>
                        <a href="${pageContext.request.contextPath}/catalog/book?id=${book.bookID}">
                            <img src="${not empty book.coverImageURL ? pageContext.request.contextPath.concat(book.coverImageURL) : pageContext.request.contextPath.concat('/images/default-book-cover.png')}" alt="Cover for <c:out value='${book.title}'/>">
                        </a>
                        <h4><a href="${pageContext.request.contextPath}/catalog/book?id=${book.bookID}"><c:out value="${book.title}"/></a></h4>
                        <p style="font-size:0.9em;">
                            <c:if test="${not empty book.bookAuthors}">
                                By:
                                <c:forEach var="author" items="${book.bookAuthors}" varStatus="loop">
                                    <c:out value="${author.authorName}"/><c:if test="${not loop.last}">, </c:if>
                                </c:forEach>
                                <br>
                            </c:if>
                            ISBN: <c:out value="${book.isbn}"/>
                        </p>
                    </div>
                    <p style="margin-top:auto;">
                         <a href="${pageContext.request.contextPath}/catalog/book?id=${book.bookID}" class="button">View Details</a>
                         <%-- TODO: Add "Place Hold" button if applicable and user is member --%>
                    </p>
                </div>
            </c:forEach>
        </div>

        <p style="margin-top: 30px;">
            <a href="${pageContext.request.contextPath}/catalog/home" class="button">Back to Catalog Home</a>
        </p>
    </div>

    <c:import url="/WEB-INF/jsp/common/footer.jsp"/>
</body>
</html>
