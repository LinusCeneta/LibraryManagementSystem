<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Item" %>
<%@ page import="java.util.*" %>
<html>
<head>
    <title>Library Catalog</title>
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

        input[type="text"],
        select {
            width: 100%;
            padding: 0.8rem 1rem;
            border: 1px solid var(--text-light);
            border-radius: var(--radius);
            font-size: 1rem;
            transition: var(--transition);
        }

        input:focus, select:focus {
            border-color: var(--accent);
            outline: none;
            box-shadow: 0 0 0 2px rgba(200, 169, 126, 0.3);
        }

        .checkbox-group {
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        .checkbox-group input {
            width: auto;
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

        .btn-primary {
            background-color: var(--accent);
            color: var(--text-dark);
        }

        .btn-primary:hover {
            background-color: #b8975e;
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

        .availability {
            font-weight: 500;
        }

        .available {
            color: #27AE60;
        }

        .unavailable {
            color: #E74C3C;
        }

        .action-btn {
            padding: 0.5rem 1rem;
            background-color: var(--primary);
            color: white;
            border: none;
            border-radius: var(--radius);
            cursor: pointer;
            font-size: 0.9rem;
            transition: var(--transition);
            display: flex;
            align-items: center;
            gap: 0.3rem;
        }

        .action-btn:hover {
            background-color: var(--primary-dark);
            transform: translateY(-2px);
        }

        .action-btn:disabled {
            background-color: var(--text-light);
            cursor: not-allowed;
        }

        .no-data {
            text-align: center;
            padding: 2rem;
            color: var(--text-light);
            background-color: var(--white);
            border-radius: var(--radius);
            box-shadow: var(--shadow);
        }

        .pagination {
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 1rem;
            margin: 1.5rem 0;
        }

        .pagination a, .pagination span {
            padding: 0.5rem 1rem;
            border-radius: var(--radius);
        }

        .pagination a {
            background-color: var(--accent);
            color: var(--text-dark);
            text-decoration: none;
            transition: var(--transition);
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        .pagination a:hover {
            background-color: #b8975e;
            transform: translateY(-2px);
        }

        .pagination span {
            font-weight: 500;
        }
    </style>
</head>
<body>
<div class="clearfix">
    <div class="facets">
        <h2>Filter Results</h2>
        <form method="post" action="CatalogServlet">
            <input type="hidden" name="keyword" value="${param.keyword}">
            
            <h3>Format</h3>
            <% Map<String, List<String>> facets = (Map<String, List<String>>) request.getAttribute("facets");
               List<String> formats = facets != null ? facets.get("formats") : new ArrayList<>(); %>
            <select name="format">
                <option value="">All Formats</option>
                <% for (String format : formats) { %>
                    <option value="<%= format %>" <%= format.equals(request.getParameter("format")) ? "selected" : "" %>>
                        <%= format %>
                    </option>
                <% } %>
            </select>
            
            <h3>Genre</h3>
            <% List<String> genres = facets != null ? facets.get("genres") : new ArrayList<>(); %>
            <select name="genre">
                <option value="">All Genres</option>
                <% for (String genre : genres) { %>
                    <option value="<%= genre %>" <%= genre.equals(request.getParameter("genre")) ? "selected" : "" %>>
                        <%= genre %>
                    </option>
                <% } %>
            </select>
            
            <h3>Language</h3>
            <% List<String> languages = facets != null ? facets.get("languages") : new ArrayList<>(); %>
            <select name="language">
                <option value="">All Languages</option>
                <% for (String lang : languages) { %>
                    <option value="<%= lang %>" <%= lang.equals(request.getParameter("language")) ? "selected" : "" %>>
                        <%= lang %>
                    </option>
                <% } %>
            </select>
            
            <h3>Branch</h3>
            <% List<String> branches = facets != null ? facets.get("branches") : new ArrayList<>(); %>
            <select name="branch">
                <option value="">All Branches</option>
                <% for (String branch : branches) { 
                    String[] parts = branch.split("\\|");
                    if (parts.length == 2) { %>
                        <option value="<%= parts[0] %>" <%= parts[0].equals(request.getParameter("branch")) ? "selected" : "" %>>
                            <%= parts[1] %>
                        </option>
                    <% }
                } %>
            </select>
            
            <h3>Publication Year</h3>
            <% List<String> years = facets != null ? facets.get("years") : Arrays.asList("1900", "2023"); %>
            From: <input type="number" name="yearFrom" min="<%= years.get(0) %>" max="<%= years.get(1) %>" 
                         value="<%= request.getParameter("yearFrom") != null ? request.getParameter("yearFrom") : years.get(0) %>">
            To: <input type="number" name="yearTo" min="<%= years.get(0) %>" max="<%= years.get(1) %>" 
                       value="<%= request.getParameter("yearTo") != null ? request.getParameter("yearTo") : years.get(1) %>">
            
            <h3>Availability</h3>
            <label><input type="checkbox" name="availability" value="Available" 
                          <%= "Available".equals(request.getParameter("availability")) ? "checked" : "" %>> Only Available</label>
            
            <br><br>
            <button type="submit">Apply Filters</button>
            <a href="CatalogServlet">Reset</a>
        </form>
    </div>

    <div class="results">
        <h1>Library Catalog</h1>
        
        <!-- Featured Collections -->
        <% if (request.getAttribute("newArrivals") != null || request.getAttribute("staffPicks") != null) { %>
            <h2>Featured Collections</h2>
            
            <% if (request.getAttribute("newArrivals") != null) { %>
                <div class="collection">
                    <h3>New Arrivals</h3>
                    <table>
                        <thead>
                            <tr>
                                <th>Title</th>
                                <th>Author</th>
                                <th>Format</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (Item item : (List<Item>) request.getAttribute("newArrivals")) { %>
                                <tr>
                                    <td><%= item.getTitle() %></td>
                                    <td><%= item.getAuthor() %></td>
                                    <td><%= item.getFormat() %></td>
                                    <td>
                                        <% if (session != null && session.getAttribute("user") != null && item.getAvailableCopies() > 0) { %>
                                            <form method="post" action="HoldServlet">
                                                <input type="hidden" name="itemId" value="<%= item.getItemId() %>" />
                                                <button type="submit">Place Hold</button>
                                            </form>
                                        <% } else if (item.getAvailableCopies() == 0) { %>
                                            Unavailable
                                        <% } else { %>
                                            <em>Login to reserve</em>
                                        <% } %>
                                    </td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            <% } %>
            
            <% if (request.getAttribute("staffPicks") != null) { %>
                <div class="collection">
                    <h3>Staff Picks</h3>
                    <table>
                        <thead>
                            <tr>
                                <th>Title</th>
                                <th>Author</th>
                                <th>Format</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (Item item : (List<Item>) request.getAttribute("staffPicks")) { %>
                                <tr>
                                    <td><%= item.getTitle() %></td>
                                    <td><%= item.getAuthor() %></td>
                                    <td><%= item.getFormat() %></td>
                                    <td>
                                        <% if (session != null && session.getAttribute("user") != null && item.getAvailableCopies() > 0) { %>
                                            <form method="post" action="HoldServlet">
                                                <input type="hidden" name="itemId" value="<%= item.getItemId() %>" />
                                                <button type="submit">Place Hold</button>
                                            </form>
                                        <% } else if (item.getAvailableCopies() == 0) { %>
                                            Unavailable
                                        <% } else { %>
                                            <em>Login to reserve</em>
                                        <% } %>
                                    </td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            <% } %>
        <% } %>

        <!-- Search Form -->
        <form method="post" action="CatalogServlet">
            <input type="text" name="keyword" placeholder="Search by title, author, subject, or keywords" 
                   value="<%= request.getParameter("keyword") != null ? request.getParameter("keyword") : "" %>">
            <button type="submit">Search</button>
        </form>

        <!-- Search Results -->
        <%
            String errorMessage = (String) request.getAttribute("errorMessage");
            if (errorMessage != null) {
        %>
            <p style="color:red;"><%= errorMessage %></p>
        <%
            }
            List<Item> items = (List<Item>) request.getAttribute("items");
            if (items != null && !items.isEmpty()) {
        %>
            <h2>Search Results</h2>
            <table>
                <thead>
                    <tr>
                        <th>Title</th>
                        <th>Author</th>
                        <th>Genre</th>
                        <th>Format</th>
                        <th>Language</th>
                        <th>Year</th>
                        <th>Branch</th>
                        <th>Available Copies</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (Item item : items) { %>
                        <tr>
                            <td><%= item.getTitle() %></td>
                            <td><%= item.getAuthor() %></td>
                            <td><%= item.getGenre() %></td>
                            <td><%= item.getFormat() %></td>
                            <td><%= item.getLanguage() %></td>
                            <td><%= item.getPublicationYear() %></td>
                            <td><%= item.getBranchName() %></td>
                            <td><%= item.getAvailableCopies() %></td>
                            <td>
                                <% if (session != null && session.getAttribute("user") != null && item.getAvailableCopies() > 0) { %>
                                    <form method="post" action="HoldServlet">
                                        <input type="hidden" name="itemId" value="<%= item.getItemId() %>" />
                                        <select name="branch">
                                            <% for (String branch : branches) { 
                                                String[] parts = branch.split("\\|");
                                                if (parts.length == 2) { %>
                                                    <option value="<%= parts[0] %>"><%= parts[1] %></option>
                                                <% }
                                            } %>
                                        </select>
                                        <button type="submit">Place Hold</button>
                                    </form>
                                <% } else if (item.getAvailableCopies() == 0) { %>
                                    Unavailable
                                <% } else { %>
                                    <em>Login to reserve</em>
                                <% } %>
                            </td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        <%
            } else if (items != null) {
        %>
            <p>No items found matching your criteria.</p>
        <%
            }
        %>
    </div>
</div>
</body>
</html>