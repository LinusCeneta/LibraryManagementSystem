<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Place Hold</title>
</head>
<body>
    <h2>Place Hold</h2>
    <form action="HoldServlet" method="post">
        Member ID: <input type="text" name="memberId" required><br><br>
        Copy ID: <input type="text" name="copyId" required><br><br>
        <input type="submit" value="Place Hold">
    </form>
</body>
</html>
