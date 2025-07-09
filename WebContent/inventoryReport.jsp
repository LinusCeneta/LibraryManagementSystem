<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Inventory Report</title>
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

        .error {
            color: #E74C3C;
            background-color: rgba(231, 76, 60, 0.1);
            padding: 0.8rem;
            border-radius: var(--radius);
            margin-bottom: 1rem;
            border-left: 4px solid #E74C3C;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        .filter-form {
            background-color: var(--white);
            padding: 1.5rem;
            border-radius: var(--radius);
            box-shadow: var(--shadow);
            margin-bottom: 2rem;
        }

        .form-group {
            margin-bottom: 1.5rem;
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 1rem;
            align-items: end;
        }

        .form-control {
            display: flex;
            flex-direction: column;
        }

        label {
            margin-bottom: 0.5rem;
            font-weight: 500;
            color: var(--primary-dark);
        }

        input[type="date"],
        input[type="text"] {
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

        .btn {
            padding: 0.8rem 1.5rem;
            border: none;
            border-radius: var(--radius);
            cursor: pointer;
            font-weight: 500;
            transition: var(--transition);
            display: inline-flex;
            align-items: center;
            gap: 0.5rem;
        }

        .btn-primary {
            background-color: var(--accent);
            color: var(--text-dark);
        }

        .btn-primary:hover {
            background-color: #b8975e;
            transform: translateY(-2px);
        }

        .btn-secondary {
            background-color: var(--primary);
            color: white;
        }

        .btn-secondary:hover {
            background-color: var(--primary-dark);
            transform: translateY(-2px);
        }

        .report-table {
            width: 100%;
            border-collapse: collapse;
            margin: 2rem 0;
            box-shadow: var(--shadow);
            background-color: var(--white);
            border-radius: var(--radius);
            overflow: hidden;
        }

        .report-table th, 
        .report-table td {
            padding: 1rem;
            text-align: left;
            border-bottom: 1px solid var(--primary-light);
        }

        .report-table th {
            background-color: var(--primary);
            color: var(--white);
            font-weight: 500;
        }

        .report-table tr:nth-child(even) {
            background-color: var(--off-white);
        }

        .report-table tr:hover {
            background-color: rgba(200, 169, 126, 0.1);
        }

        .no-data {
            background-color: var(--white);
            padding: 1.5rem;
            border-radius: var(--radius);
            box-shadow: var(--shadow);
            text-align: center;
            color: var(--text-light);
        }

        .total-row {
            font-weight: bold;
            background-color: rgba(200, 169, 126, 0.2) !important;
        }

        .currency {
            font-family: 'Courier New', monospace;
        }
    </style>
</head>
<body>

<h2>Inventory Report - New Acquisitions</h2>

<c:if test="${not empty error}">
    <p style="color:red;">${error}</p>
</c:if>

<form method="get" action="inventoryReport">
    Start Date: <input type="date" name="startDate" value="${param.startDate}" required />
    End Date: <input type="date" name="endDate" value="${param.endDate}" required />
    Category: <input type="text" name="category" value="${param.category}" />
    <button type="submit">Filter</button>
    <button type="submit" name="export" value="csv">Export CSV</button>
</form>

<table border="1" cellpadding="5" cellspacing="0">
    <thead>
        <tr>
            <th>Date Added</th>
            <th>Item Count</th>
            <th>Total Cost</th>
        </tr>
    </thead>
    <tbody>
    <c:forEach var="item" items="${reportList}">
        <tr>
            <td><c:out value="${item.dateAdded}" /></td>
            <td><c:out value="${item.itemCount}" /></td>
            <td><c:out value="${item.totalCost}" /></td>
        </tr>
    </c:forEach>
    <c:if test="${empty reportList}">
        <tr><td colspan="3">No records found.</td></tr>
    </c:if>
    </tbody>
</table>

</body>
</html>
