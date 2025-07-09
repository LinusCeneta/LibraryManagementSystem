<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isErrorPage="true" %>
<html>
<head>
    <title>Application Error</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <h1>An Unexpected Error Occurred</h1>
        <p>We apologize for the inconvenience. An unexpected error has occurred.</p>
        <p>Our technical team has been notified. Please try again later.</p>

        <%-- For debugging (remove or comment out in production) --%>
        <% if (exception != null) { %>
            <div style="margin-top: 20px; padding:10px; border:1px solid #ccc; background-color:#f9f9f9;">
                <h3>Error Details (for developers):</h3>
                <p><strong>Exception Type:</strong> <%= exception.getClass().getName() %></p>
                <p><strong>Message:</strong> <%= exception.getMessage() %></p>
                <h4>Stack Trace:</h4>
                <pre><% exception.printStackTrace(new java.io.PrintWriter(out)); %></pre>
            </div>
        <% } else if (pageContext.getErrorData() != null) { %>
             <div style="margin-top: 20px; padding:10px; border:1px solid #ccc; background-color:#f9f9f9;">
                <h3>Error Details (for developers):</h3>
                <p><strong>Status Code:</strong> ${pageContext.errorData.statusCode}</p>
                <p><strong>Request URI:</strong> ${pageContext.errorData.requestURI}</p>
                <p><strong>Servlet Name:</strong> ${pageContext.errorData.servletName}</p>
                <% if (pageContext.errorData.throwable != null) { %>
                    <p><strong>Exception:</strong> ${pageContext.errorData.throwable.message}</p>
                    <h4>Stack Trace:</h4>
                    <pre><% pageContext.errorData.throwable.printStackTrace(new java.io.PrintWriter(out)); %></pre>
                <% } %>
            </div>
        <% } %>

        <p><a href="${pageContext.request.contextPath}/index.jsp">Go to Homepage</a></p>
    </div>
</body>
</html>
