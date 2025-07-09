<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${empty book.id ? 'Add New' : 'Edit'} Book</title>
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
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
        }

        .container {
            max-width: 800px;
            width: 100%;
        }

        h1 {
            color: var(--primary);
            font-size: 1.8rem;
            border-bottom: 2px solid var(--accent);
            padding-bottom: 0.5rem;
            margin-bottom: 1.5rem;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        .form-container {
            background-color: var(--white);
            padding: 2rem;
            border-radius: var(--radius);
            box-shadow: var(--shadow);
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

        label.required:after {
            content: " *";
            color: #E74C3C;
        }

        input[type="text"],
        input[type="number"],
        select,
        textarea {
            width: 100%;
            padding: 0.8rem 1rem;
            border: 1px solid var(--text-light);
            border-radius: var(--radius);
            font-size: 1rem;
            transition: var(--transition);
        }

        textarea {
            min-height: 100px;
            resize: vertical;
        }

        select[multiple] {
            height: 120px;
        }

        input:focus, select:focus, textarea:focus {
            border-color: var(--accent);
            outline: none;
            box-shadow: 0 0 0 2px rgba(200, 169, 126, 0.3);
        }

        .btn-group {
            display: flex;
            gap: 1rem;
            margin-top: 1.5rem;
        }

        .submit-btn {
            padding: 0.8rem 1.5rem;
            background-color: var(--accent);
            color: var(--text-dark);
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

        .submit-btn:hover {
            background-color: #b8975e;
            transform: translateY(-2px);
        }

        .cancel-btn {
            padding: 0.8rem 1.5rem;
            background-color: var(--text-light);
            color: white;
            border: none;
            border-radius: var(--radius);
            cursor: pointer;
            transition: var(--transition);
            text-decoration: none;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 0.5rem;
        }

        .cancel-btn:hover {
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

        .hint-text {
            font-size: 0.8rem;
            color: var(--text-light);
            margin-top: 0.3rem;
        }

        .select2-container {
            width: 100% !important;
        }
    </style>
    <link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css" rel="stylesheet" />
</head>
<body>
    <h1>${empty book.id ? 'Add New' : 'Edit'} Book</h1>
    
    <c:if test="${not empty error}">
        <div class="error">${error}</div>
    </c:if>
    
    <form action="${pageContext.request.contextPath}/book-edit" method="post">
        <input type="hidden" name="id" value="${book.id}">
        
        <div class="form-group">
            <label for="isbn" class="required">ISBN:</label>
            <input type="text" id="isbn" name="isbn" value="${book.isbn}" required 
                   pattern="\d{10,13}" title="10-13 digit ISBN">
        </div>
        
        <div class="form-group">
            <label for="title" class="required">Title:</label>
            <input type="text" id="title" name="title" value="${book.title}" required>
        </div>
        
        <div class="form-group">
            <label for="subtitle">Subtitle:</label>
            <input type="text" id="subtitle" name="subtitle" value="${book.subtitle}">
        </div>
        
        <div class="form-group">
            <label for="authors" class="required">Authors:</label>
            <select id="authors" name="authorIds" multiple required>
                <c:forEach items="${authors}" var="author">
                    <option value="${author.id}"
                        <c:if test="${fn:contains(selectedAuthorIds, author.id)}">selected</c:if>
                    >${author.name}</option>
                </c:forEach>
            </select>
        </div>
        
        <div class="form-group">
            <label for="publisher" class="required">Publisher:</label>
            <select id="publisher" name="publisherId" required>
                <option value="">-- Select Publisher --</option>
                <c:forEach items="${publishers}" var="publisher">
                    <option value="${publisher.id}"
                        <c:if test="${book.publisher != null && book.publisher.id == publisher.id}">selected</c:if>
                    >${publisher.name}</option>
                </c:forEach>
            </select>
        </div>
        
        <div class="form-group">
            <label for="categories">Categories:</label>
            <select id="categories" name="categoryIds" multiple>
                <c:forEach items="${categories}" var="category">
                    <option value="${category.id}"
                        <c:if test="${fn:contains(selectedCategoryIds, category.id)}">selected</c:if>
                    >${category.name}</option>
                </c:forEach>
            </select>
        </div>
        
        <div class="form-group">
            <label for="publicationYear" class="required">Publication Year:</label>
            <input type="number" id="publicationYear" name="publicationYear" 
                   value="${book.publicationYear}" min="1900" max="${java.time.Year.now().value}" required>
        </div>
        
        <div class="form-group">
            <label for="edition">Edition:</label>
            <input type="text" id="edition" name="edition" value="${book.edition}">
        </div>
        
        <div class="form-group">
            <label for="language">Language:</label>
            <input type="text" id="language" name="language" value="${book.language}">
        </div>
        
        <div class="form-group">
            <label for="callNumber">Call Number:</label>
            <input type="text" id="callNumber" name="callNumber" value="${book.callNumber}">
        </div>
        
        <div class="form-group">
            <label for="numberOfPages" class="required">Pages:</label>
            <input type="number" id="numberOfPages" name="numberOfPages" 
                   value="${book.numberOfPages}" min="1" required>
        </div>
        
        <div class="form-group">
            <label for="summary">Summary:</label>
            <textarea id="summary" name="summary" rows="4" cols="50">${book.summary}</textarea>
        </div>
        
        <div class="form-group">
            <label for="format" class="required">Format:</label>
            <select id="format" name="format" required>
                <option value="HARDCOVER" ${book.format == 'HARDCOVER' ? 'selected' : ''}>Hardcover</option>
                <option value="PAPERBACK" ${book.format == 'PAPERBACK' ? 'selected' : ''}>Paperback</option>
                <option value="EBOOK" ${book.format == 'EBOOK' ? 'selected' : ''}>E-book</option>
                <option value="AUDIOBOOK" ${book.format == 'AUDIOBOOK' ? 'selected' : ''}>Audiobook</option>
            </select>
        </div>
        
        <button type="submit">Save Book</button>
        <a href="${pageContext.request.contextPath}/book-list" style="margin-left: 10px;">Cancel</a>
    </form>
</body>
</html>