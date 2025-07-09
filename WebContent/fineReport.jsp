<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Fine Revenue Report</title>
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

        .filter-container {
            background-color: var(--white);
            padding: 1.5rem;
            border-radius: var(--radius);
            box-shadow: var(--shadow);
            margin-bottom: 1.5rem;
        }

        .filter-form {
            display: flex;
            gap: 1rem;
            align-items: flex-end;
            flex-wrap: wrap;
        }

        .form-group {
            margin-bottom: 0;
            flex-grow: 1;
        }

        label {
            display: block;
            margin-bottom: 0.5rem;
            font-weight: 500;
            color: var(--primary-dark);
        }

        input[type="date"] {
            width: 100%;
            padding: 0.8rem 1rem;
            border: 1px solid var(--text-light);
            border-radius: var(--radius);
            font-size: 1rem;
            transition: var(--transition);
        }

        input:focus {
            border-color: var(--accent);
            outline: none;
            box-shadow: 0 0 0 2px rgba(200, 169, 126, 0.3);
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
            height: fit-content;
        }

        button[name="export"] {
            background-color: var(--primary);
            color: white;
        }

        button:hover {
            transform: translateY(-2px);
        }

        button[name="export"]:hover {
            background-color: var(--primary-dark);
        }

        button[type="submit"]:hover {
            background-color: #b8975e;
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

        .summary-card {
            background-color: var(--white);
            padding: 1.5rem;
            border-radius: var(--radius);
            box-shadow: var(--shadow);
            margin-bottom: 1.5rem;
            display: flex;
            justify-content: space-between;
            flex-wrap: wrap;
            gap: 1rem;
        }

        .summary-item {
            flex: 1;
            min-width: 200px;
        }

        .summary-value {
            font-size: 1.5rem;
            font-weight: 600;
            color: var(--primary);
        }

        .summary-label {
            color: var(--text-light);
            font-size: 0.9rem;
        }
    </style>
</head>
<body>

<h2>Fine Revenue Report</h2>

<c:if test="${not empty error}">
    <p style="color:red;">${error}</p>
</c:if>

<form method="get" action="fineReport">
    Start Date: <input type="date" name="startDate" value="${param.startDate}" required />
    End Date: <input type="date" name="endDate" value="${param.endDate}" required />
    <button type="submit">Filter</button>
    <button type="submit" name="export" value="csv">Export CSV</button>
</form>

<table border="1" cellpadding="5" cellspacing="0">
    <thead>
        <tr>
            <th>Member ID</th>
            <th>Member Name</th>
            <th>Total Fines</th>
            <th>Average Fine</th>
        </tr>
    </thead>
    <tbody>
    <c:forEach var="item" items="${reportList}">
        <tr>
            <td><c:out value="${item.memberId}" /></td>
            <td><c:out value="${item.memberName}" /></td>
            <td><c:out value="${item.totalFines}" /></td>
            <td><c:out value="${item.averageFine}" /></td>
        </tr>
    </c:forEach>
    <c:if test="${empty reportList}">
        <tr><td colspan="4">No records found.</td></tr>
    </c:if>
    </tbody>
</table>

</body>
</html>
