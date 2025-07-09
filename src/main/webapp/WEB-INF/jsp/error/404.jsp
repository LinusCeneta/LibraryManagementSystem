<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isErrorPage="true" %>
<html>
<head>
    <title>Error 404 - Page Not Found</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <h1>Page Not Found (Error 404)</h1>
        <p>Sorry, the page you are looking for does not exist or has been moved.</p>
        <p>Requested URI: ${pageContext.errorData.requestURI}</p>
        <p><a href="${pageContext.request.contextPath}/index.jsp">Go to Homepage</a></p>
    </div>
</body>
</html>
