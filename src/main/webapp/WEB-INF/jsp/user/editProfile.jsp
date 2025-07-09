<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>
<head>
    <title>Edit Profile - <c:out value="${currentUser.username}"/></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; margin-bottom: 5px; }
        .form-group input[type="text"],
        .form-group input[type="email"],
        .form-group input[type="tel"],
        .form-group input[type="date"],
        .form-group textarea {
            width: 100%;
            padding: 8px;
            box-sizing: border-box;
        }
        .current-photo { margin-bottom: 10px;}
        .current-photo img { max-width: 100px; max-height: 100px; border: 1px solid #ddd; }
    </style>
</head>
<body>
     <c:if test="${empty sessionScope.user || empty currentUser}">
        <c:redirect url="${pageContext.request.contextPath}/user/login?message=loginRequired"/>
    </c:if>

    <div class="container">
        <h1>Edit Profile: <c:out value="${currentUser.username}"/></h1>

        <c:if test="${not empty errorMessage}">
            <p class="error-message"><c:out value="${errorMessage}"/></p>
        </c:if>
        <c:if test="${not empty message}">
            <p class="message"><c:out value="${message}"/></p>
        </c:if>

        <form action="${pageContext.request.contextPath}/user/editProfile" method="post" enctype="multipart/form-data">
            <input type="hidden" name="_csrf" value="${_csrf}">
            <input type="hidden" name="userID" value="${currentUser.userID}">

            <fieldset>
                <legend>Basic Information</legend>
                <div class="form-group">
                    <label for="username">Username:</label>
                    <input type="text" id="username" name="username" value="<c:out value="${currentUser.username}"/>" readonly>
                    <small>(Username cannot be changed)</small>
                </div>
                <div class="form-group">
                    <label for="email">Email:</label>
                    <input type="email" id="email" name="email" value="<c:out value="${currentUser.email}"/>" required>
                     <small>(Changing email might require re-verification in a full system)</small>
                </div>
                <div class="form-group">
                    <label for="firstName">First Name:</label>
                    <input type="text" id="firstName" name="firstName" value="<c:out value="${currentUser.firstName}"/>" required>
                </div>
                <div class="form-group">
                    <label for="lastName">Last Name:</label>
                    <input type="text" id="lastName" name="lastName" value="<c:out value="${currentUser.lastName}"/>" required>
                </div>
                 <div class="form-group">
                    <label for="profilePhoto">Profile Photo (Optional):</label>
                    <c:if test="${not empty currentUser.profilePhotoURL}">
                        <div class="current-photo">
                            Current: <img src="${pageContext.request.contextPath}/${currentUser.profilePhotoURL}" alt="Current Profile Photo">
                        </div>
                    </c:if>
                    <input type="file" id="profilePhoto" name="profilePhoto" accept="image/png, image/jpeg, image/gif">
                    <small>(Max 5MB. PNG, JPG, GIF)</small>
                </div>
            </fieldset>

            <fieldset>
                <legend>Contact &amp; Other Details</legend>
                <div class="form-group">
                    <label for="phoneNumber">Phone Number:</label>
                    <input type="tel" id="phoneNumber" name="phoneNumber" value="<c:out value="${currentUser.userProfile.phoneNumber}"/>">
                </div>
                <div class="form-group">
                    <label for="addressLine1">Address Line 1:</label>
                    <input type="text" id="addressLine1" name="addressLine1" value="<c:out value="${currentUser.userProfile.addressLine1}"/>">
                </div>
                <div class="form-group">
                    <label for="addressLine2">Address Line 2:</label>
                    <input type="text" id="addressLine2" name="addressLine2" value="<c:out value="${currentUser.userProfile.addressLine2}"/>">
                </div>
                <div class="form-group">
                    <label for="city">City:</label>
                    <input type="text" id="city" name="city" value="<c:out value="${currentUser.userProfile.city}"/>">
                </div>
                <div class="form-group">
                    <label for="stateProvince">State/Province:</label>
                    <input type="text" id="stateProvince" name="stateProvince" value="<c:out value="${currentUser.userProfile.stateProvince}"/>">
                </div>
                <div class="form-group">
                    <label for="postalCode">Postal Code:</label>
                    <input type="text" id="postalCode" name="postalCode" value="<c:out value="${currentUser.userProfile.postalCode}"/>">
                </div>
                <div class="form-group">
                    <label for="country">Country:</label>
                    <input type="text" id="country" name="country" value="<c:out value="${currentUser.userProfile.country}"/>">
                </div>
                 <div class="form-group">
                    <label for="birthDate">Birth Date:</label>
                    <input type="date" id="birthDate" name="birthDate" value="<fmt:formatDate value='${currentUser.userProfile.birthDate}' pattern='yyyy-MM-dd'/>">
                </div>
            </fieldset>

            <fieldset>
                <legend>Emergency Contact (Optional)</legend>
                 <div class="form-group">
                    <label for="emergencyContactName">Name:</label>
                    <input type="text" id="emergencyContactName" name="emergencyContactName" value="<c:out value="${currentUser.userProfile.emergencyContactName}"/>">
                </div>
                 <div class="form-group">
                    <label for="emergencyContactPhone">Phone:</label>
                    <input type="tel" id="emergencyContactPhone" name="emergencyContactPhone" value="<c:out value="${currentUser.userProfile.emergencyContactPhone}"/>">
                </div>
            </fieldset>

            <p>
                <button type="submit">Save Changes</button>
                <a href="${pageContext.request.contextPath}/user/profile" class="button">Cancel</a>
            </p>
        </form>
    </div>
</body>
</html>
