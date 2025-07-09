<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Library Copies</title>
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

        h1 {
            color: var(--primary);
            font-size: 2rem;
            border-bottom: 2px solid var(--accent);
            padding-bottom: 0.5rem;
            margin-bottom: 1.5rem;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        .filter-container {
            background-color: var(--white);
            padding: 1rem;
            border-radius: var(--radius);
            box-shadow: var(--shadow);
            margin-bottom: 1.5rem;
            display: flex;
            gap: 1rem;
            align-items: center;
            flex-wrap: wrap;
        }

        label {
            font-weight: 500;
            color: var(--primary-dark);
        }

        select {
            padding: 0.5rem 1rem;
            border: 1px solid var(--text-light);
            border-radius: var(--radius);
            font-size: 1rem;
            transition: var(--transition);
        }

        select:focus {
            border-color: var(--accent);
            outline: none;
            box-shadow: 0 0 0 2px rgba(200, 169, 126, 0.3);
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
            cursor: pointer;
            transition: var(--transition);
        }

        th:hover {
            background-color: var(--primary-dark);
        }

        tr:nth-child(even) {
            background-color: var(--off-white);
        }

        tr:hover {
            background-color: rgba(200, 169, 126, 0.1);
        }

        .status-available {
            color: #27AE60;
            font-weight: 500;
        }

        .status-loaned {
            color: #F39C12;
            font-weight: 500;
        }

        .status-lost {
            color: #E74C3C;
            font-weight: 500;
        }

        .status-on_hold {
            color: #3498DB;
            font-weight: 500;
        }

        .status-under_repair {
            color: #9B59B6;
            font-weight: 500;
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
            margin-top: 1rem;
        }

        .add-btn:hover {
            background-color: var(--primary-dark);
            transform: translateY(-2px);
        }
    </style>
</head>
<body>
    <h1>Library Copies</h1>
    
    <c:if test="${not empty message}">
        <div class="message">${message}</div>
    </c:if>
    
    <div>
        <label for="statusFilter">Filter by Status:</label>
        <select id="statusFilter" onchange="filterByStatus()">
            <option value="">All</option>
            <c:forEach items="${statuses}" var="status">
                <option value="${status}" ${param.status == status ? 'selected' : ''}>${status}</option>
            </c:forEach>
        </select>
    </div>
    
    <table>
        <thead>
            <tr>
                <th onclick="sortTable('copyId')">Copy ID</th>
                <th onclick="sortTable('isbn')">ISBN</th>
                <th onclick="sortTable('condition')">Condition</th>
                <th onclick="sortTable('status')">Status</th>
                <th>Location</th>
                <th>Acquisition Date</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach items="${copies}" var="copy">
                <tr>
                    <td>${copy.copyId}</td>
                    <td>${copy.isbn}</td>
                    <td>${copy.condition}</td>
                    <td class="status-${copy.status.name().toLowerCase()}">${copy.status}</td>
                    <td>${copy.location}</td>
                    <td>${copy.acquisitionDate}</td>
                    <td>
                       <a href="${pageContext.request.contextPath}/copies/edit?copyId=${copy.copyId}">Edit</a>
                       <a href="${pageContext.request.contextPath}/copies?action=delete&copyId=${copy.copyId}" 
                        onclick="return confirm('Are you sure you want to delete this copy?')">Delete</a>
                   </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    
    <div class="pagination">
        <c:if test="${page > 1}">
            <a href="copies?page=${page - 1}">Previous</a>
        </c:if>
        <span>Page ${page} of ${totalPages}</span>
        <c:if test="${page < totalPages}">
            <a href="copies?page=${page + 1}">Next</a>
        </c:if>
    </div>
    
    <br>
    <a href="addCopy.jsp">Add New Copy</a>
    
    <script>
        function sortTable(column) {
            window.location.href = 'copies?sort=' + column + '&direction=' + 
                ('${param.sort}' === column && '${param.direction}' === 'asc' ? 'desc' : 'asc');
        }
        
        function filterByStatus() {
            const status = document.getElementById('statusFilter').value;
            window.location.href = 'copies?status=' + status;
        }
    </script>
</body>
</html>