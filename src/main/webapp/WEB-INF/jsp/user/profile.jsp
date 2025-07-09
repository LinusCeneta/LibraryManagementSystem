<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>
<head>
    <title>User Profile - <c:out value="${currentUser.username}"/></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .profile-container { display: flex; }
        .profile-photo { margin-right: 20px; }
        .profile-photo img { max-width: 150px; max-height: 150px; border: 1px solid #ddd; border-radius: 4px; }
        .profile-details table th { text-align: right; width: 150px; }
    </style>
</head>
<body>
    <c:if test="${empty sessionScope.user}">
        <%-- Redirect to login if session is lost or user not logged in --%>
        <c:redirect url="${pageContext.request.contextPath}/user/login?message=loginRequired"/>
    </c:if>
    <c:if test="${empty currentUser}">
        <%-- currentUser should be set by servlet, if not, something is wrong or session data might be stale --%>
         <c:set var="currentUser" value="${sessionScope.user}" scope="page"/>
    </c:if>


    <div class="container">
        <h1>User Profile: <c:out value="${currentUser.username}"/></h1>

        <c:if test="${not empty message}">
            <p class="message"><c:out value="${message}"/></p>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <p class="error-message"><c:out value="${errorMessage}"/></p>
        </c:if>

        <div class="profile-container">
            <div class="profile-photo">
                <c:choose>
                    <c:when test="${not empty currentUser.profilePhotoURL}">
                        <%-- Assuming profilePhotoURL is relative to context path or a full URL --%>
                        <%-- For local file system paths (like UPLOAD_DIR), you'd need a servlet to serve images --%>
                        <img src="${pageContext.request.contextPath}/${currentUser.profilePhotoURL}" alt="Profile Photo">
                    </c:when>
                    <c:otherwise>
                        <img src="${pageContext.request.contextPath}/images/default-avatar.png" alt="Default Profile Photo">
                         <%-- Make sure to have a default-avatar.png in webapp/images/ --%>
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="profile-details">
                <table class="details-table">
                    <tr><th>Username:</th><td><c:out value="${currentUser.username}"/></td></tr>
                    <tr><th>Email:</th><td><c:out value="${currentUser.email}"/></td></tr>
                    <tr><th>First Name:</th><td><c:out value="${currentUser.firstName}"/></td></tr>
                    <tr><th>Last Name:</th><td><c:out value="${currentUser.lastName}"/></td></tr>
                    <tr><th>Role:</th><td><c:out value="${currentUser.role.roleName}"/></td></tr>
                    <tr><th>Date Registered:</th><td><fmt:formatDate value="${currentUser.dateRegistered}" pattern="yyyy-MM-dd HH:mm:ss"/></td></tr>
                    <tr><th>Last Login:</th><td><fmt:formatDate value="${currentUser.lastLoginDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td></tr>
                    <c:if test="${not empty currentUser.userProfile}">
                        <tr><th colspan="2" style="text-align:left; background-color:#eee; padding-top:10px; padding-bottom:10px;">Contact Information</th></tr>
                        <tr><th>Phone Number:</th><td><c:out value="${currentUser.userProfile.phoneNumber}"/></td></tr>
                        <tr><th>Address:</th><td><c:out value="${currentUser.userProfile.fullAddress}"/></td></tr>
                        <tr><th>Birth Date:</th><td><fmt:formatDate value="${currentUser.userProfile.birthDate}" pattern="yyyy-MM-dd"/></td></tr>
                        <tr><th colspan="2" style="text-align:left; background-color:#eee; padding-top:10px; padding-bottom:10px;">Emergency Contact</th></tr>
                        <tr><th>Name:</th><td><c:out value="${currentUser.userProfile.emergencyContactName}"/></td></tr>
                        <tr><th>Phone:</th><td><c:out value="${currentUser.userProfile.emergencyContactPhone}"/></td></tr>
                    </c:if>
                </table>
            </div>
        </div>

        <p style="margin-top: 20px;">
            <a href="${pageContext.request.contextPath}/user/editProfile" class="button">Edit Profile</a>
            <a href="${pageContext.request.contextPath}/user/changePassword" class="button">Change Password</a>
        </p>

        <p><a href="${pageContext.request.contextPath}/index.jsp">Back to Home</a></p>
    </div>
</body>
</html>
