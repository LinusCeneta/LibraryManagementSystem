<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<html>
<head>
    <title><c:out value="${book.title}"/> - Library Catalog</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .book-detail-container { display: flex; gap: 30px; margin-top:20px;}
        .book-cover-detail { flex: 0 0 250px; }
        .book-cover-detail img { width: 100%; max-width:250px; height: auto; border: 1px solid #ddd; }
        .book-info-detail { flex-grow: 1; }
        .book-info-detail h1 { margin-top: 0; font-size: 1.8em; }
        .book-info-detail h2 { font-size: 1.2em; color: #555; margin-bottom: 15px;} /* Subtitle */
        .meta-info p { margin: 5px 0; }
        .meta-info strong { margin-right: 5px; }
        .availability-section, .actions-section { margin-top: 25px; padding-top:15px; border-top: 1px solid #eee;}
        .availability-section h3, .actions-section h3 { margin-bottom:10px; }
        .copy-item { padding: 8px; border-bottom: 1px dotted #eee; }
        .copy-item:last-child { border-bottom: none; }
        .copy-status-Available { color: green; font-weight: bold; }
        .copy-status-Checked_Out, .copy-status-On_Hold { color: orange; } /* Checked Out or On Hold */
        .copy-status-Lost, .copy-status-Damaged { color: red; }
    </style>
</head>
<body>
    <c:import url="/WEB-INF/jsp/common/navbar.jsp"/>

    <div class="container">
        <c:if test="${empty book}">
            <p class="error-message">Book not found.</p>
            <p><a href="${pageContext.request.contextPath}/catalog/search">Back to Search</a></p>
            <c:redirect url="${pageContext.request.contextPath}/catalog/search?message=BookNotFound"/>
        </c:if>

        <c:if test="${not empty message}">
            <p class="message"><c:out value="${message}"/></p>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <p class="error-message"><c:out value="${errorMessage}"/></p>
        </c:if>

        <div class="book-detail-container">
            <div class="book-cover-detail">
                <img src="${not empty book.coverImageURL ? pageContext.request.contextPath.concat(book.coverImageURL) : pageContext.request.contextPath.concat('/images/default-book-cover.png')}" alt="Cover for <c:out value='${book.title}'/>">
            </div>

            <div class="book-info-detail">
                <h1><c:out value="${book.title}"/></h1>
                <c:if test="${not empty book.subtitle}">
                    <h2><c:out value="${book.subtitle}"/></h2>
                </c:if>

                <div class="meta-info">
                    <p><strong>Author(s):</strong>
                        <c:forEach var="author" items="${book.bookAuthors}" varStatus="loop">
                            <a href="${pageContext.request.contextPath}/catalog/search?author=${author.authorName}"><c:out value="${author.authorName}"/></a><c:if test="${not loop.last}">, </c:if>
                        </c:forEach>
                    </p>
                    <p><strong>Publisher:</strong> <a href="${pageContext.request.contextPath}/catalog/search?publisher=${book.publisher.publisherName}"><c:out value="${book.publisher.publisherName}"/></a> (<c:out value="${book.publicationYear}"/>)</p>
                    <p><strong>ISBN:</strong> <c:out value="${book.isbn}"/></p>
                    <c:if test="${not empty book.edition}"><p><strong>Edition:</strong> <c:out value="${book.edition}"/></p></c:if>
                    <p><strong>Format:</strong> <a href="${pageContext.request.contextPath}/catalog/search?format=${book.format}"><c:out value="${book.format}"/></a></p>
                    <p><strong>Language:</strong> <a href="${pageContext.request.contextPath}/catalog/search?language=${book.language}"><c:out value="${book.language}"/></a></p>
                    <c:if test="${book.numberOfPages > 0}"><p><strong>Pages:</strong> ${book.numberOfPages}</p></c:if>
                    <p><strong>Categories:</strong>
                        <c:forEach var="category" items="${book.bookCategories}" varStatus="loop">
                            <a href="${pageContext.request.contextPath}/catalog/search?category=${category.categoryName}"><c:out value="${category.categoryName}"/></a><c:if test="${not loop.last}">, </c:if>
                        </c:forEach>
                    </p>
                    <c:if test="${not empty book.deweyDecimal}"><p><strong>Dewey Decimal:</strong> <c:out value="${book.deweyDecimal}"/></p></c:if>
                    <c:if test="${not empty book.locCallNumber}"><p><strong>LoC Call #:</strong> <c:out value="${book.locCallNumber}"/></p></c:if>
                </div>

                <c:if test="${not empty book.summary}">
                    <div class="summary-section" style="margin-top:15px;">
                        <h3>Summary</h3>
                        <p><c:out value="${book.summary}" escapeXml="false"/></p> <%-- Allow basic HTML if summary might contain it --%>
                    </div>
                </c:if>

                <div class="availability-section">
                    <h3>Availability</h3>
                    <c:if test="${empty allCopiesOfBook}">
                        <p>No copies of this book are currently in the system.</p>
                    </c:if>
                    <c:forEach var="copy" items="${allCopiesOfBook}">
                        <div class="copy-item">
                            <strong>Copy Barcode:</strong> <c:out value="${copy.copyBarcode}"/> |
                            <strong>Location:</strong>
                            <c:set var="locName" value="Unknown Location"/>
                            <c:forEach var="locEntry" items="${locationAvailability}"> <%-- This map might not be ideal here, better to have Location on Copy object --%>
                                <c:if test="${copy.locationID == locEntry.key.locationID}"><c:set var="locName" value="${locEntry.key.branchName}"/></c:if>
                            </c:forEach>
                            <c:out value="${locName}"/> |
                            <strong>Status:</strong> <span class="copy-status-${fn:replace(copy.status, ' ', '_')}"><c:out value="${copy.status}"/></span>
                            <c:if test="${copy.status == 'Checked Out'}">
                                <%-- Find the loan for this copy to display due date --%>
                                <%-- This is complex for JSP. Better to populate DueDate on Copy model in Servlet/DAO if checked out --%>
                                <%-- (Due: <fmt:formatDate value="${copy.dueDate}" pattern="yyyy-MM-dd"/>) --%>
                            </c:if>
                        </div>
                    </c:forEach>
                     <p><em>Total available at all branches: ${locationAvailability.values().stream().mapToInt(Integer::intValue).sum()}</em></p>
                </div>

                <div class="actions-section">
                    <h3>Actions</h3>
                    <c:choose>
                        <c:when test="${canPlaceHold}">
                            <form action="${pageContext.request.contextPath}/circulation/holds/place" method="post">
                                 <input type="hidden" name="_csrf" value="${_csrf}">
                                <input type="hidden" name="bookId" value="${book.bookID}">
                                <%-- TODO: Add preferred pickup location dropdown if multi-branch --%>
                                <button type="submit" class="button">Place Hold</button>
                            </form>
                        </c:when>
                        <c:when test="${empty sessionScope.user}">
                            <p><a href="${pageContext.request.contextPath}/user/login?redirect=${pageContext.request.contextPath}/catalog/book?id=${book.bookID}">Login</a> to place a hold or see more options.</p>
                        </c:when>
                        <c:otherwise>
                             <p>Check availability at your preferred branch. All available copies may be at other locations, or you may already have this item on loan/hold.</p>
                             <%-- Could add a link to "My Loans" or "My Holds" --%>
                        </c:otherwise>
                    </c:choose>
                    <%-- Add other actions like "Add to Wishlist", "Cite this", etc. later --%>
                </div>

            </div>
        </div>
        <p style="margin-top:30px;"><a href="${pageContext.request.contextPath}/catalog/search?q=${param.q}" class="button">Back to Search Results</a></p>
        <p><a href="${pageContext.request.contextPath}/catalog/home" class="button">Back to Catalog Home</a></p>
    </div>

    <c:import url="/WEB-INF/jsp/common/footer.jsp"/>
</body>
</html>
