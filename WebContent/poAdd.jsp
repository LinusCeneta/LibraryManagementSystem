<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Purchase Orders</title>
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
            display: grid;
            grid-template-columns: 150px 1fr;
            align-items: center;
            gap: 1rem;
        }

        label {
            font-weight: 500;
            color: var(--primary-dark);
        }

        input[type="text"],
        input[type="number"],
        input[type="date"],
        select {
            padding: 0.8rem 1rem;
            border: 1px solid var(--text-light);
            border-radius: var(--radius);
            font-size: 1rem;
            transition: var(--transition);
        }

        input:focus, select:focus {
            border-color: var(--accent);
            outline: none;
            box-shadow: 0 0 0 2px rgba(200, 169, 126, 0.3);
        }

        /* Tables */
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

        .submit-btn {
            padding: 0.8rem 1.5rem;
            background-color: var(--accent);
            color: var(--text-dark);
            border: none;
            border-radius: var(--radius);
            cursor: pointer;
            font-weight: 500;
            transition: var(--transition);
            grid-column: 2;
            width: fit-content;
        }

        .submit-btn:hover {
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
            margin-top: 1rem;
        }

        .back-btn:hover {
            background-color: var(--primary-dark);
            transform: translateY(-2px);
        }

        /* Status badges */
        .status-badge {
            padding: 0.3rem 0.6rem;
            border-radius: 1rem;
            font-size: 0.8rem;
            font-weight: 500;
            display: inline-block;
        }

        .status-created {
            background-color: #E3F2FD;
            color: #0D47A1;
        }

        .status-submitted {
            background-color: #FFF8E1;
            color: #FF8F00;
        }

        .status-partially-received {
            background-color: #E0F7FA;
            color: #00838F;
        }

        .status-fully-received {
            background-color: #E8F5E9;
            color: #2E7D32;
        }

        .status-closed {
            background-color: #F5F5F5;
            color: #424242;
        }

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
<h2>Add New Purchase Order</h2>

<c:if test="${not empty error}">
    <p class="error">${error}</p>
</c:if>

<form action="${pageContext.request.contextPath}/po/add" method="post">
    <input type="hidden" name="csrfToken" value="${csrfToken}">
    PO Number: <input type="text" name="poNumber" value="${formData.poNumber}" required/><br/>
    Supplier ID: <input type="number" name="supplierId" value="${formData.supplierId}" required min="1" /><br/>
    Created Date: <input type="date" name="createdDate" value="${formData.createdDate}" required /><br/>
    Expected Delivery: <input type="date" name="expectedDeliveryDate" value="${formData.expectedDeliveryDate}" required /><br/>
    <button type="submit">Create PO</button>
</form>
<script>
function confirmAdd() {
    console.log("Add form submitting via POST");
    return true;
}
</script>
<a href="${pageContext.request.contextPath}/po/purchaseorder">Back to List</a>
</body>
</html>