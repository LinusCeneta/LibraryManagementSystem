<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page session="true" %>
<%@ page import="java.util.*" %>
<%@ page import="java.util.UUID" %>
<%
    // Generate CSRF token
    String csrfToken = UUID.randomUUID().toString();
    session.setAttribute("csrfToken", csrfToken);
    System.out.println("Registration form is loaded!");
%>

<!DOCTYPE html>
<html>
<head>
    <title>Library Registration</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        
        body {
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            background: url('https://images.unsplash.com/photo-1521587760476-6c12a4b040da?ixlib=rb-1.2.1&auto=format&fit=crop&w=1350&q=80') no-repeat center center fixed;
            background-size: cover;
            position: relative;
        }
        
        body::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.5);
        }
        
        .wrapper {
            position: relative;
            width: 400px;
            background: rgba(255, 255, 255, 0.9);
            border-radius: 10px;
            box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
            padding: 40px;
            z-index: 1;
        }
        
        .title {
            font-size: 2em;
            color: #5d4e6d;
            text-align: center;
            margin-bottom: 30px;
            font-weight: 600;
        }
        
        .row {
            position: relative;
            width: 100%;
            height: 50px;
            margin-bottom: 30px;
        }
        
        .row i {
            position: absolute;
            top: 50%;
            left: 15px;
            transform: translateY(-50%);
            color: #5d4e6d;
            font-size: 1.2em;
        }
        
        .row input, .row select {
            width: 100%;
            height: 100%;
            background: #f0e9f5;
            border: none;
            outline: none;
            padding-left: 45px;
            border-radius: 5px;
            font-size: 1em;
            color: #2a2630;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
            transition: all 0.3s ease;
        }
        
        .row select {
            appearance: none;
            background-image: url("data:image/svg+xml;charset=UTF-8,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3e%3cpolyline points='6 9 12 15 18 9'%3e%3c/polyline%3e%3c/svg%3e");
            background-repeat: no-repeat;
            background-position: right 1rem center;
            background-size: 1em;
        }
        
        .row input:focus, .row select:focus {
            box-shadow: 0 0 10px rgba(93, 78, 109, 0.3);
        }
        
        .button input {
            width: 100%;
            height: 50px;
            background: #5d4e6d;
            color: white;
            border: none;
            outline: none;
            border-radius: 5px;
            font-size: 1.1em;
            font-weight: 500;
            cursor: pointer;
            transition: all 0.3s ease;
            box-shadow: 0 4px 10px rgba(93, 78, 109, 0.3);
        }
        
        .button input:hover {
            background: #423a4d;
            transform: translateY(-2px);
            box-shadow: 0 6px 15px rgba(93, 78, 109, 0.4);
        }
        
        .login-link {
            text-align: center;
            margin-top: 25px;
            color: #2a2630;
        }
        
        .login-link a {
            color: #5d4e6d;
            text-decoration: none;
            font-weight: 600;
            transition: all 0.3s ease;
        }
        
        .login-link a:hover {
            text-decoration: underline;
            color: #423a4d;
        }
        
        .error-message {
            color: #b71c1c;
            font-size: 0.9rem;
            margin-bottom: 20px;
            padding: 10px;
            background-color: #f8f0f0;
            border-left: 3px solid #b71c1c;
            border-radius: 4px;
            display: flex;
            align-items: center;
            gap: 0.7rem;
        }
        
        @media (max-width: 480px) {
            .wrapper {
                width: 90%;
                padding: 30px;
            }
            
            .title {
                font-size: 1.8em;
            }
        }
    </style>
</head>
<body>
    <div class="wrapper">
        <div class="title">Library Registration</div>
      
        
        <form method="post" action="${pageContext.request.contextPath}/register">
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />
            
            <div class="row">
                <i class="fas fa-user"></i>
                <input type="text" name="username" placeholder="Username" value="${username}" required>
            </div>
            
            <div class="row">
                <i class="fas fa-lock"></i>
                <input type="password" name="password" placeholder="Password" required>
            </div>
            
            <div class="row">
                <i class="fas fa-envelope"></i>
                <input type="email" name="email" placeholder="Email" value="${email}" required>
            </div>
            
            <div class="row">
                <i class="fas fa-user-tag"></i>
                <select name="role" required>
                    <option value="">Select Account Type</option>
                    <option value="ROLE_MEMBER" <c:if test="${role == 'ROLE_MEMBER'}">selected</c:if>>Member</option>
                    <option value="ROLE_STAFF" <c:if test="${role == 'ROLE_STAFF'}">selected</c:if>>Staff</option>
                    <option value="ROLE_ADMIN" <c:if test="${role == 'ROLE_ADMIN'}">selected</c:if>>Administrator</option>
                </select>
            </div>
            
            <div class="row button">
                <input type="submit" value="Register">
            </div>
            
            <div class="login-link">
                Already have an account? <a href="login.jsp">Login here</a>
            </div>
        </form>
    </div>
</body>
</html>