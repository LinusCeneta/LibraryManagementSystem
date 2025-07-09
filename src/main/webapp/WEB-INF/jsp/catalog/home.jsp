<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>
<head>
    <title>Library Catalog Home</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .search-banner {
            background-color: #e9ecef;
            padding: 2rem 1rem;
            margin-bottom: 2rem;
            text-align: center;
        }
        .search-banner input[type="text"] {
            width: 60%;
            padding: 0.75rem;
            font-size: 1.1rem;
            margin-right: 0.5rem;
        }
        .search-banner button {
            padding: 0.75rem 1.5rem;
            font-size: 1.1rem;
        }
        .collections-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 1rem;
        }
        .collection-card {
            border: 1px solid #ddd;
            padding: 1rem;
            border-radius: 4px;
        }
        .collection-card h3 { margin-top: 0;}
    </style>
</head>
<body>
    <%-- Include a common header JSP if you have one --%>
    <%-- <jsp:include page="/WEB-INF/jsp/common/header.jsp" /> --%>
    <c:import url="/WEB-INF/jsp/common/navbar.jsp"/>


    <div class="search-banner">
        <h2>Search the Catalog</h2>
        <form action="${pageContext.request.contextPath}/catalog/search" method="get">
            <input type="text" name="q" placeholder="Search by title, author, ISBN, keyword..." value="<c:out value='${param.q}'/>">
            <button type="submit">Search</button>
        </form>
    </div>

    <div class="container">
        <h1>Welcome to the Library Catalog</h1>

        <c:if test="${not empty message}">
            <p class="message"><c:out value="${message}"/></p>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <p class="error-message"><c:out value="${errorMessage}"/></p>
        </c:if>

        <%-- Placeholder for New Arrivals or Popular Books --%>
        <%--
        <section class="new-arrivals">
            <h2>New Arrivals</h2>
            <div class="books-grid">
                <c:forEach var="book" items="${newArrivals}">
                    <div class="book-card">
                        <a href="${pageContext.request.contextPath}/catalog/book?id=${book.bookID}">
                            <img src="${not empty book.coverImageURL ? pageContext.request.contextPath.concat(book.coverImageURL) : pageContext.request.contextPath.concat('/images/default-book-cover.png')}" alt="Cover for <c:out value='${book.title}'/>" style="width:100px; height:150px; object-fit:cover;">
                            <h4><c:out value="${book.title}"/></h4>
                        </a>
                        <p><c:out value="${book.authorsConcatenated}"/></p>
                    </div>
                </c:forEach>
            </div>
        </section>
        --%>

        <section class="featured-collections">
            <h2>Featured Collections</h2>
            <c:if test="${empty featuredCollections}">
                <p>No featured collections at this time. Check back soon!</p>
            </c:if>
            <div class="collections-grid">
                <c:forEach var="collection" items="${featuredCollections}">
                    <div class="collection-card">
                        <h3><a href="${pageContext.request.contextPath}/catalog/collection?id=${collection.collectionID}"><c:out value="${collection.collectionName}"/></a></h3>
                        <p><c:out value="${collection.description}"/></p>
                        <p><small>Created by: <c:out value="${collection.createdByStaff.username}"/> on <fmt:formatDate value="${collection.dateCreated}" pattern="yyyy-MM-dd"/></small></p>
                    </div>
                </c:forEach>
            </div>
        </section>

        <p style="margin-top: 20px;">
            <a href="${pageContext.request.contextPath}/index.jsp" class="button">Back to Main Library Home</a>
        </p>

    </div>

    <%-- Include a common footer JSP if you have one --%>
    <%-- <jsp:include page="/WEB-INF/jsp/common/footer.jsp" /> --%>
     <c:import url="/WEB-INF/jsp/common/footer.jsp"/>
</body>
</html>
