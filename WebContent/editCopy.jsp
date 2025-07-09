<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Edit Copy</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <script>
document.addEventListener('DOMContentLoaded', function() {
    const form = document.querySelector('form');
    form.addEventListener('submit', function(e) {
        console.log("Form submitting to:", form.action);
        console.log("Form method:", form.method);
        console.log("Copy ID:", document.querySelector('input[name="copyId"]').value);
        
        // Optional: Add validation here
        // if (!valid) { e.preventDefault(); }
    });
});
</script>
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
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
        }

        .container {
            max-width: 800px;
            width: 100%;
        }

        h2 {
            color: var(--primary);
            font-size: 1.8rem;
            border-bottom: 2px solid var(--accent);
            padding-bottom: 0.5rem;
            margin-bottom: 1.5rem;
            text-align: center;
        }

        .form-container {
            background-color: var(--white);
            padding: 2rem;
            border-radius: var(--radius);
            box-shadow: var(--shadow);
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
        input[type="number"],
        input[type="date"],
        select {
            width: 100%;
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

        .btn-group {
            display: flex;
            gap: 1rem;
            justify-content: flex-end;
            margin-top: 2rem;
        }

        .btn {
            padding: 0.8rem 1.5rem;
            border: none;
            border-radius: var(--radius);
            cursor: pointer;
            font-weight: 500;
            transition: var(--transition);
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 0.5rem;
            text-decoration: none;
        }

        .btn-primary {
            background-color: var(--accent);
            color: var(--text-dark);
        }

        .btn-primary:hover {
            background-color: #b8975e;
            transform: translateY(-2px);
        }

        .btn-secondary {
            background-color: var(--text-light);
            color: white;
        }

        .btn-secondary:hover {
            background-color: var(--primary-dark);
            transform: translateY(-2px);
        }

        .status-message {
            padding: 0.8rem;
            border-radius: var(--radius);
            margin-bottom: 1rem;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        .error {
            background-color: rgba(231, 76, 60, 0.1);
            border-left: 4px solid #E74C3C;
            color: #E74C3C;
        }

        .success {
            background-color: rgba(39, 174, 96, 0.1);
            border-left: 4px solid #27AE60;
            color: #27AE60;
        }

        .readonly-field {
            background-color: var(--off-white);
            cursor: not-allowed;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="form-container">
        <div class="header">
            <h2>Edit Copy</h2>
        </div>
        
        <c:if test="${not empty error}">
            <div class="alert alert-danger" role="alert">
                ${error}
            </div>
        </c:if>
        
        <form action="${pageContext.request.contextPath}/copies/edit" method="post">
            <input type="hidden" name="copyId" value="${copy.copyId}">
            
            <div class="mb-3">
                <label for="copyIdDisplay" class="form-label">Copy ID</label>
                <input type="text" class="form-control" id="copyIdDisplay" 
                       value="${copy.copyId}" readonly>
                <small class="text-muted">Copy ID cannot be changed</small>
            </div>
            
            <div class="mb-3">
                <label for="isbn" class="form-label">ISBN</label>
                <input type="text" class="form-control" id="isbn" 
                       name="isbn" value="${copy.isbn}" required>
            </div>
            
            <div class="mb-3">
                <label for="acquisitionDate" class="form-label">Acquisition Date</label>
                <fmt:formatDate var="formattedDate" value="${copy.acquisitionDate}" pattern="yyyy-MM-dd" />
                <input type="date" class="form-control" id="acquisitionDate" 
                       name="acquisitionDate" value="${formattedDate}" required>
            </div>
            
            <div class="mb-3">
                <label for="cost" class="form-label">Cost</label>
                <input type="number" step="0.01" class="form-control" id="cost" 
                       name="cost" value="${copy.cost}" required>
            </div>
            
            <div class="mb-3">
                <label for="condition" class="form-label">Condition</label>
                <select class="form-select" id="condition" name="condition" required>
                    <option value="NEW" ${copy.condition == 'NEW' ? 'selected' : ''}>New</option>
                    <option value="GOOD" ${copy.condition == 'GOOD' ? 'selected' : ''}>Good</option>
                    <option value="FAIR" ${copy.condition == 'FAIR' ? 'selected' : ''}>Fair</option>
                    <option value="DAMAGED" ${copy.condition == 'DAMAGED' ? 'selected' : ''}>Damaged</option>
                </select>
            </div>
            
            <div class="mb-3">
                <label for="location" class="form-label">Location</label>
                <input type="text" class="form-control" id="location" 
                       name="location" value="${copy.location}" required>
            </div>
            
            <div class="mb-3">
                <label for="status" class="form-label">Status</label>
                <select class="form-select" id="status" name="status" required>
                    <option value="AVAILABLE" ${copy.status == 'AVAILABLE' ? 'selected' : ''}>Available</option>
                    <option value="CHECKED_OUT" ${copy.status == 'CHECKED_OUT' ? 'selected' : ''}>Checked Out</option>
                    <option value="ON_HOLD" ${copy.status == 'ON_HOLD' ? 'selected' : ''}>On Hold</option>
                    <option value="LOST" ${copy.status == 'LOST' ? 'selected' : ''}>Lost</option>
                    <option value="UNDER_REPAIR" ${copy.status == 'UNDER_REPAIR' ? 'selected' : ''}>Under Repair</option>
                </select>
            </div>
            
            <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                <a href="${pageContext.request.contextPath}/copies" class="btn btn-secondary me-md-2">Cancel</a>
                <button type="submit" class="btn btn-primary">Save Changes</button>
            </div>
        </form>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
document.addEventListener('DOMContentLoaded', function() {
    const form = document.querySelector('form');
    form.addEventListener('submit', function(e) {
        console.log("Form submitting to:", form.action);
        console.log("Form method:", form.method);
        console.log("Copy ID:", "${copy.copyId}");
    });
});
</script>
</body>
</html>