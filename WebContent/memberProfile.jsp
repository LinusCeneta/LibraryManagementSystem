<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Member Profile</title>
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

        .profile-header {
            border-bottom: 2px solid var(--accent);
            padding-bottom: 0.5rem;
            margin-bottom: 1.5rem;
        }

        h1 {
            color: var(--primary);
            font-size: 1.8rem;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        .profile-card {
            background-color: var(--white);
            padding: 1.5rem;
            border-radius: var(--radius);
            box-shadow: var(--shadow);
            margin-bottom: 2rem;
        }

        .profile-details {
            display: grid;
            grid-template-columns: max-content 1fr;
            gap: 1rem;
            align-items: center;
        }

        .profile-detail-label {
            font-weight: 500;
            color: var(--primary-dark);
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        .profile-detail-value {
            color: var(--text-dark);
        }

        .status-badge {
            padding: 0.3rem 0.6rem;
            border-radius: 1rem;
            font-size: 0.8rem;
            font-weight: 500;
            display: inline-block;
        }

        .status-active {
            background-color: #E8F5E9;
            color: #2E7D32;
        }

        .status-inactive {
            background-color: #F8D7DA;
            color: #721C24;
        }

        .status-pending {
            background-color: #FFF3CD;
            color: #856404;
        }

        .action-links {
            display: flex;
            gap: 1rem;
            margin-top: 2rem;
        }

        .action-btn {
            padding: 0.8rem 1.5rem;
            background-color: var(--accent);
            color: var(--text-dark);
            border: none;
            border-radius: var(--radius);
            cursor: pointer;
            font-weight: 500;
            transition: var(--transition);
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 0.5rem;
        }

        .action-btn:hover {
            background-color: #b8975e;
            transform: translateY(-2px);
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

        .success-message {
            background-color: rgba(39, 174, 96, 0.1);
            border-left: 4px solid #27AE60;
            color: #27AE60;
            padding: 0.8rem;
            border-radius: var(--radius);
            margin-bottom: 1.5rem;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="profile-header">
            <h1><i class="fas fa-user-circle"></i> Member Profile</h1>
        </div>

        <c:if test="${not empty param.success}">
            <div class="success-message">
                <i class="fas fa-check-circle"></i> Member registered successfully!
            </div>
        </c:if>

        <div class="profile-card">
            <div class="profile-details">
                <span class="profile-detail-label"><i class="fas fa-user"></i> Name:</span>
                <span class="profile-detail-value">${member.name}</span>

                <span class="profile-detail-label"><i class="fas fa-id-card"></i> Member ID:</span>
                <span class="profile-detail-value">${member.memberId}</span>

                <span class="profile-detail-label"><i class="fas fa-envelope"></i> Email:</span>
                <span class="profile-detail-value">${member.email}</span>

                <span class="profile-detail-label"><i class="fas fa-phone"></i> Phone:</span>
                <span class="profile-detail-value">${member.phone}</span>

                <span class="profile-detail-label"><i class="fas fa-users"></i> Membership Type:</span>
                <span class="profile-detail-value">${member.membershipType}</span>

                <span class="profile-detail-label"><i class="fas fa-info-circle"></i> Status:</span>
                <span class="profile-detail-value">
                    <span class="status-badge status-${member.status.toLowerCase()}">
                        ${member.status}
                    </span>
                </span>
            </div>
        </div>

        <div class="action-links">
            <a href="register-member" class="action-btn">
                <i class="fas fa-user-plus"></i> Register New Member
            </a>
            <a href="memberSearch.jsp" class="back-btn">
                <i class="fas fa-arrow-left"></i> Back to Search
            </a>
        </div>
    </div>
</body>
</html>