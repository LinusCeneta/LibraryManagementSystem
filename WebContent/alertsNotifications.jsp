<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Alerts & Notifications</title>
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

        .error {
            background-color: rgba(231, 76, 60, 0.1);
            border-left: 4px solid #E74C3C;
            color: #E74C3C;
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

        .alert-overdue {
            border-left: 4px solid #E74C3C;
        }

        .alert-due-soon {
            border-left: 4px solid #F39C12;
        }

        .alert-new {
            border-left: 4px solid #3498DB;
        }

        .alert-reservation {
            border-left: 4px solid #27AE60;
        }

        .no-alerts {
            text-align: center;
            padding: 2rem;
            color: var(--text-light);
            background-color: var(--white);
            border-radius: var(--radius);
            box-shadow: var(--shadow);
        }

        .badge {
            display: inline-block;
            padding: 0.25rem 0.5rem;
            border-radius: var(--radius);
            font-size: 0.8rem;
            font-weight: 500;
            margin-left: 0.5rem;
        }

        .badge-overdue {
            background-color: rgba(231, 76, 60, 0.1);
            color: #E74C3C;
        }

        .badge-due-soon {
            background-color: rgba(241, 196, 15, 0.1);
            color: #F39C12;
        }

        .badge-new {
            background-color: rgba(52, 152, 219, 0.1);
            color: #3498DB;
        }

        .badge-reservation {
            background-color: rgba(39, 174, 96, 0.1);
            color: #27AE60;
        }

        .action-btn {
            padding: 0.5rem 1rem;
            background-color: var(--primary);
            color: white;
            border: none;
            border-radius: var(--radius);
            cursor: pointer;
            font-size: 0.9rem;
            transition: var(--transition);
            display: inline-flex;
            align-items: center;
            gap: 0.3rem;
        }

        .action-btn:hover {
            background-color: var(--primary-dark);
            transform: translateY(-2px);
        }
    </style>
</head>
<body>
    <h2>Alerts & Notifications</h2>

    <c:if test="${not empty errorMessage}">
        <p style="color: red;">${errorMessage}</p>
    </c:if>

    <c:if test="${not empty alerts}">
        <table border="1" cellpadding="5">
            <tr>
                <th>Type</th>
                <th>Title/Description</th>
                <th>Member</th>
                <th>Date</th>
            </tr>
            <c:forEach var="alert" items="${alerts}">
                <tr>
                    <td>${alert.type}</td>
                    <td>${alert.title}</td>
                    <td>${alert.memberName}</td>
                    <td>${alert.date}</td>
                </tr>
            </c:forEach>
        </table>
    </c:if>
</body>
</html>
