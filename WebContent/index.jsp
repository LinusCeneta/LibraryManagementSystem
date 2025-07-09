<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
    <title>Library Management System</title>
    <style>
        :root {
            --primary-color: #3498db;
            --secondary-color: #2980b9;
            --sidebar-color: #2c3e50;
            --sidebar-hover: #34495e;
            --header-color: #1a252f;
            --card-color-1: #e74c3c;
            --card-color-2: #2ecc71;
            --card-color-3: #f39c12;
            --card-color-4: #9b59b6;
            --success-color: #27ae60;
            --warning-color: #f39c12;
            --danger-color: #e74c3c;
        }
        
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        
        body {
            display: flex;
            min-height: 100vh;
            background-color: #f5f5f5;
        }
        
        .sidebar {
            width: 250px;
            background-color: var(--sidebar-color);
            color: white;
            height: 100vh;
            position: fixed;
            transition: all 0.3s;
            overflow-y: auto;
        }
        
        .sidebar-header {
            padding: 20px;
            background-color: var(--header-color);
            text-align: center;
        }
        
        .sidebar-header h3 {
            margin-bottom: 5px;
        }
        
        .sidebar-header p {
            font-size: 12px;
            color: #bdc3c7;
        }
        
        .sidebar-menu {
            padding: 20px 0;
        }
        
        .sidebar-menu ul {
            list-style: none;
        }
        
        .sidebar-menu li a {
            display: block;
            padding: 12px 20px;
            color: #ecf0f1;
            text-decoration: none;
            transition: all 0.3s;
            border-left: 3px solid transparent;
        }
        
        .sidebar-menu li a:hover {
            background-color: var(--sidebar-hover);
            padding-left: 25px;
            border-left: 3px solid var(--primary-color);
        }
        
        .sidebar-menu li a i {
            margin-right: 10px;
            width: 20px;
            text-align: center;
        }
        
        .sidebar-menu li.active a {
            background-color: var(--sidebar-hover);
            border-left: 3px solid var(--primary-color);
        }
        
        .main-content {
            margin-left: 250px;
            width: calc(100% - 250px);
            padding: 20px;
        }
        
        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 15px 20px;
            background-color: white;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
            margin-bottom: 20px;
            border-radius: 5px;
        }
        
        .user-info {
            display: flex;
            align-items: center;
        }
        
        .user-info img {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            margin-right: 10px;
        }
        
        .dashboard-cards {
            display: grid;
            grid-template-columns: repeat(4, 1fr);
            gap: 20px;
            margin-bottom: 30px;
        }
        
        .card {
            background-color: white;
            border-radius: 5px;
            padding: 20px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            position: relative;
            overflow: hidden;
            transition: transform 0.3s;
        }
        
        .card:hover {
            transform: translateY(-5px);
        }
        
        .card::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            width: 5px;
            height: 100%;
        }
        
        .card:nth-child(1)::before {
            background-color: var(--card-color-1);
        }
        
        .card:nth-child(2)::before {
            background-color: var(--card-color-2);
        }
        
        .card:nth-child(3)::before {
            background-color: var(--card-color-3);
        }
        
        .card:nth-child(4)::before {
            background-color: var(--card-color-4);
        }
        
        .card h3 {
            color: #7f8c8d;
            font-size: 14px;
            margin-bottom: 10px;
        }
        
        .card h2 {
            font-size: 24px;
            margin-bottom: 15px;
        }
        
        .card a {
            color: var(--primary-color);
            text-decoration: none;
            font-size: 12px;
            display: flex;
            align-items: center;
        }
        
        .card a i {
            margin-left: 5px;
            transition: transform 0.3s;
        }
        
        .card a:hover i {
            transform: translateX(3px);
        }
        
        .dashboard-title {
            margin-bottom: 20px;
        }
        
        .footer {
            text-align: center;
            padding: 20px;
            color: #7f8c8d;
            font-size: 12px;
            margin-top: 30px;
        }
        
        .activate-windows {
            background-color: #f1c40f;
            color: #000;
            padding: 10px;
            text-align: center;
            margin-top: 20px;
            font-size: 12px;
            border-radius: 3px;
        }
        
        /* Alert styles */
        .alert {
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 4px;
        }
        
        .alert-success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        
        .alert-danger {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        
        /* Responsive styles */
        @media (max-width: 992px) {
            .dashboard-cards {
                grid-template-columns: repeat(2, 1fr);
            }
        }
        
        @media (max-width: 768px) {
            .sidebar {
                width: 100%;
                height: auto;
                position: relative;
            }
            
            .main-content {
                margin-left: 0;
                width: 100%;
            }
            
            .dashboard-cards {
                grid-template-columns: 1fr;
            }
        }
    </style>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
</head>
<body>
    <!-- Sidebar -->
    <div class="sidebar">
        <div class="sidebar-header">
            <h3>Library Management</h3>
            <p>admin <span style="color: #2ecc71;">Online</span></p>
        </div>
        
        <div class="sidebar-menu">
            <ul>
                <li class="active"><a href="index.jsp"><i class="fas fa-tachometer-alt"></i> Dashboard</a></li>
                <li><a href="memberSearch.jsp"><i class="fas fa-users"></i> Members</a></li>
                <li><a href="bookList.jsp"><i class="fas fa-book"></i> Books</a></li>
                <li><a href="magazines.jsp"><i class="fas fa-newspaper"></i> Magazines</a></li>
                <li><a href="newspapers.jsp"><i class="fas fa-newspaper"></i> Newspapers</a></li>
                <li><a href="issueLoan.jsp"><i class="fas fa-book-reader"></i> Issue Loan</a></li>
                <li><a href="returned.jsp"><i class="fas fa-book-return"></i> Returned</a></li>
                <li><a href="notReturned.jsp"><i class="fas fa-exclamation-circle"></i> Not Returned</a></li>
                
                <!-- Reports Section -->
                <li><a href="activityMembersReport.jsp"><i class="fas fa-chart-line"></i> Activity Report</a></li>
                <li><a href="fineReport.jsp"><i class="fas fa-money-bill-wave"></i> Fine Report</a></li>
                <li><a href="circulationReport.jsp"><i class="fas fa-exchange-alt"></i> Circulation Report</a></li>
                <li><a href="adhocReport.jsp"><i class="fas fa-chart-pie"></i> Ad-Hoc Report</a></li>
                
                <!-- Administration Section -->
                <li><a href="registerMember.jsp"><i class="fas fa-user-plus"></i> Register Member</a></li>
                <li><a href="poForm.jsp"><i class="fas fa-file-invoice-dollar"></i> Purchase Orders</a></li>
                <li><a href="purchaseRequest.jsp"><i class="fas fa-clipboard-list"></i> Purchase Requests</a></li>
                <li><a href="receiving.jsp"><i class="fas fa-truck-loading"></i> Receiving</a></li>
                
                <!-- User Section -->
                <li><a href="profile.jsp"><i class="fas fa-user-cog"></i> Profile</a></li>
                <li><a href="logout.jsp"><i class="fas fa-sign-out-alt"></i> Logout</a></li>
            </ul>
        </div>
    </div>
    
    <!-- Main Content -->
    <div class="main-content">
        <div class="header">
            <h2>Library Management Control panel</h2>
            <div class="user-info">
                <img src="https://via.placeholder.com/40" alt="User">
                <span>Admin</span>
            </div>
        </div>
        
        <div class="dashboard-title">
            <h1>Admin dashboard</h1>
        </div>
        
        <!-- Display messages if any -->
        <c:if test="${not empty successMessage}">
            <div class="alert alert-success">${successMessage}</div>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger">${errorMessage}</div>
        </c:if>
        
        <div class="dashboard-cards">
            <div class="card">
                <h3>Books</h3>
                <h2>ISSUED</h2>
                <h1>2</h1>
                <a href="bookList.jsp">More info <i class="fas fa-arrow-right"></i></a>
            </div>
            
            <div class="card">
                <h3>Members</h3>
                <h2>RETURNED</h2>
                <h1>1</h1>
                <a href="memberSearch.jsp">More info <i class="fas fa-arrow-right"></i></a>
            </div>
            
            <div class="card">
                <h3>NewsPapers</h3>
                <h2>NOT RETURNED</h2>
                <h1>1</h1>
                <a href="newspapers.jsp">More info <i class="fas fa-arrow-right"></i></a>
            </div>
            
            <div class="card">
                <h3>Magazines</h3>
                <h2>DATE TODAY</h2>
                <h1>07/21/2020</h1>
                <a href="magazines.jsp">More info <i class="fas fa-arrow-right"></i></a>
            </div>
        </div>
        
        <!-- Quick Actions Section -->
        <div style="display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; margin-bottom: 30px;">
            <a href="issueLoan.jsp" class="card" style="text-align: center; text-decoration: none; color: inherit;">
                <i class="fas fa-book-reader" style="font-size: 2rem; margin-bottom: 10px; color: var(--primary-color);"></i>
                <h3>Issue New Loan</h3>
            </a>
            
            <a href="registerMember.jsp" class="card" style="text-align: center; text-decoration: none; color: inherit;">
                <i class="fas fa-user-plus" style="font-size: 2rem; margin-bottom: 10px; color: var(--success-color);"></i>
                <h3>Register Member</h3>
            </a>
            
            <a href="purchaseRequest.jsp" class="card" style="text-align: center; text-decoration: none; color: inherit;">
                <i class="fas fa-clipboard-list" style="font-size: 2rem; margin-bottom: 10px; color: var(--warning-color);"></i>
                <h3>Create Purchase Request</h3>
            </a>
        </div>
        
        <div class="activate-windows">
            Activate Windows<br>
            Go to Settings to activate Windows.
        </div>
        
        <div class="footer">
            Version 1.1.0
        </div>
    </div>
</body>
</html>