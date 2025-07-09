<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Supplier Management</title>
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

        .success {
            color: #27AE60;
            background-color: rgba(39, 174, 96, 0.1);
            padding: 0.8rem;
            border-radius: var(--radius);
            margin-bottom: 1rem;
            border-left: 4px solid #27AE60;
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
            background-color: var(--primary);
            color: white;
            border: none;
            border-radius: var(--radius);
            cursor: pointer;
            transition: var(--transition);
            font-size: 0.9rem;
            text-decoration: none;
            display: inline-block;
            margin-right: 0.5rem;
        }

        .action-btn:hover {
            background-color: var(--primary-dark);
            transform: translateY(-2px);
        }

        .delete-btn {
            background-color: #E74C3C;
        }

        .delete-btn:hover {
            background-color: #C0392B;
        }

        .add-btn {
            padding: 0.8rem 1.5rem;
            background-color: var(--accent);
            color: var(--text-dark);
            border: none;
            border-radius: var(--radius);
            cursor: pointer;
            font-weight: 500;
            transition: var(--transition);
            text-decoration: none;
            display: inline-block;
            margin-bottom: 1rem;
        }

        .add-btn:hover {
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
            display: inline-block;
        }

        .back-btn:hover {
            background-color: var(--primary-dark);
            transform: translateY(-2px);
        }

        /* Form elements */
        input[type="submit"] {
            padding: 0.5rem 1rem;
            border: none;
            border-radius: var(--radius);
            cursor: pointer;
            transition: var(--transition);
            font-size: 0.9rem;
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
        <h2><i class="fas fa-truck"></i> Supplier Management</h2>

        <c:if test="${not empty successMessage}">
            <div class="success">
                <i class="fas fa-check-circle"></i> ${successMessage}
            </div>
            <c:remove var="successMessage" scope="session" />
        </c:if>

        <a href="${pageContext.request.contextPath}/suppliers/add" class="add-btn">
            <i class="fas fa-plus"></i> Add New Supplier
        </a>

        <h3><i class="fas fa-list"></i> Supplier List</h3>
        <c:choose>
            <c:when test="${not empty suppliers}">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Name</th>
                            <th>Contact</th>
                            <th>Phone</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="supplier" items="${suppliers}">
                            <tr>
                                <td>${supplier.supplierId}</td>
                                <td>${supplier.name}</td>
                                <td>${supplier.contactPerson}</td>
                                <td>${supplier.phone}</td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/suppliers/edit?supplierId=${supplier.supplierId}" class="action-btn">
                                        <i class="fas fa-edit"></i> Edit
                                    </a>
                                    <form action="${pageContext.request.contextPath}/suppliers" method="post" style="display:inline;">
                                        <input type="hidden" name="supplierId" value="${supplier.supplierId}" />
                                        <input type="hidden" name="csrfToken" value="${csrfToken}" />
                                        <input type="hidden" name="action" value="delete" />
                                        <button type="submit" class="action-btn delete-btn" onclick="return confirm('Delete this supplier?');">
                                            <i class="fas fa-trash"></i> Delete
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
                    <i class="fas fa-box-open"></i> No suppliers found
                </div>
            </c:otherwise>
        </c:choose>

        <a href="dashboard.jsp" class="back-btn">
            <i class="fas fa-arrow-left"></i> Back to Dashboard
        </a>
    </div>
</body>
</html>