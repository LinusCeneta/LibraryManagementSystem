<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.UUID" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    // Generate CSRF token
    String csrfToken = UUID.randomUUID().toString();
    session.setAttribute("csrfToken", csrfToken);
%>
<html>
<head>
    <title>Library Login</title>
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
        
        .row input {
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
        
        .row input:focus {
            box-shadow: 0 0 10px rgba(93, 78, 109, 0.3);
        }
        
        .pass {
            text-align: right;
            margin-bottom: 20px;
        }
        
        .pass a {
            color: #5d4e6d;
            text-decoration: none;
            font-size: 0.9em;
            transition: all 0.3s ease;
        }
        
        .pass a:hover {
            text-decoration: underline;
            color: #423a4d;
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
        
        .signup-link {
            text-align: center;
            margin-top: 25px;
            color: #2a2630;
        }
        
        .signup-link a {
            color: #5d4e6d;
            text-decoration: none;
            font-weight: 600;
            transition: all 0.3s ease;
        }
        
        .signup-link a:hover {
            text-decoration: underline;
            color: #423a4d;
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
        <div class="title">Library Login</div>
        <form action="login" method="post">
            <input type="hidden" name="csrfToken" value="<%= csrfToken %>" />
            
            <div class="row">
                <i class="fas fa-user"></i>
                <input type="text" name="username" placeholder="Email or Phone" required>
            </div>
            
            <div class="row">
                <i class="fas fa-lock"></i>
                <input type="password" name="password" placeholder="Password" required>
            </div>
            
            <div class="pass">
                <a href="forgotpassword.jsp">Forgot password?</a>
            </div>
            
            <div class="row button">
                <input type="submit" value="Login">
            </div>
            
            <div class="signup-link">
                Not a member? <a href="register.jsp">Register now</a>
            </div>
        </form>
    </div>
    
    <c:if test="${not empty errorMessage}">
        <script>
            alert("${errorMessage}");
        </script>
    </c:if>
</body>
</html>