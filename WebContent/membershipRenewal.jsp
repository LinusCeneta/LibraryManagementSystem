<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Renew Membership</title>
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
            text-align: center;
        }

        .container {
            max-width: 600px;
            width: 100%;
        }

        h1 {
            color: var(--primary);
            font-size: 2rem;
            margin-bottom: 2rem;
            padding-bottom: 0.5rem;
            border-bottom: 2px solid var(--accent);
        }

        .message-container {
            background-color: var(--white);
            padding: 2rem;
            border-radius: var(--radius);
            box-shadow: var(--shadow);
            margin-bottom: 2rem;
        }

        .success {
            color: #27AE60;
            background-color: rgba(39, 174, 96, 0.1);
            padding: 1rem;
            border-radius: var(--radius);
            border-left: 4px solid #27AE60;
        }

        .error {
            color: #E74C3C;
            background-color: rgba(231, 76, 60, 0.1);
            padding: 1rem;
            border-radius: var(--radius);
            border-left: 4px solid #E74C3C;
        }

        .back-btn {
            padding: 0.8rem 1.5rem;
            background-color: var(--primary);
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
    </style>
</head>
<body>
    <div class="container">
        <div class="message-container ${message.contains('successfully') ? 'success' : 'error'}">
            <h1>
                <i class="fas ${message.contains('successfully') ? 'fa-check-circle' : 'fa-exclamation-circle'}"></i>
                ${message}
            </h1>
        </div>
        <a href="memberSearch.jsp" class="back-btn">
            <i class="fas fa-arrow-left"></i> Back to Search
        </a>
    </div>
</body>
</html>