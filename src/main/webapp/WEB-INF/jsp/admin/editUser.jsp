<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>
<head>
    <title>Admin - Edit User: <c:out value="${userToEdit.username}"/></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; margin-bottom: 5px; }
        .form-group input[type="text"],
        .form-group input[type="email"],
        .form-group input[type="tel"],
        .form-group input[type="password"],
        .form-group select {
            width: 100%;
            padding: 8px;
            box-sizing: border-box;
        }
        .password-strength-info { font-size: 0.9em; color: #666; margin-top: 5px;}
    </style>
</head>
<body>
    <c:if test="${empty sessionScope.user || sessionScope.user.role.roleName != 'ROLE_ADMIN'}">
        <c:redirect url="${pageContext.request.contextPath}/user/login?message=adminLoginRequired"/>
    </c:if>
    <c:if test="${empty userToEdit}">
        <c:redirect url="${pageContext.request.contextPath}/user/admin/listUsers?errorMessage=UserNotFoundForEdit"/>
    </c:if>

    <div class="container">
        <h1>Admin: Edit User - <c:out value="${userToEdit.username}"/></h1>

        <c:if test="${not empty errorMessage}">
            <p class="error-message"><c:out value="${errorMessage}"/></p>
        </c:if>
        <c:if test="${not empty message}">
            <p class="message"><c:out value="${message}"/></p>
        </c:if>

        <form action="${pageContext.request.contextPath}/user/admin/editUser" method="post">
            <input type="hidden" name="_csrf" value="${_csrf}">
            <input type="hidden" name="userID" value="${userToEdit.userID}">

            <fieldset>
                <legend>Account Information</legend>
                <div class="form-group">
                    <label for="username">Username:</label>
                    <input type="text" id="username" name="username" value="<c:out value="${userToEdit.username}"/>" required>
                </div>
                <div class="form-group">
                    <label for="email">Email:</label>
                    <input type="email" id="email" name="email" value="<c:out value="${userToEdit.email}"/>" required>
                </div>
                <div class="form-group">
                    <label for="roleID">Role:</label>
                    <select id="roleID" name="roleID" required>
                        <c:forEach var="role" items="${allRoles}">
                            <option value="${role.roleID}" ${userToEdit.role.roleID == role.roleID ? 'selected' : ''}>
                                <c:out value="${role.roleName}"/>
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label for="isActive">Account Active:</label>
                    <select id="isActive" name="isActive">
                        <option value="true" ${userToEdit.active ? 'selected' : ''}>Yes</option>
                        <option value="false" ${!userToEdit.active ? 'selected' : ''}>No</option>
                    </select>
                </div>
            </fieldset>

            <fieldset>
                <legend>Personal Information</legend>
                <div class="form-group">
                    <label for="firstName">First Name:</label>
                    <input type="text" id="firstName" name="firstName" value="<c:out value="${userToEdit.firstName}"/>" required>
                </div>
                <div class="form-group">
                    <label for="lastName">Last Name:</label>
                    <input type="text" id="lastName" name="lastName" value="<c:out value="${userToEdit.lastName}"/>" required>
                </div>
                 <div class="form-group">
                    <label for="phoneNumber">Phone Number (Profile):</label> <%-- Assuming this comes from UserProfile --%>
                    <input type="tel" id="phoneNumber" name="phoneNumber" value="<c:out value="${userToEdit.userProfile.phoneNumber}"/>">
                </div>
                <%-- Add more UserProfile fields here if admins should edit them e.g. address --%>
            </fieldset>

            <fieldset>
                <legend>Reset Password (Optional)</legend>
                <div class="form-group">
                    <label for="newPassword">New Password (leave blank to keep current):</label>
                    <input type="password" id="newPassword" name="newPassword">
                    <p class="password-strength-info">
                        If setting a new password: must be at least 8 characters, include uppercase, lowercase, digit, and special character.
                    </p>
                </div>
            </fieldset>

            <p>
                <button type="submit">Save Changes</button>
                <a href="${pageContext.request.contextPath}/user/admin/listUsers" class="button">Cancel</a>
            </p>
        </form>
    </div>
</body>
</html>
