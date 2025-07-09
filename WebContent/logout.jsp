<%@ page session="true" %>
<%
    // Invalidate the current session to log out the user
    session.invalidate();

    // Optionally set a logout message or feedback
    request.setAttribute("message", "You have been successfully logged out.");

    // Redirect to login page (change to your login page if different)
    response.sendRedirect("login.jsp");
%>
