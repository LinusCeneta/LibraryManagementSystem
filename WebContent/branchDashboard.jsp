<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Branch Overview Dashboard</title>
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
            font-size: 2rem;
            border-bottom: 2px solid var(--accent);
            padding-bottom: 0.5rem;
            margin-bottom: 1.5rem;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        h3 {
            color: var(--primary-dark);
            font-size: 1.5rem;
            margin: 1.5rem 0 1rem;
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

        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 1rem;
            margin-bottom: 2rem;
        }

        .stat-card {
            background-color: var(--white);
            padding: 1.5rem;
            border-radius: var(--radius);
            box-shadow: var(--shadow);
            transition: var(--transition);
            border-left: 4px solid var(--accent);
        }

        .stat-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 6px 16px rgba(93, 78, 109, 0.15);
        }

        .stat-value {
            font-size: 1.8rem;
            font-weight: 600;
            color: var(--primary);
            margin-bottom: 0.5rem;
        }

        .stat-label {
            color: var(--text-light);
            font-size: 0.9rem;
        }

        .alert-card {
            background-color: var(--white);
            padding: 1.5rem;
            border-radius: var(--radius);
            box-shadow: var(--shadow);
            margin-bottom: 2rem;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin: 1rem 0;
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

        .no-alerts {
            text-align: center;
            padding: 2rem;
            color: var(--text-light);
            background-color: var(--white);
            border-radius: var(--radius);
            box-shadow: var(--shadow);
        }

        .alert-badge {
            display: inline-block;
            padding: 0.25rem 0.5rem;
            border-radius: var(--radius);
            font-size: 0.8rem;
            font-weight: 500;
            margin-left: 0.5rem;
        }

        .alert-critical {
            background-color: rgba(231, 76, 60, 0.1);
            color: #E74C3C;
        }

        .alert-warning {
            background-color: rgba(241, 196, 15, 0.1);
            color: #F39C12;
        }
    </style>
</head>
<body>
    <h2>Branch Overview Dashboard</h2>

    <c:if test="${not empty errorMessage}">
        <p style="color: red;">${errorMessage}</p>
    </c:if>

    <c:if test="${not empty report}">
        <h3>Quick Stats</h3>
        <ul>
            <li>Total Items: ${report.totalItems}</li>
            <li>Available: ${report.availableItems}</li>
            <li>Checked Out: ${report.checkedOutItems}</li>
            <li>On Hold: ${report.onHoldItems}</li>
            <li>Daily Checkouts: ${report.dailyCheckouts}</li>
            <li>Daily Returns: ${report.dailyReturns}</li>
            <li>Overdue Count: ${report.overdueCount}</li>
        </ul>

        <h3>Low Inventory Alerts</h3>
        <c:if test="${not empty report.lowInventoryAlerts}">
            <table border="1" cellpadding="5">
                <thead>
                    <tr>
                        <th>Title</th>
                        <th>Copies Remaining</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="entry" items="${report.lowInventoryAlerts}">
                        <tr>
                            <td>${entry.key}</td>
                            <td>${entry.value}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>
        <c:if test="${empty report.lowInventoryAlerts}">
            <p>No low inventory alerts.</p>
        </c:if>
    </c:if>
</body>
</html>
