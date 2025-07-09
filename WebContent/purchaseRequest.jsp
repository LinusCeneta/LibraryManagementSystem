<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Purchase Requests</title>
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

        .form-container {
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

        input[type="text"],
        input[type="email"] {
            width: 100%;
            padding: 0.8rem 1rem;
            border: 1px solid var(--text-light);
            border-radius: var(--radius);
            font-size: 1rem;
            transition: var(--transition);
        }

        input[type="text"]:focus,
        input[type="email"]:focus {
            border-color: var(--accent);
            outline: none;
            box-shadow: 0 0 0 2px rgba(200, 169, 126, 0.3);
        }

        .action-buttons {
            display: flex;
            gap: 1rem;
            margin-top: 1.5rem;
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
        }

        .submit-btn:hover {
            background-color: #b8975e;
            transform: translateY(-2px);
        }

        .cancel-btn {
            padding: 0.8rem 1.5rem;
            background-color: var(--text-light);
            color: white;
            border: none;
            border-radius: var(--radius);
            cursor: pointer;
            transition: var(--transition);
            text-decoration: none;
            display: inline-block;
            text-align: center;
        }

        .cancel-btn:hover {
            background-color: var(--primary-dark);
            transform: translateY(-2px);
        }
    </style>
</head>
<body>
<h2>Purchase Requests </h2>


<!-- Display success/error messages -->
<c:if test="${not empty successMessage}">
    <div style="color: green;">${successMessage}</div>
    <c:remove var="successMessage" scope="session"/>
</c:if>
<c:if test="${not empty errorMessage}">
    <div style="color: red;">${errorMessage}</div>
    <c:remove var="errorMessage" scope="session"/>
</c:if>

<a href="${pageContext.request.contextPath}/purchase-request-add">Create New Request</a>

<h3>Requests List</h3>
<table border="1">
    <tr>
        <th>ID</th>
        <th>Title</th>
        <th>Requested By</th>
        <th>Date</th>
        <th>Status</th>
        <th>Actions</th>
    </tr>
    <c:forEach var="request" items="${requests}">
        <tr>
            <td>${request.requestId}</td>
            <td>${request.title}</td>
            <td>${request.requestedBy}</td>
            <td>${request.requestDate}</td>
            <td>${request.status}</td>
            <td>
               <!-- Status Update Form -->
<form id="updateForm" action="${pageContext.request.contextPath}/purchase-request-update" method="post" onsubmit="return confirmUpdate()">
    <input type="hidden" name="_method" value="POST" />
    <input type="hidden" name="requestId" value="${request.requestId}" />
    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />
    <select name="status">
        <option value="Pending" ${request.status == 'Pending' ? 'selected' : ''}>Pending</option>
        <option value="Approved" ${request.status == 'Approved' ? 'selected' : ''}>Approved</option>
        <option value="Rejected" ${request.status == 'Rejected' ? 'selected' : ''}>Rejected</option>
    </select>
    <input type="submit" value="Update Status" />
</form>

<c:if test="${request.status == 'Approved'}">
    <form action="${pageContext.request.contextPath}/p0/add" method="get" style="display:inline;">
        <input type="hidden" name="requestId" value="${request.requestId}" />
        <input type="submit" value="Convert to PO" />
    </form>
</c:if>

<!-- Delete Form -->
<form id="deleteForm" action="${pageContext.request.contextPath}/purchase-request-delete" method="post" onsubmit="return confirmDelete()">
    <input type="hidden" name="_method" value="POST" />
    <input type="hidden" name="requestId" value="${request.requestId}" />
    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />
    <input type="submit" value="Delete" />
</form>

<script>
function confirmUpdate() {
    console.log("Update form submitting via POST");
    return true;
}

function confirmDelete() {
    console.log("Delete form submitting via POST");
    return confirm('Are you sure you want to delete this request?');
}
</script>
            </td>
        </tr>
    </c:forEach>
</table>

<a href="${pageContext.request.contextPath}/index.jsp">Back to Dashboard</a>
</body>
</html>