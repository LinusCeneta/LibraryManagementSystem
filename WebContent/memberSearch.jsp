<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Member Search</title>
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

        h1, h2 {
            color: var(--primary);
            margin-bottom: 1rem;
        }

        h1 {
            font-size: 1.8rem;
            border-bottom: 2px solid var(--accent);
            padding-bottom: 0.5rem;
        }

        .search-form {
            background-color: var(--white);
            padding: 1.5rem;
            border-radius: var(--radius);
            box-shadow: var(--shadow);
            margin-bottom: 2rem;
        }

        .form-group {
            margin-bottom: 1.5rem;
        }

        label {
            display: block;
            margin-bottom: 0.5rem;
            font-weight: 500;
            color: var(--primary-dark);
        }

        input[type="text"] {
            width: 100%;
            padding: 0.8rem 1rem;
            border: 1px solid var(--text-light);
            border-radius: var(--radius);
            font-size: 1rem;
            transition: var(--transition);
        }

        input[type="text"]:focus {
            border-color: var(--accent);
            outline: none;
            box-shadow: 0 0 0 2px rgba(200, 169, 126, 0.3);
        }

        .submit-btn {
            padding: 0.8rem 1.5rem;
            background-color: var(--accent);
            color: var(--text-dark);
            border: none;
            border-radius: var(--radius);
            cursor: pointer;
            font-weight: 500;
            transition: var(--transition);
            margin-top: 1rem;
        }

        .submit-btn:hover {
            background-color: #b8975e;
            transform: translateY(-2px);
        }

        .error-message {
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

        .results-container {
            background-color: var(--white);
            padding: 1.5rem;
            border-radius: var(--radius);
            box-shadow: var(--shadow);
            margin-top: 2rem;
        }

        .member-card {
            padding: 1rem;
            border-bottom: 1px solid var(--off-white);
            transition: var(--transition);
        }

        .member-card:hover {
            background-color: rgba(200, 169, 126, 0.1);
        }

        .member-link {
            color: var(--primary);
            text-decoration: none;
            font-weight: 500;
            transition: var(--transition);
        }

        .member-link:hover {
            color: var(--primary-dark);
            text-decoration: underline;
        }

        .member-details {
            color: var(--text-light);
            font-size: 0.9rem;
            margin-top: 0.5rem;
        }

        .no-results {
            text-align: center;
            color: var(--text-light);
            padding: 1rem;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1><i class="fas fa-search"></i> Member Search</h1>
        
        <div class="search-form">
            <form action="member-search" method="post">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <div class="form-group">
                    <label for="memberId"><i class="fas fa-id-card"></i> Search by Member ID</label>
                    <input type="text" id="memberId" name="memberId" placeholder="Enter Member ID" required>
                </div>
                <button type="submit" class="submit-btn">
                    <i class="fas fa-search"></i> Search
                </button>
            </form>
        </div>

        <c:if test="${not empty error}">
            <div class="error-message">
                <i class="fas fa-exclamation-circle"></i>
                <c:choose>
                    <c:when test="${error == 'MemberNotFound'}">
                        Member not found with ID: ${param.memberId}
                    </c:when>
                    <c:otherwise>
                        An error occurred during search.
                    </c:otherwise>
                </c:choose>
            </div>
        </c:if>

        <c:if test="${not empty members}">
            <div class="results-container">
                <h2><i class="fas fa-users"></i> Search Results</h2>
                <c:forEach var="member" items="${members}">
                    <div class="member-card">
                        <a href="MemberProfileServlet?memberId=${member.memberId}" class="member-link">
                            <i class="fas fa-user"></i> <strong>${member.name}</strong> (ID: ${member.memberId})
                        </a>
                        <div class="member-details">
                            <i class="fas fa-envelope"></i> ${member.email} | <i class="fas fa-phone"></i> ${member.phone}
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:if>

        <c:if test="${empty members and empty error and not empty param.memberId}">
            <div class="no-results">
                <i class="fas fa-info-circle"></i> No members found matching your search criteria
            </div>
        </c:if>
    </div>
</body>
</html>