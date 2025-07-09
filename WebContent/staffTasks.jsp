<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Staff Task List</title>
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
            max-width: 800px;
            margin: 0 auto;
        }

        h2, h3 {
            color: var(--primary);
            margin-bottom: 1rem;
        }

        h2 {
            font-size: 1.8rem;
            border-bottom: 2px solid var(--accent);
            padding-bottom: 0.5rem;
        }

        .error {
            color: #E74C3C;
            background-color: rgba(231, 76, 60, 0.1);
            padding: 0.8rem;
            border-radius: var(--radius);
            margin-bottom: 1rem;
            border-left: 4px solid #E74C3C;
        }

        /* Task Status Cards */
        .task-cards {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 1.5rem;
            margin-top: 2rem;
        }

        .task-card {
            background-color: var(--white);
            border-radius: var(--radius);
            box-shadow: var(--shadow);
            padding: 1.5rem;
            transition: var(--transition);
            border-top: 4px solid var(--accent);
        }

        .task-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 6px 16px rgba(93, 78, 109, 0.15);
        }

        .task-title {
            font-weight: 600;
            color: var(--primary-dark);
            margin-bottom: 0.5rem;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        .task-count {
            font-size: 2rem;
            font-weight: 700;
            color: var(--primary);
            margin: 1rem 0;
        }

        .task-icon {
            color: var(--accent);
            font-size: 1.2rem;
        }

        /* Table styling (fallback if cards aren't preferred) */
        table {
            width: 100%;
            border-collapse: collapse;
            margin: 2rem 0;
            box-shadow: var(--shadow);
            background-color: var(--white);
            border-radius: var(--radius);
            overflow: hidden;
        }

        th, td {
            padding: 1rem;
            text-align: left;
            border-bottom: 1px solid var(--primary-light);
        }

        th {
            background-color: var(--primary);
            color: var(--white);
            font-weight: 500;
        }

        tr:nth-child(even) {
            background-color: var(--off-white);
        }

        tr:hover {
            background-color: rgba(200, 169, 126, 0.1);
        }

        /* No Data Message */
        .no-data {
            background-color: var(--white);
            padding: 1.5rem;
            border-radius: var(--radius);
            box-shadow: var(--shadow);
            text-align: center;
            color: var(--text-light);
        }
    </style>
</head>
<body>
    <div class="container">
        <h2><i class="fas fa-tasks"></i> Staff Task List</h2>

        <c:if test="${not empty errorMessage}">
            <div class="error">
                <i class="fas fa-exclamation-circle"></i> ${errorMessage}
            </div>
        </c:if>

        <c:choose>
            <c:when test="${not empty report}">
                <!-- Card View (modern alternative) -->
                <div class="task-cards">
                    <div class="task-card">
                        <div class="task-title">
                            <i class="fas fa-clock task-icon"></i>
                            Pending Acquisition Requests
                        </div>
                        <div class="task-count">${report.pendingAcquisitions}</div>
                        <a href="#" class="action-btn">View Details</a>
                    </div>
                    
                    <div class="task-card">
                        <div class="task-title">
                            <i class="fas fa-book task-icon"></i>
                            Books to Catalog
                        </div>
                        <div class="task-count">${report.booksToCatalog}</div>
                        <a href="#" class="action-btn">View Details</a>
                    </div>
                    
                    <div class="task-card">
                        <div class="task-title">
                            <i class="fas fa-inbox task-icon"></i>
                            Items Awaiting Check-In
                        </div>
                        <div class="task-count">${report.itemsAwaitingCheckIn}</div>
                        <a href="#" class="action-btn">View Details</a>
                    </div>
                    
                    <div class="task-card">
                        <div class="task-title">
                            <i class="fas fa-bookmark task-icon"></i>
                            Holds to Process
                        </div>
                        <div class="task-count">${report.holdsToProcess}</div>
                        <a href="#" class="action-btn">View Details</a>
                    </div>
                </div>

                <!-- Table View (original layout) -->
                <table>
                    <thead>
                        <tr>
                            <th>Task</th>
                            <th>Count</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>Pending Acquisition Requests</td>
                            <td>${report.pendingAcquisitions}</td>
                        </tr>
                        <tr>
                            <td>Books to Catalog</td>
                            <td>${report.booksToCatalog}</td>
                        </tr>
                        <tr>
                            <td>Items Awaiting Check-In</td>
                            <td>${report.itemsAwaitingCheckIn}</td>
                        </tr>
                        <tr>
                            <td>Holds to Process</td>
                            <td>${report.holdsToProcess}</td>
                        </tr>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <div class="no-data">
                    <i class="fas fa-info-circle"></i> No task data available
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>