<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${not empty error}">
    <div class="alert alert-danger">${error}</div>
</c:if>
<html>
<head>
    <title>Book List</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        :root {
            --primary: #5d4e6d;       /* Deep purple */
            --primary-dark: #423a4d;  /* Darker purple */
            --accent: #c8a97e;       /* Warm gold */
            --text-dark: #2a2630;     /* Dark grayish purple */
            --text-light: #857e8f;    /* Light grayish purple */
            --white: #ffffff;
            --off-white: #f9f7fa;
            --radius: 8px;
            --shadow: 0 4px 12px rgba(93, 78, 109, 0.1);
            --transition: all 0.3s ease;
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: 'Segoe UI', 'Georgia', sans-serif;
        }

        body {
            background-color: var(--off-white);
            color: var(--text-dark);
            padding: 20px;
        }

        .container {
            max-width: 1200px;
            margin: 0 auto;
        }

        h2 {
            color: var(--primary);
            font-size: 1.8rem;
            border-bottom: 2px solid var(--accent);
            padding-bottom: 0.5rem;
            margin-bottom: 1.5rem;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        .status-message {
            padding: 0.8rem;
            border-radius: var(--radius);
            margin-bottom: 1.5rem;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        .success {
            background-color: rgba(39, 174, 96, 0.1);
            border-left: 4px solid #27AE60;
            color: #27AE60;
        }

        .error {
            background-color: rgba(231, 76, 60, 0.1);
            border-left: 4px solid #E74C3C;
            color: #E74C3C;
        }

        .debug-info {
            background-color: var(--white);
            padding: 1rem;
            border-radius: var(--radius);
            box-shadow: var(--shadow);
            margin-bottom: 1.5rem;
            font-family: monospace;
            color: var(--text-light);
        }

        .add-btn {
            display: inline-flex;
            align-items: center;
            gap: 0.5rem;
            padding: 0.8rem 1.5rem;
            background-color: var(--primary);
            color: white;
            text-decoration: none;
            border-radius: var(--radius);
            transition: var(--transition);
            margin-bottom: 1.5rem;
        }

        .add-btn:hover {
            background-color: var(--primary-dark);
            transform: translateY(-2px);
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin: 1.5rem 0;
            box-shadow: var(--shadow);
            border-radius: var(--radius);
            overflow: hidden;
        }

        th, td {
            padding: 1rem;
            text-align: left;
            border-bottom: 1px solid var(--text-light);
        }

        th {
            background-color: var(--primary);
            color: white;
            font-weight: 500;
        }

        tr:nth-child(even) {
            background-color: var(--off-white);
        }

        tr:hover {
            background-color: rgba(200, 169, 126, 0.1);
        }

        .action-link {
            color: var(--primary);
            text-decoration: none;
            margin-right: 1rem;
            transition: var(--transition);
            display: inline-flex;
            align-items: center;
            gap: 0.3rem;
        }

        .action-link:hover {
            color: var(--primary-dark);
            text-decoration: underline;
        }

        .action-link.delete {
            color: #E74C3C;
        }

        .action-link.delete:hover {
            color: #C0392B;
        }

        .no-data {
            text-align: center;
            padding: 2rem;
            color: var(--text-light);
            background-color: var(--white);
            border-radius: var(--radius);
            box-shadow: var(--shadow);
        }

        .pagination {
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 1rem;
            margin: 1.5rem 0;
        }

        .pagination a, .pagination span {
            padding: 0.5rem 1rem;
            border-radius: var(--radius);
        }

        .pagination a {
            background-color: var(--accent);
            color: var(--text-dark);
            text-decoration: none;
            transition: var(--transition);
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        .pagination a:hover {
            background-color: #b8975e;
            transform: translateY(-2px);
        }

        .pagination span {
            font-weight: 500;
        }
    </style>
</head>
<body>

<h2>Book List</h2>

<!-- Debug Info -->
<p>Debug Info:</p>
<ul>
    <li>Book List exists: ${not empty bookList}</li>
    <li>Number of books: ${bookList.size()}</li>
</ul>

<!-- Add New Book Button -->
<a href="${pageContext.request.contextPath}/book-add">Add New Book</a><br/><br/>

<!-- Book Table -->
<table border="1" cellpadding="5">
    <tr>
        <th>ISBN</th>
        <th>Title</th>
        <th>Authors</th>
        <th>Publisher</th>
        <th>Categories</th>
        <th>Actions</th>
    </tr>
    <c:forEach var="book" items="${bookList}">
        <tr>
            <td>${book.isbn}</td>
            <td>${book.title}</td>
            <td>
                   <c:if test="${not empty book.authors}">
                   <c:forEach var="author" items="${book.authors}">
                    ${author.name}<br/>
            </c:forEach>
    </c:if>
          </td>

            <td>
                <c:if test="${book.publisher != null}">
                    ${book.publisher.name}
                </c:if>
            </td>
            <td>
                <c:forEach var="category" items="${book.categories}">
                    ${category.name}<br/>
                </c:forEach>
            </td>
            <td>
                <a href="${pageContext.request.contextPath}/book-edit?isbn=${book.isbn}">Edit</a> |
                <a href="${pageContext.request.contextPath}/book-delete?isbn=${book.isbn}"
                   onclick="return confirm('Are you sure you want to delete this book?');">Delete</a>
            </td>
        </tr>
    </c:forEach>
</table>

<!-- Optional: Show message -->
<c:if test="${not empty message}">
    <p style="color:green;">${message}</p>
</c:if>
<c:if test="${not empty errorMessage}">
    <p style="color:red;">${errorMessage}</p>
</c:if>

</body>
</html>
