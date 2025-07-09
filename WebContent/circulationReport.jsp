<%@ page import="model.CirculationReport" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Circulation Report</title>
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

        h1 {
            color: var(--primary);
            font-size: 2rem;
            border-bottom: 2px solid var(--accent);
            padding-bottom: 0.5rem;
            margin-bottom: 1.5rem;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        .filter-container {
            background-color: var(--white);
            padding: 1.5rem;
            border-radius: var(--radius);
            box-shadow: var(--shadow);
            margin-bottom: 1.5rem;
        }

        .filter-form {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 1rem;
            align-items: flex-end;
        }

        .form-group {
            margin-bottom: 0;
        }

        label {
            display: block;
            margin-bottom: 0.5rem;
            font-weight: 500;
            color: var(--primary-dark);
        }

        select, input[type="date"] {
            width: 100%;
            padding: 0.8rem 1rem;
            border: 1px solid var(--text-light);
            border-radius: var(--radius);
            font-size: 1rem;
            transition: var(--transition);
        }

        select:focus, input[type="date"]:focus {
            border-color: var(--accent);
            outline: none;
            box-shadow: 0 0 0 2px rgba(200, 169, 126, 0.3);
        }

        .btn-group {
            display: flex;
            gap: 1rem;
            grid-column: 1 / -1;
            justify-content: flex-end;
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

        button[type="submit"] {
            background-color: var(--accent);
            color: var(--text-dark);
        }

        button[type="submit"]:hover {
            background-color: #b8975e;
            transform: translateY(-2px);
        }

        button[name="export"] {
            background-color: var(--primary);
            color: white;
        }

        button[name="export"]:hover {
            background-color: var(--primary-dark);
            transform: translateY(-2px);
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

        .no-data {
            text-align: center;
            padding: 2rem;
            color: var(--text-light);
        }

        .summary-cards {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 1rem;
            margin-bottom: 1.5rem;
        }

        .summary-card {
            background-color: var(--white);
            padding: 1.5rem;
            border-radius: var(--radius);
            box-shadow: var(--shadow);
        }

        .summary-value {
            font-size: 1.8rem;
            font-weight: 600;
            color: var(--primary);
            margin-bottom: 0.5rem;
        }

        .summary-label {
            color: var(--text-light);
            font-size: 0.9rem;
        }
</head>
<body>
<h1>Circulation Report</h1>
<form method="post" action="CirculationReportServlet">
    Branch:
    <select name="branch">
        <option value="">All Branches</option>
        <option value="Itech Branch" <%= "Itech Branch".equals(request.getAttribute("branch")) ? "selected" : "" %>>Itech Branch</option>
        <option value="PUP Main Library" <%= "PUP Main Library".equals(request.getAttribute("branch")) ? "selected" : "" %>>PUP Main Library</option>
    </select>
    Start Date: <input type="date" name="startDate" value="<%= request.getAttribute("startDate") != null ? request.getAttribute("startDate") : "" %>"/>
    End Date: <input type="date" name="endDate" value="<%= request.getAttribute("endDate") != null ? request.getAttribute("endDate") : "" %>"/>
    <button type="submit">Generate Report</button>
    <button type="submit" name="export" value="csv">Export CSV</button>
</form>

<%
    String errorMessage = (String) request.getAttribute("errorMessage");
    if (errorMessage != null) {
%>
    <p style="color:red;"><%= errorMessage %></p>
<%
    }

    List<CirculationReport> reports = (List<CirculationReport>) request.getAttribute("reports");
    if (reports != null && !reports.isEmpty()) {
%>
    <table border="1">
        <tr><th>Date</th><th>Checkout Count</th></tr>
        <% for (CirculationReport report : reports) { %>
            <tr>
                <td><%= new java.text.SimpleDateFormat("yyyy-MM-dd").format(report.getLoanDate()) %></td>
                <td><%= report.getCheckoutCount() %></td>
            </tr>
        <% } %>
    </table>
<%
    }
%>
</body>
</html>
