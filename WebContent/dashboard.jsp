<%@ page session="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    String role = (String) session.getAttribute("role");
    if (role == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    String username = (String) session.getAttribute("username");
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Library Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard.css">
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
            padding: 2rem;
        }

        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 2rem;
            padding-bottom: 1rem;
            border-bottom: 1px solid var(--text-light);
        }

        .welcome-message {
            color: var(--primary);
        }

        .welcome-message h2 {
            margin-bottom: 0.5rem;
        }

        .user-role {
            background-color: var(--accent);
            color: var(--text-dark);
            padding: 0.3rem 0.8rem;
            border-radius: var(--radius);
            font-size: 0.9rem;
            font-weight: 500;
        }

        .nav-links {
            display: flex;
            gap: 1rem;
        }

        .nav-links a {
            color: var(--primary);
            text-decoration: none;
            transition: var(--transition);
            padding: 0.5rem 1rem;
            border-radius: var(--radius);
        }

        .nav-links a:hover {
            background-color: rgba(93, 78, 109, 0.1);
        }

        .dashboard-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
            gap: 1.5rem;
            margin-top: 2rem;
        }

        .section {
            background-color: var(--white);
            padding: 1.5rem;
            border-radius: var(--radius);
            box-shadow: var(--shadow);
            transition: var(--transition);
        }

        .section:hover {
            transform: translateY(-5px);
            box-shadow: 0 6px 16px rgba(93, 78, 109, 0.15);
        }

        .section h3 {
            color: var(--primary);
            margin-bottom: 1.2rem;
            padding-bottom: 0.5rem;
            border-bottom: 2px solid var(--accent);
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        .section ul {
            list-style: none;
        }

        .section li {
            margin-bottom: 0.8rem;
        }

        .section a {
            color: var(--text-dark);
            text-decoration: none;
            transition: var(--transition);
            display: flex;
            align-items: center;
            gap: 0.5rem;
            padding: 0.3rem 0;
        }

        .section a:hover {
            color: var(--primary);
            padding-left: 0.5rem;
        }

        .section a i {
            color: var(--accent);
            width: 1.2rem;
            text-align: center;
        }
    </style>
</head>
<body>
    <div class="header">
        <div class="welcome-message">
            <h2>Welcome, <%= username %>!</h2>
            <span class="user-role"><%= role %></span>
        </div>
        <div class="nav-links">
            <a href="profile.jsp"><i class="fas fa-user"></i> My Profile</a>
            <a href="index.jsp"><i class="fas fa-home"></i> Home</a>
            <a href="logout.jsp"><i class="fas fa-sign-out-alt"></i> Logout</a>
        </div>
    </div>

    <div class="dashboard-grid">
        <c:if test="${role == 'ROLE_ADMIN'}">
            <!-- Admin Dashboard -->
            <div class="section">
                <h3><i class="fas fa-tachometer-alt"></i> Admin Dashboard</h3>
                <ul>
                    <li><a href="activityMembersReport.jsp"><i class="fas fa-clipboard-list"></i> User Activity Logs</a></li>
                    <li><a href="alertsNotifications.jsp"><i class="fas fa-bell"></i> Alerts & Notifications</a></li>
                    <li><a href="staffTasks.jsp"><i class="fas fa-tasks"></i> Staff Task Assignments</a></li>
                </ul>
            </div>

            <div class="section">
                <h3><i class="fas fa-users"></i> User Management</h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/register"><i class="fas fa-user-plus"></i> Register User</a></li>
                    <li><a href="${pageContext.request.contextPath}/register-member"><i class="fas fa-user-edit"></i> Register Member</a></li>
                    <li><a href="${pageContext.request.contextPath}/member-search"><i class="fas fa-search"></i> Search Members</a></li>
                    <li><a href="${pageContext.request.contextPath}/member-profile"><i class="fas fa-id-card"></i> Member Profiles</a></li>
                    <li><a href="${pageContext.request.contextPath}/membership-renewal"><i class="fas fa-sync-alt"></i> Renew Memberships</a></li>
                    <li><a href="${pageContext.request.contextPath}/member-history"><i class="fas fa-history"></i> View Member History</a></li>
                </ul>
            </div>

            <div class="section">
                <h3><i class="fas fa-book"></i> Book Management</h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/book-list"><i class="fas fa-list"></i> Book List</a></li>
                    <li><a href="${pageContext.request.contextPath}/book-add"><i class="fas fa-plus-circle"></i> Add Book</a></li>
                    <li><a href="${pageContext.request.contextPath}/book-edit"><i class="fas fa-edit"></i> Edit Book</a></li>
                    <li><a href="${pageContext.request.contextPath}/book-delete"><i class="fas fa-trash-alt"></i> Delete Book</a></li>
                    <li><a href="${pageContext.request.contextPath}/catalog"><i class="fas fa-book-open"></i> Catalog Management</a></li>
                </ul>
            </div>

            <div class="section">
                <h3><i class="fas fa-truck"></i> Procurement</h3>
                <ul>
                    <li><a href="supplierForm.jsp"><i class="fas fa-parachute-box"></i> Suppliers</a></li>
                    <li><a href="${pageContext.request.contextPath}/purchase-request"><i class="fas fa-file-signature"></i> Purchase Requests</a></li>
                    <li><a href="${pageContext.request.contextPath}/po/purchaseorder"><i class="fas fa-file-invoice-dollar"></i> Purchase Orders</a></li>
                    <li><a href="receiving.jsp"><i class="fas fa-dolly"></i> Receiving Inventory</a></li>
                </ul>
            </div>

            <div class="section">
                <h3><i class="fas fa-chart-bar"></i> Reports</h3>
                <ul>
                    <li><a href="activeInactiveReport.jsp"><i class="fas fa-user-clock"></i> Member Status</a></li>
                    <li><a href="fineReport.jsp"><i class="fas fa-money-bill-wave"></i> Fine Revenue</a></li>
                    <li><a href="holdReport.jsp"><i class="fas fa-bookmark"></i> Hold Requests</a></li>
                    <li><a href="inventoryReport.jsp"><i class="fas fa-boxes"></i> Inventory Status</a></li>
                    <li><a href="circulationReport.jsp"><i class="fas fa-exchange-alt"></i> Circulation Trends</a></li>
                    <li><a href="adhocReport.jsp"><i class="fas fa-question-circle"></i> Ad-Hoc Reports</a></li>
                </ul>
            </div>
        </c:if>

        <c:if test="${role == 'ROLE_STAFF'}">
            <div class="section">
                <h3><i class="fas fa-exchange-alt"></i> Loans & Returns</h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/book-list"><i class="fas fa-book"></i> View Book List</a></li>
                    <li><a href="${pageContext.request.contextPath}/catalog"><i class="fas fa-search"></i> Catalog Search</a></li>
                    <li><a href="loan.jsp"><i class="fas fa-hand-holding"></i> Issue Loans</a></li>
                    <li><a href="return.jsp"><i class="fas fa-undo"></i> Return Books</a></li>
                    <li><a href="renewal.jsp"><i class="fas fa-redo"></i> Renew Loans</a></li>
                    <li><a href="hold.jsp"><i class="fas fa-bookmark"></i> Manage Holds</a></li>
                </ul>
            </div>

            <div class="section">
                <h3><i class="fas fa-boxes"></i> Inventory</h3>
                <ul>
                    <li><a href="inventoryAdjustments.jsp"><i class="fas fa-sliders-h"></i> Adjust Inventory</a></li>
                    <li><a href="receiving.jsp"><i class="fas fa-dolly"></i> Receive Books</a></li>
                </ul>
            </div>

            <div class="section">
                <h3><i class="fas fa-money-bill-wave"></i> Fines</h3>
                <ul>
                    <li><a href="fine.jsp"><i class="fas fa-calculator"></i> Manage Fines</a></li>
                </ul>
            </div>

            <div class="section">
                <h3><i class="fas fa-chart-line"></i> Reports</h3>
                <ul>
                    <li><a href="circulationReport.jsp"><i class="fas fa-exchange-alt"></i> Circulation</a></li>
                    <li><a href="inventoryReport.jsp"><i class="fas fa-box-open"></i> Inventory</a></li>
                    <li><a href="holdReport.jsp"><i class="fas fa-bookmark"></i> Holds</a></li>
                    <li><a href="fineReport.jsp"><i class="fas fa-coins"></i> Fines</a></li>
                </ul>
            </div>
        </c:if>

        <c:if test="${role == 'ROLE_MEMBER'}">
            <div class="section">
                <h3><i class="fas fa-book"></i> Catalog</h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/book-list"><i class="fas fa-search"></i> Search Catalog</a></li>
                    <li><a href="hold.jsp"><i class="fas fa-bookmark"></i> Place Hold</a></li>
                </ul>
            </div>

            <div class="section">
                <h3><i class="fas fa-user"></i> My Library</h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/member-history"><i class="fas fa-history"></i> Borrowing History</a></li>
                    <li><a href="${pageContext.request.contextPath}/membership-renewal"><i class="fas fa-sync-alt"></i> Renew Membership</a></li>
                </ul>
            </div>
        </c:if>
    </div>
</body>
</html>