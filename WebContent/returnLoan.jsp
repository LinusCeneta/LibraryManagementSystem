<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Return Loan</title>
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
            max-width: 600px;
            margin: 0 auto;
        }

        h2 {
            color: var(--primary);
            font-size: 1.8rem;
            border-bottom: 2px solid var(--accent);
            padding-bottom: 0.5rem;
            margin-bottom: 1.5rem;
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
            text-align: center;
        }

        .back-btn:hover {
            background-color: var(--primary-dark);
            transform: translateY(-2px);
        }

        /* Status messages */
        .status-message {
            padding: 0.8rem;
            border-radius: var(--radius);
            margin-bottom: 1rem;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        .success {
            background-color: rgba(39, 174, 96, 0.1);
            border-left: 4px solid #27AE60;
            color: #27AE60;
        }

        .error {
            background-color: rgba(231, 76, 60, 0.1);
            border-left: 4px solid #E74C3C;
            color: #E74C3C;
        }
    </style>
</head>
<body>
    <div class="container">
        <h2><i class="fas fa-book-return"></i> Return Loan</h2>

        <c:if test="${not empty successMessage}">
            <div class="status-message success">
                <i class="fas fa-check-circle"></i> ${successMessage}
            </div>
        </c:if>

        <c:if test="${not empty errorMessage}">
            <div class="status-message error">
                <i class="fas fa-exclamation-circle"></i> ${errorMessage}
            </div>
        </c:if>

        <div class="form-container">
            <form action="ReturnLoanServlet" method="post">
                <div class="form-group">
                    <label for="copyId"><i class="fas fa-barcode"></i> Copy ID</label>
                    <input type="text" id="copyId" name="copyId" required>
                </div>

                <div class="form-group">
                    <label for="staffId"><i class="fas fa-user-tie"></i> Staff ID</label>
                    <input type="text" id="staffId" name="staffId" required>
                </div>

                <div class="action-buttons">
                    <button type="submit" class="submit-btn">
                        <i class="fas fa-check"></i> Process Return
                    </button>
                    <a href="dashboard.jsp" class="back-btn">
                        <i class="fas fa-arrow-left"></i> Back to Dashboard
                    </a>
                </div>
            </form>
        </div>
    </div>
</body>
</html>