<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>User Activity Monitoring</title>
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

        h2, h3 {
            color: var(--primary);
            margin-bottom: 1rem;
        }

        h2 {
            font-size: 1.8rem;
            border-bottom: 2px solid var(--accent);
            padding-bottom: 0.5rem;
        }

        h3 {
            font-size: 1.4rem;
            margin-top: 2rem;
        }

        .error {
            color: #E74C3C;
            background-color: rgba(231, 76, 60, 0.1);
            padding: 0.8rem;
            border-radius: var(--radius);
            margin-bottom: 1rem;
            border-left: 4px solid #E74C3C;
        }

        /* Search Box */
        .search-box {
            background-color: var(--white);
            padding: 1.5rem;
            border-radius: var(--radius);
            box-shadow: var(--shadow);
            margin-bottom: 2rem;
        }

        .search-box form {
            display: flex;
            gap: 1rem;
            align-items: center;
        }

        .search-box input[type="text"] {
            flex: 1;
            padding: 0.8rem 1rem;
            border: 1px solid var(--primary-light);
            border-radius: var(--radius);
            font-size: 1rem;
        }

        .search-box button {
            padding: 0.8rem 1.5rem;
            background-color: var(--accent);
            color: var(--text-dark);
            border: none;
            border-radius: var(--radius);
            cursor: pointer;
            font-weight: 500;
            transition: var(--transition);
        }

        .search-box button:hover {
            background-color: #b8975e;
            transform: translateY(-2px);
        }

        /* Tables */
        table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 2rem;
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

        /* Action Buttons */
        .action-btn {
            padding: 0.5rem 1rem;
            background-color: #E74C3C;
            color: white;
            border: none;
            border-radius: var(--radius);
            cursor: pointer;
            transition: var(--transition);
            font-size: 0.9rem;
        }

        .action-btn:hover {
            background-color: #C0392B;
            transform: translateY(-2px);
        }

        /* Export Buttons */
        .export-buttons {
            display: flex;
            gap: 1rem;
            margin: 2rem 0;
        }

        .export-btn {
            padding: 0.8rem 1.5rem;
            background-color: var(--primary);
            color: white;
            border: none;
            border-radius: var(--radius);
            cursor: pointer;
            transition: var(--transition);
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        .export-btn:hover {
            background-color: var(--primary-dark);
            transform: translateY(-2px);
        }

        /* Pagination */
        .pagination {
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 1rem;
            margin-top: 2rem;
        }

        .pagination a {
            padding: 0.5rem 1rem;
            background-color: var(--primary);
            color: white;
            border-radius: var(--radius);
            text-decoration: none;
            transition: var(--transition);
        }

        .pagination a:hover {
            background-color: var(--primary-dark);
        }

        .pagination span {
            font-weight: 500;
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
        <h2><i class="fas fa-user-shield"></i> User Activity Monitoring</h2>

        <c:if test="${not empty errorMessage}">
            <div class="error">
                <i class="fas fa-exclamation-circle"></i> ${errorMessage}
            </div>
        </c:if>

        <!-- Search Form -->
        <div class="search-box">
            <h3><i class="fas fa-search"></i> Search Activities</h3>
            <form action="userActivityMonitoring" method="get">
                <input type="text" name="searchQuery" placeholder="Search by username, action, or IP..." value="${param.searchQuery}">
                <button type="submit"><i class="fas fa-search"></i> Search</button>
                <button type="button" onclick="window.location.href='userActivityMonitoring'"><i class="fas fa-undo"></i> Reset</button>
            </form>
        </div>

        <!-- Active Sessions Table -->
        <h3><i class="fas fa-user-clock"></i> Active Sessions</h3>
        <c:choose>
            <c:when test="${not empty activeSessions}">
                <table>
                    <thead>
                        <tr>
                            <th>Username</th>
                            <th>Session ID</th>
                            <th>IP Address</th>
                            <th>Login Time</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="activity" items="${activeSessions}">
                            <tr>
                                <td>${activity.username}</td>
                                <td>${activity.sessionId}</td>
                                <td>${activity.ipAddress}</td>
                                <td>${activity.timestamp}</td>
                                <td>
                                    <form action="terminateSession" method="post" style="display: inline;">
                                        <input type="hidden" name="sessionId" value="${activity.sessionId}">
                                        <button type="submit" class="action-btn" onclick="return confirm('Terminate this session?')">
                                            <i class="fas fa-power-off"></i> Terminate
                                        </button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <div class="no-data">
                    <i class="fas fa-user-slash"></i> No active sessions found
                </div>
            </c:otherwise>
        </c:choose>

        <!-- Recent Staff Actions Table -->
        <h3><i class="fas fa-history"></i> Recent Staff Actions (Last 7 Days)</h3>
        <c:choose>
            <c:when test="${not empty recentActions}">
                <table>
                    <thead>
                        <tr>
                            <th>Username</th>
                            <th>Action</th>
                            <th>IP Address</th>
                            <th>Timestamp</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="activity" items="${recentActions}">
                            <tr>
                                <td>${activity.username}</td>
                                <td>${activity.action}</td>
                                <td>${activity.ipAddress}</td>
                                <td>${activity.timestamp}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <div class="no-data">
                    <i class="fas fa-info-circle"></i> No recent actions found
                </div>
            </c:otherwise>
        </c:choose>

        <!-- Suspicious Activities Table -->
        <h3><i class="fas fa-exclamation-triangle"></i> Suspicious Activities (Last 24 Hours)</h3>
        <c:choose>
            <c:when test="${not empty suspiciousActivities}">
                <table>
                    <thead>
                        <tr>
                            <th>Username</th>
                            <th>Action</th>
                            <th>IP Address</th>
                            <th>Timestamp</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="activity" items="${suspiciousActivities}">
                            <tr>
                                <td>${activity.username}</td>
                                <td>${activity.action}</td>
                                <td>${activity.ipAddress}</td>
                                <td>${activity.timestamp}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <div class="no-data">
                    <i class="fas fa-check-circle"></i> No suspicious activities found
                </div>
            </c:otherwise>
        </c:choose>

        <!-- Export Buttons -->
        <div class="export-buttons">
            <form action="exportActivities" method="get">
                <button type="submit" name="format" value="csv" class="export-btn">
                    <i class="fas fa-file-csv"></i> Export to CSV
                </button>
                <button type="submit" name="format" value="pdf" class="export-btn">
                    <i class="fas fa-file-pdf"></i> Export to PDF
                </button>
            </form>
        </div>

        <!-- Pagination -->
        <div class="pagination">
            <c:if test="${currentPage > 1}">
                <a href="userActivityMonitoring?page=${currentPage - 1}&searchQuery=${param.searchQuery}">
                    <i class="fas fa-chevron-left"></i> Previous
                </a>
            </c:if>
            <span>Page ${currentPage} of ${totalPages}</span>
            <c:if test="${currentPage < totalPages}">
                <a href="userActivityMonitoring?page=${currentPage + 1}&searchQuery=${param.searchQuery}">
                    Next <i class="fas fa-chevron-right"></i>
                </a>
            </c:if>
        </div>
    </div>
</body>
</html>