<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Hold and Reservation Report</title>
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

        h2 {
            color: var(--primary);
            font-size: 1.8rem;
            border-bottom: 2px solid var(--accent);
            padding-bottom: 0.5rem;
            margin-bottom: 1.5rem;
            text-align: center;
        }

        form {
            margin-bottom: 1.5rem;
            display: flex;
            gap: 1rem;
        }

        button {
            padding: 0.8rem 1.5rem;
            background-color: var(--accent);
            color: var(--text-dark);
            border: none;
            border-radius: var(--radius);
            cursor: pointer;
            font-weight: 500;
            transition: var(--transition);
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 0.5rem;
        }

        button:hover {
            background-color: #b8975e;
            transform: translateY(-2px);
        }

        button[name="export"] {
            background-color: var(--primary);
            color: white;
        }

        button[name="export"]:hover {
            background-color: var(--primary-dark);
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 1rem;
            box-shadow: var(--shadow);
            border-radius: var(--radius);
            overflow: hidden;
        }

        th, td {
            padding: 1rem;
            text-align: left;
            border: 1px solid var(--text-light);
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

        .error {
            background-color: rgba(231, 76, 60, 0.1);
            border-left: 4px solid #E74C3C;
            color: #E74C3C;
            padding: 0.8rem;
            margin-bottom: 1rem;
            border-radius: var(--radius);
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        .error i {
            font-size: 1.2rem;
        }
    </style>
</head>
<body>

<h2>Hold and Reservation Report</h2>

<c:if test="${not empty error}">
    <p style="color:red;">${error}</p>
</c:if>

<form method="get" action="holdReport">
    <button type="submit">Refresh Report</button>
    <button type="submit" name="export" value="csv">Export CSV</button>
</form>

<table border="1" cellpadding="5" cellspacing="0">
    <thead>
        <tr>
            <th>Title</th>
            <th>Hold Queue</th>
            <th>Average Wait Days</th>
        </tr>
    </thead>
    <tbody>
    <c:forEach var="item" items="${reportList}">
        <tr>
            <td><c:out value="${item.title}" /></td>
            <td><c:out value="${item.holdQueue}" /></td>
            <td><c:out value="${item.avgWaitDays}" /></td>
        </tr>
    </c:forEach>
    <c:if test="${empty reportList}">
        <tr><td colspan="3">No records found.</td></tr>
    </c:if>
    </tbody>
</table>

</body>
</html>
