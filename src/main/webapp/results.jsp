<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Current Result - Web Programming Lab 2</title>
    <link rel="stylesheet" href="css/style.css" />
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet">
</head>
<body>
<header id="header">
    <div class="header-container">
        <h1 class="main-title">Check Point On a Graph</h1>
        <p id="student-info">Student: SHUAIBU IDRIS, Group: P3231, Variant: 2463</p>
    </div>
</header>

<main id="main">
    <div class="results-page-container">
        <div class="results-div card">
            <div class="results-div-header">
                <h3 class="results-header card-title">Current Calculation Result</h3>
            </div>
            <div class="results-table-container">
                <table id="result-table">
                    <thead>
                    <tr>
                        <th>R</th>
                        <th>X</th>
                        <th>Y</th>
                        <th>Time</th>
                        <th>Execution Time</th>
                        <th>Result</th>
                    </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty currentResult}">
                                <tr>
                                    <td>${currentResult.r()}</td>
                                    <td>${currentResult.x()}</td>
                                    <td>${currentResult.y()}</td>
                                    <td>${currentResult.timestamp()}</td>
                                    <td>
                                        <%-- Format the number for clean display --%>
                                        <fmt:formatNumber value="${currentResult.executionTimeNanos() / 1000000.0}" maxFractionDigits="2" /> ms
                                    </td>
                                    <td>${currentResult.hit() ? 'Hit' : 'Miss'}</td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="6">No current result to display.</td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>

        <div class="navigation-card card">
            <%-- The ControllerServlet prepares the index page --%>
            <a href="controller" class="go-back-link">
                <button id="go-back-btn">Go Back to Main Page</button>
            </a>
        </div>
    </div>
</main>

<footer></footer>

</body>
</html>