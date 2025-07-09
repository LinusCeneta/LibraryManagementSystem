<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Add Book</title>
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

        input:focus, select:focus, textarea:focus {
            border-color: var(--accent);
            outline: none;
            box-shadow: 0 0 0 2px rgba(200, 169, 126, 0.3);
        }

        .submit-btn {
            width: 100%;
            padding: 0.8rem;
            background-color: var(--accent);
            color: var(--text-dark);
            border: none;
            border-radius: var(--radius);
            cursor: pointer;
            font-weight: 500;
            transition: var(--transition);
            font-size: 1rem;
            margin-top: 1rem;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 0.5rem;
        }

        .submit-btn:hover {
            background-color: #b8975e;
            transform: translateY(-2px);
        }

        .back-btn {
            padding: 0.8rem 1.5rem;
            background-color: var(--text-light);
            color: white;
            border: none;
            border-radius: var(--radius);
            cursor: pointer;
            transition: var(--transition);
            text-decoration: none;
            display: inline-block;
            text-align: center;
            width: 100%;
            margin-top: 1rem;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 0.5rem;
        }

        .back-btn:hover {
            background-color: var(--primary-dark);
            transform: translateY(-2px);
        }

        .status-message {
            padding: 0.8rem;
            border-radius: var(--radius);
            margin-bottom: 1rem;
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
    </style>
</head>
<body>
    <div class="container">
        <h2><i class="fas fa-book-medical"></i> Add Book</h2>

        <c:if test="${not empty error}">
            <div class="status-message error">
                <i class="fas fa-exclamation-circle"></i> ${error}
            </div>
        </c:if>

        <div class="form-container">
            <form action="${pageContext.request.contextPath}/book-add" method="post" id="bookForm">
                <input type="hidden" name="csrfToken" value="${csrfToken}" />

                <div class="form-group">
                    <label for="title"><i class="fas fa-heading"></i> Title</label>
                    <input type="text" id="title" name="title" required />
                </div>

                <div class="form-group">
                    <label for="subtitle"><i class="fas fa-heading"></i> Subtitle</label>
                    <input type="text" id="subtitle" name="subtitle" />
                </div>

                <div class="form-group">
                    <label for="isbn"><i class="fas fa-barcode"></i> ISBN</label>
                    <input type="text" id="isbn" name="isbn" required />
                </div>

                <div class="form-group">
                    <label for="publisherName"><i class="fas fa-building"></i> Publisher</label>
                    <input type="text" id="publisherName" name="publisherName" />
                </div>

                <div class="form-group">
                    <label for="authorNames"><i class="fas fa-user-edit"></i> Authors</label>
                    <input type="text" id="authorNames" name="authorNames" />
                    <p class="hint-text">Separate multiple authors with commas</p>
                </div>

                <div class="form-group">
                    <label for="categoryNames"><i class="fas fa-tags"></i> Categories</label>
                    <input type="text" id="categoryNames" name="categoryNames" />
                    <p class="hint-text">Separate multiple categories with commas</p>
                </div>

                <div class="form-group">
                    <label for="publicationYear"><i class="fas fa-calendar-alt"></i> Publication Year</label>
                    <input type="number" id="publicationYear" name="publicationYear" min="1000" max="<%= java.time.Year.now().getValue() + 5 %>" />
                </div>

                <div class="form-group">
                    <label for="edition"><i class="fas fa-layer-group"></i> Edition</label>
                    <input type="text" id="edition" name="edition" />
                </div>

                <div class="form-group">
                    <label for="language"><i class="fas fa-language"></i> Language</label>
                    <input type="text" id="language" name="language" />
                </div>

                <div class="form-group">
                    <label for="callNumber"><i class="fas fa-hashtag"></i> Call Number</label>
                    <input type="text" id="callNumber" name="callNumber" />
                </div>

                <div class="form-group">
                    <label for="numberOfPages"><i class="fas fa-file-alt"></i> Number of Pages</label>
                    <input type="number" id="numberOfPages" name="numberOfPages" min="1" />
                </div>

                <div class="form-group">
                    <label for="summary"><i class="fas fa-align-left"></i> Summary</label>
                    <textarea id="summary" name="summary"></textarea>
                </div>

                <div class="form-group">
                    <label for="format"><i class="fas fa-book-open"></i> Format</label>
                    <select id="format" name="format" required>
                        <option value="">-- Select Format --</option>
                        <option value="HARDCOVER">Hardcover</option>
                        <option value="PAPERBACK">Paperback</option>
                        <option value="EBOOK">eBook</option>
                        <option value="AUDIOBOOK">Audiobook</option>
                    </select>
                </div>

                <button type="submit" class="submit-btn" onclick="return confirm('Are you sure you want to add this book?')">
                    <i class="fas fa-save"></i> Save Book
                </button>
                <a href="book-list" class="back-btn">
                    <i class="fas fa-arrow-left"></i> Back to List
                </a>
            </form>
        </div>
    </div>

    <script>
        document.getElementById('bookForm').addEventListener('submit', function(e) {
            console.log("Form submitting with method:", this.method);
            console.log("CSRF Token being sent:", this.csrfToken.value);
            
            // Additional validation can be added here
            // if (!valid) { e.preventDefault(); }
        });

        // Auto-format ISBN input
        document.getElementById('isbn').addEventListener('input', function(e) {
            let value = this.value.replace(/[^\dXx]/g, '');
            if (value.length > 0) {
                value = value.substring(0, 13); // Limit to ISBN-13 length
            }
            this.value = value;
        });
    </script>
</body>
</html>