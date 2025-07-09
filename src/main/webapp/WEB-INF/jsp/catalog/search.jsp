<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<html>
<head>
    <title>Catalog Search Results</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .search-container { display: flex; gap: 20px; }
        .search-filters { flex: 0 0 250px; border-right: 1px solid #ddd; padding-right: 20px; }
        .search-results-area { flex-grow: 1; }
        .search-bar-results-page { margin-bottom: 20px; }
        .search-bar-results-page input[type="text"] { width: 70%; padding: 0.5rem; }
        .search-bar-results-page button { padding: 0.5rem 1rem; }
        .facet-group { margin-bottom: 15px; }
        .facet-group h4 { margin-bottom: 5px; font-size: 1em; }
        .facet-group ul { list-style: none; padding-left: 0; margin-left: 10px; max-height: 200px; overflow-y: auto; }
        .facet-group ul li a { text-decoration: none; color: #337ab7; }
        .facet-group ul li a:hover { text-decoration: underline; }
        .book-result-item { border-bottom: 1px solid #eee; padding: 15px 0; display: flex; }
        .book-cover-search { flex: 0 0 100px; margin-right: 15px; }
        .book-cover-search img { max-width: 100px; height: auto; max-height: 140px; object-fit: cover; border: 1px solid #ddd;}
        .book-details-search { flex-grow: 1; }
        .book-details-search h3 { margin-top: 0; margin-bottom: 5px;}
        .book-details-search p { margin: 3px 0; font-size: 0.9em; }
        .pagination { text-align: center; margin-top: 20px; }
        .pagination a, .pagination span { margin: 0 5px; padding: 5px 10px; border: 1px solid #ddd; text-decoration: none; }
        .pagination .current-page { background-color: #337ab7; color: white; border-color: #337ab7; }
        .applied-filters span { background-color: #e0e0e0; padding: 3px 7px; border-radius: 3px; margin-right: 5px; font-size:0.9em;}
        .applied-filters a { color: red; text-decoration: none; font-weight: bold; margin-left:3px;}
    </style>
</head>
<body>
    <c:import url="/WEB-INF/jsp/common/navbar.jsp"/>

    <div class="container">
        <h1>Catalog Search</h1>

        <div class="search-bar-results-page">
            <form action="${pageContext.request.contextPath}/catalog/search" method="get" id="searchForm">
                <input type="text" name="q" placeholder="Search by title, author, ISBN..." value="<c:out value='${searchText}'/>">
                <button type="submit">Search</button>
                <%-- Hidden fields for existing filters to persist them on new search --%>
                <c:forEach var="filterEntry" items="${appliedFilters}">
                    <c:forEach var="value" items="${filterEntry.value}">
                        <input type="hidden" name="${filterEntry.key}" value="<c:out value='${value}'/>">
                    </c:forEach>
                </c:forEach>
            </form>
        </div>

        <c:if test="${not empty appliedFilters}">
            <div class="applied-filters">
                <strong>Applied Filters:</strong>
                <c:forEach var="filterEntry" items="${appliedFilters}">
                    <c:forEach var="value" items="${filterEntry.value}">
                        <span><c:out value="${filterEntry.key}"/>: <c:out value="${value}"/>
                           <a href="${pageContext.request.contextPath}/catalog/search${fn:replace(pageContext.request.queryString, fn:concat(filterEntry.key, '='.concat(fn:replace(value, ' ', '%20'))), '')}&q=${searchText}" title="Remove filter">&times;</a>
                        </span>
                    </c:forEach>
                </c:forEach>
                 <a href="${pageContext.request.contextPath}/catalog/search?q=${searchText}" class="button" style="font-size:0.8em; padding:3px 6px; margin-left:10px;">Clear All Filters</a>
            </div>
        </c:if>


        <div class="search-container">
            <aside class="search-filters">
                <h2>Refine Results</h2>

                <%-- Author Facet --%>
                <div class="facet-group">
                    <h4>Authors</h4>
                    <ul>
                        <c:forEach var="author" items="${allAuthors}" varStatus="loop" begin="0" end="9"> <%-- Limit display --%>
                            <li><a href="${pageContext.request.contextPath}/catalog/search?q=${searchText}&author=<c:out value='${author.authorName}'/>${currentFilterParamsSansAuthor}">${author.authorName}</a></li>
                        </c:forEach>
                        <c:if test="${fn:length(allAuthors) > 10}"><li><a href="#">More...</a></li></c:if>
                    </ul>
                </div>

                <%-- Category Facet --%>
                <div class="facet-group">
                    <h4>Categories</h4>
                    <ul>
                        <c:forEach var="category" items="${allCategories}" varStatus="loop" begin="0" end="9">
                             <li><a href="${pageContext.request.contextPath}/catalog/search?q=${searchText}&category=<c:out value='${category.categoryName}'/>${currentFilterParamsSansCategory}">${category.categoryName}</a></li>
                        </c:forEach>
                         <c:if test="${fn:length(allCategories) > 10}"><li><a href="#">More...</a></li></c:if>
                    </ul>
                </div>

                <%-- Publication Year Facet (Example: ranges or specific years) --%>
                <div class="facet-group">
                    <h4>Publication Year</h4>
                     <ul>
                        <c:forEach var="year" items="${allPublicationYears}" varStatus="loop" begin="0" end="9">
                             <li><a href="${pageContext.request.contextPath}/catalog/search?q=${searchText}&year=<c:out value='${year}'/>${currentFilterParamsSansYear}">${year}</a></li>
                        </c:forEach>
                         <c:if test="${fn:length(allPublicationYears) > 10}"><li><a href="#">More...</a></li></c:if>
                    </ul>
                </div>

                <%-- Language Facet --%>
                <div class="facet-group">
                    <h4>Language</h4>
                     <ul>
                        <c:forEach var="lang" items="${allLanguages}">
                             <li><a href="${pageContext.request.contextPath}/catalog/search?q=${searchText}&language=<c:out value='${lang}'/>${currentFilterParamsSansLanguage}">${lang}</a></li>
                        </c:forEach>
                    </ul>
                </div>

                <%-- Format Facet --%>
                 <div class="facet-group">
                    <h4>Format</h4>
                     <ul>
                        <c:forEach var="format" items="${allFormats}">
                             <li><a href="${pageContext.request.contextPath}/catalog/search?q=${searchText}&format=<c:out value='${format}'/>${currentFilterParamsSansFormat}">${format}</a></li>
                        </c:forEach>
                    </ul>
                </div>

                 <%-- Location/Branch Facet --%>
                 <div class="facet-group">
                    <h4>Branch</h4>
                     <ul>
                        <c:forEach var="location" items="${allLocations}">
                             <li><a href="${pageContext.request.contextPath}/catalog/search?q=${searchText}&location=<c:out value='${location.locationID}'/>${currentFilterParamsSansLocation}">${location.branchName}</a></li>
                        </c:forEach>
                    </ul>
                </div>
                <%-- TODO: Availability Facet (e.g., "Available Now") --%>

            </aside>

            <main class="search-results-area">
                <c:if test="${not empty searchText || not empty appliedFilters}">
                    <p>Showing results for: <strong>"<c:out value='${searchText}'/>"</strong>. Found ${totalResults} items.</p>
                </c:if>

                <c:if test="${empty searchResults && (not empty searchText || not empty appliedFilters)}">
                    <p>No books found matching your criteria. Try broadening your search or removing some filters.</p>
                </c:if>

                <c:forEach var="book" items="${searchResults}">
                    <div class="book-result-item">
                        <div class="book-cover-search">
                            <a href="${pageContext.request.contextPath}/catalog/book?id=${book.bookID}">
                                <img src="${not empty book.coverImageURL ? pageContext.request.contextPath.concat(book.coverImageURL) : pageContext.request.contextPath.concat('/images/default-book-cover.png')}" alt="Cover for <c:out value='${book.title}'/>">
                            </a>
                        </div>
                        <div class="book-details-search">
                            <h3><a href="${pageContext.request.contextPath}/catalog/book?id=${book.bookID}"><c:out value="${book.title}"/></a></h3>
                            <p><strong>Author(s):</strong> <c:out value="${book.authorsConcatenated}"/></p> <%-- TODO: Link individual authors --%>
                            <p><strong>Publisher:</strong> <c:out value="${book.publisher.publisherName}"/> (${book.publicationYear})</p>
                            <p><strong>ISBN:</strong> <c:out value="${book.isbn}"/></p>
                            <p><strong>Format:</strong> <c:out value="${book.format}"/></p>
                            <p><strong>Language:</strong> <c:out value="${book.language}"/></p>
                            <%-- TODO: Show basic availability summary (e.g., "Available at Main Library") --%>
                            <p><a href="${pageContext.request.contextPath}/catalog/book?id=${book.bookID}" class="button">View Details</a></p>
                        </div>
                    </div>
                </c:forEach>

                <%-- Pagination --%>
                <c:if test="${totalPages > 1}">
                    <div class="pagination">
                        <c:if test="${currentPage > 1}">
                            <a href="${pageContext.request.contextPath}/catalog/search?q=${searchText}&page=${currentPage - 1}${currentFilterParams}">Previous</a>
                        </c:if>
                        <c:forEach var="i" begin="1" end="${totalPages}">
                            <c:choose>
                                <c:when test="${i == currentPage}">
                                    <span class="current-page">${i}</span>
                                </c:when>
                                <c:otherwise>
                                    <a href="${pageContext.request.contextPath}/catalog/search?q=${searchText}&page=${i}${currentFilterParams}">${i}</a>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                        <c:if test="${currentPage < totalPages}">
                            <a href="${pageContext.request.contextPath}/catalog/search?q=${searchText}&page=${currentPage + 1}${currentFilterParams}">Next</a>
                        </c:if>
                    </div>
                </c:if>
            </main>
        </div>
         <p><a href="${pageContext.request.contextPath}/catalog/home">Back to Catalog Home</a></p>
    </div>
    <c:import url="/WEB-INF/jsp/common/footer.jsp"/>

    <%-- Helper script to build current filter params for facet links --%>
    <c:set var="currentFilterParams" value=""/>
    <c:forEach var="filterEntry" items="${appliedFilters}">
        <c:forEach var="value" items="${filterEntry.value}">
            <c:set var="currentFilterParams" value="${currentFilterParams}&${filterEntry.key}=${value}"/>
        </c:forEach>
    </c:forEach>

    <%-- For each facet type, create a param string excluding that facet's own key --%>
    <c:set var="currentFilterParamsSansAuthor" value=""/>
    <c:forEach var="filterEntry" items="${appliedFilters}"><c:if test="${filterEntry.key != 'author'}"><c:forEach var="value" items="${filterEntry.value}"><c:set var="currentFilterParamsSansAuthor" value="${currentFilterParamsSansAuthor}&${filterEntry.key}=${value}"/></c:forEach></c:if></c:forEach>
    <c:set var="currentFilterParamsSansCategory" value=""/>
    <c:forEach var="filterEntry" items="${appliedFilters}"><c:if test="${filterEntry.key != 'category'}"><c:forEach var="value" items="${filterEntry.value}"><c:set var="currentFilterParamsSansCategory" value="${currentFilterParamsSansCategory}&${filterEntry.key}=${value}"/></c:forEach></c:if></c:forEach>
    <c:set var="currentFilterParamsSansYear" value=""/>
    <c:forEach var="filterEntry" items="${appliedFilters}"><c:if test="${filterEntry.key != 'year'}"><c:forEach var="value" items="${filterEntry.value}"><c:set var="currentFilterParamsSansYear" value="${currentFilterParamsSansYear}&${filterEntry.key}=${value}"/></c:forEach></c:if></c:forEach>
    <c:set var="currentFilterParamsSansLanguage" value=""/>
    <c:forEach var="filterEntry" items="${appliedFilters}"><c:if test="${filterEntry.key != 'language'}"><c:forEach var="value" items="${filterEntry.value}"><c:set var="currentFilterParamsSansLanguage" value="${currentFilterParamsSansLanguage}&${filterEntry.key}=${value}"/></c:forEach></c:if></c:forEach>
    <c:set var="currentFilterParamsSansFormat" value=""/>
    <c:forEach var="filterEntry" items="${appliedFilters}"><c:if test="${filterEntry.key != 'format'}"><c:forEach var="value" items="${filterEntry.value}"><c:set var="currentFilterParamsSansFormat" value="${currentFilterParamsSansFormat}&${filterEntry.key}=${value}"/></c:forEach></c:if></c:forEach>
    <c:set var="currentFilterParamsSansLocation" value=""/>
    <c:forEach var="filterEntry" items="${appliedFilters}"><c:if test="${filterEntry.key != 'location'}"><c:forEach var="value" items="${filterEntry.value}"><c:set var="currentFilterParamsSansLocation" value="${currentFilterParamsSansLocation}&${filterEntry.key}=${value}"/></c:forEach></c:if></c:forEach>

</body>
</html>
