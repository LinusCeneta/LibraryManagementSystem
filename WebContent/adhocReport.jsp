<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Ad-Hoc Report Builder</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        :root {
            --primary: #5d4e6d;       /* Deep purple */
            --primary-dark: #423a4d;  /* Darker purple */
            --accent: #c8a97e;       /* Warm gold */
            --text-dark: #2a2630;     /* Dark grayish purple */
            --text-light: #857e8f;    /* Light grayish purple */
            --white: #ffffff;
            --off-white: #f9f7fa;
            --radius: 8px;
            --shadow: 0 4px 12px rgba(93, 78, 109, 0.1);
            --transition: all 0.3s ease;
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: 'Segoe UI', 'Georgia', sans-serif;
        }

        body {
            background-color: var(--off-white);
            color: var(--text-dark);
            padding: 20px;
        }

        .container {
            max-width: 1200px;
            margin: 0 auto;
        }

        h2 {
            color: var(--primary);
            font-size: 1.8rem;
            border-bottom: 2px solid var(--accent);
            padding-bottom: 0.5rem;
            margin-bottom: 1.5rem;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        .status-message {
            padding: 0.8rem;
            border-radius: var(--radius);
            margin-bottom: 1.5rem;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        .error {
            background-color: rgba(231, 76, 60, 0.1);
            border-left: 4px solid #E74C3C;
            color: #E74C3C;
        }

        .form-container {
            background-color: var(--white);
            padding: 1.5rem;
            border-radius: var(--radius);
            box-shadow: var(--shadow);
            margin-bottom: 1.5rem;
        }

        .form-group {
            margin-bottom: 1.5rem;
        }

        label {
            display: block;
            margin-bottom: 0.5rem;
            font-weight: 500;
            color: var(--primary-dark);
        }

        input[type="text"],
        textarea {
            width: 100%;
            padding: 0.8rem 1rem;
            border: 1px solid var(--text-light);
            border-radius: var(--radius);
            font-size: 1rem;
            transition: var(--transition);
        }

        input:focus, textarea:focus {
            border-color: var(--accent);
            outline: none;
            box-shadow: 0 0 0 2px rgba(200, 169, 126, 0.3);
        }

        .btn-group {
            display: flex;
            gap: 1rem;
            margin-top: 1rem;
        }

        button {
            padding: 0.8rem 1.5rem;
            border: none;
            border-radius: var(--radius);
            cursor: pointer;
            font-weight: 500;
            transition: var(--transition);
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 0.5rem;
        }

        .btn-primary {
            background-color: var(--accent);
            color: var(--text-dark);
        }

        .btn-primary:hover {
            background-color: #b8975e;
            transform: translateY(-2px);
        }

        .btn-secondary {
            background-color: var(--primary);
            color: white;
        }

        .btn-secondary:hover {
            background-color: var(--primary-dark);
            transform: translateY(-2px);
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin: 1.5rem 0;
            box-shadow: var(--shadow);
            border-radius: var(--radius);
            overflow: hidden;
        }

        th, td {
            padding: 1rem;
            text-align: left;
            border-bottom: 1px solid var(--text-light);
        }

        th {
            background-color: var(--primary);
            color: white;
            font-weight: 500;
        }

        tr:nth-child(even) {
            background-color: var(--off-white);
        }

        tr:hover {
            background-color: rgba(200, 169, 126, 0.1);
        }

        .no-results {
            text-align: center;
            padding: 2rem;
            color: var(--text-light);
            background-color: var(--white);
            border-radius: var(--radius);
            box-shadow: var(--shadow);
        }

        .hint-text {
            font-size: 0.8rem;
            color: var(--text-light);
            margin-top: 0.3rem;
        }

        .example-queries {
            background-color: var(--white);
            padding: 1rem;
            border-radius: var(--radius);
            box-shadow: var(--shadow);
            margin-top: 1.5rem;
        }

        .example-queries h3 {
            margin-top: 0;
            color: var(--primary-dark);
        }

        .example-query {
            font-family: monospace;
            background-color: var(--off-white);
            padding: 0.5rem;
            border-radius: var(--radius);
            margin: 0.5rem 0;
            cursor: pointer;
        }

        .example-query:hover {
            background-color: rgba(200, 169, 126, 0.1);
        }
    </style>
</head>
<body>

<h2>Ad-Hoc Report</h2>

<c:if test="${not empty error}">
    <p style="color:red;">${error}</p>
</c:if>

<form method="post" action="adhocReport">
    <label>WHERE Clause (SQL conditions, e.g. category='Fiction'):</label><br />
    <input type="text" name="whereClause" value="${param.whereClause}" size="80" /><br /><br />
    <button type="submit">Run Report</button>
    <button type="submit" name="export" value="csv">Export CSV</button>
</form>

<c:if test="${not empty rows}">
    <table border="1" cellpadding="5" cellspacing="0">
        <thead>
            <tr>
                <c:forEach var="col" items="${columns}">
                    <th><c:out value="${col}" /></th>
                </c:forEach>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="row" items="${rows}">
                <tr>
                    <c:forEach var="col" items="${columns}">
                        <td><c:out value="${row[col]}" /></td>
                    </c:forEach>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</c:if>

</body>
</html>
