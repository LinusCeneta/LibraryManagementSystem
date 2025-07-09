<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Member Registration</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 600px; margin: 20px auto; padding: 20px; }
        .form-group { margin-bottom: 15px; }
        label { display: inline-block; width: 150px; font-weight: bold; }
        input, select { 
            padding: 8px; 
            width: 300px; 
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        .submit-btn { 
            padding: 10px 20px; 
            background: #4CAF50; 
            color: white; 
            border: none; 
            border-radius: 4px;
            cursor: pointer; 
            margin-top: 20px;
            font-weight: bold;
        }
        .error { color: #f44336; }
        .success { color: #4CAF50; }
    </style>    
</head>
<body>
    <h2>Member Registration Form</h2>
    
    <c:if test="${not empty successMessage}">
        <div class="success">${successMessage}</div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="error">${errorMessage}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/register-member" method="post">
        <input type="hidden" name="csrfToken" value="${csrfToken}">
        
        <div class="form-group">
            <label for="name">Full Name:</label>
            <input type="text" id="name" name="name" value="${member.name}" required>
        </div>
        
        <div class="form-group">
            <label for="email">Email:</label>
            <input type="email" id="email" name="email" value="${member.email}" required>
        </div>
        
        <div class="form-group">
            <label for="phone">Phone:</label>
            <input type="tel" id="phone" name="phone" value="${member.phone}" required>
        </div>
        
        <div class="form-group">
            <label for="memberId">Member ID:</label>
            <input type="text" id="memberId" name="memberId" value="${member.memberId}" required>
        </div>
        
        <div class="form-group">
            <label for="membershipType">Membership Type:</label>
            <select id="membershipType" name="membershipType" required>
                <option value="Member" ${member.membershipType eq 'Member' ? 'selected' : ''}>Member</option>
                <option value="Staff" ${member.membershipType eq 'Staff' ? 'selected' : ''}>Staff</option>
                <option value="Admin" ${member.membershipType eq 'Admin' ? 'selected' : ''}>Admin</option>
            </select>
        </div>
        
        <div class="form-group">
            <label for="status">Status:</label>
            <select id="status" name="status" required>
                <option value="Active" ${member.status eq 'Active' ? 'selected' : ''}>Active</option>
                <option value="Inactive" ${member.status eq 'Inactive' ? 'selected' : ''}>Inactive</option>
                <option value="Pending" ${member.status eq 'Pending' ? 'selected' : ''}>Pending</option>
            </select>
        </div>
        
        <div class="form-group">
            <button type="submit" class="submit-btn">Register Member</button>
        </div>
    </form>
</body>
</html>