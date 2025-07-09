<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head><title>Borrowing History</title></head>
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
            max-width: 1000px;
            margin: 0 auto;
        }

        h1 {
            color: var(--primary);
            font-size: 1.8rem;
            border-bottom: 2px solid var(--accent);
            padding-bottom: 0.5rem;
            margin-bottom: 1.5rem;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        .history-container {
            background-color: var(--white);
            padding: 1.5rem;
            border-radius: var(--radius);
            box-shadow: var(--shadow);
            margin-bottom: 2rem;
        }

        .history-table {
            width: 100%;
            border-collapse: collapse;
        }

        .history-table th, 
        .history-table td {
            padding: 1rem;
            text-align: left;
            border-bottom: 1px solid var(--off-white);
        }

        .history-table th {
            background-color: var(--primary);
            color: var(--white);
            font-weight: 500;
        }

        .history-table tr:nth-child(even) {
            background-color: var(--off-white);
        }

        .history-table tr:hover {
            background-color: rgba(200, 169, 126, 0.1);
        }

        .status-badge {
            padding: 0.3rem 0.6rem;
            border-radius: 1rem;
            font-size: 0.8rem;
            font-weight: 500;
            display: inline-block;
        }

        .status-returned {
            background-color: #E8F5E9;
            color: #2E7D32;
        }

        .status-overdue {
            background-color: #F8D7DA;
            color: #721C24;
        }

        .status-borrowed {
            background-color: #FFF3CD;
            color: #856404;
        }

        .back-btn {
            padding: 0.8rem 1.5rem;
            background-color: var(--text-light);
            color: white;
            border: none;
            border-radius: var(--radius);
            cursor: pointer;
            transition: var(--transition);
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 0.5rem;
        }

        .back-btn:hover {
            background-color: var(--primary-dark);
            transform: translateY(-2px);
        }

        .no-history {
            text-align: center;
            color: var(--text-light);
            padding: 2rem;
        }
    </style>
<body>
    <h1>Borrowing History</h1>
    <c:forEach var="record" items="${borrowingHistory}">
        <p>${record}</p>
    </c:forEach>
    <a href="memberSearch.jsp">Back to Search</a>
</body>
</html>
