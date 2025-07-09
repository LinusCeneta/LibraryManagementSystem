<%@ page isErrorPage="true" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Oops! We Goofed Up</title>
    <style>
        body {
            font-family: 'Comic Sans MS', cursive, sans-serif;
            background-color: #ffe6e6;
            text-align: center;
            padding: 50px;
        }
        .container {
            background-color: white;
            border-radius: 20px;
            padding: 30px;
            box-shadow: 0 0 15px #ff9999;
        }
        h1 {
            color: #ff3333;
        }
        .emoji {
            font-size: 5em;
            animation: bounce 2s infinite;
        }
        @keyframes bounce {
            0%, 100% { transform: translateY(0); }
            50% { transform: translateY(-20px); }
        }
        .error-details {
            background-color: #fff0f0;
            padding: 15px;
            border-radius: 10px;
            margin: 20px 0;
            font-family: monospace;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="emoji">🤦‍♂️💥🤷‍♀️</div>
        <h1>Well, this is awkward...</h1>
        
        <p>Our code monkeys have been notified and are currently:</p>
        <ul style="list-style-type: none; padding: 0;">
            <li>☑️ Panicking</li>
            <li>☑️ Blaming each other</li>
            <li>☑️ Googling the error</li>
            <li>☑️ Considering career changes</li>
        </ul>
        
        <div class="error-details">
            <p><strong>What happened:</strong> <%= exception != null ? exception.getMessage() : "Something mysterious" %></p>
            <p><strong>Technical jargon:</strong> ${pageContext.errorData.statusCode}</p>
        </div>
        
        <p>Try these highly technical solutions:</p>
        <ol style="text-align: left; display: inline-block;">
            <li>Turn it off and on again</li>
            <li>Sacrifice a rubber duck to the coding gods</li>
            <li>Wait 5 minutes and pretend it never happened</li>
        </ol>
        
        <p style="margin-top: 30px;">
            <a href="/" style="background-color: #ff6666; color: white; padding: 10px 20px; border-radius: 10px; text-decoration: none;">
                Take me home before I break something else!
            </a>
        </p>
        
        <p style="font-size: 0.8em; color: #999;">
            P.S. If you're a developer, we're not really sorry. You know how this goes.
        </p>
    </div>
</body>
</html>