<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page import="web.model.CalculationResult" %>
<%@ page import="web.model.ResultManager" %>
<%@ page import="java.util.List" %>
<%
    // Load all results from the HTTP Session
    List<CalculationResult> resultsList = ResultManager.getResults(request.getSession());
    request.setAttribute("resultsList", resultsList);
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Results - Web Programming Lab 2</title>
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
                <h3 class="results-header card-title">Results Table</h3>
                <%-- Specific action to clear results --%>
                <form action="controller" method="GET" style="display:inline;">
                    <input type="hidden" name="clear_results" value="true" />
                    <button id="clear-results" type="submit">Clear Table</button>
                </form>
            </div>
            <div class="results-table-container">
                <%-- Table with previous results from HTTP Session --%>
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
                    <%
                        // The list is guaranteed to be non-null by ResultManager.getResults()
                        for (CalculationResult res : resultsList) {
                            double execTimeMs = res.getExecutionTimeNanos() / 1_000_000.0; // Convert nanos to ms for display
                            String result = res.isHit() ? "Hit" : "Miss";
                    %>
                    <tr>
                        <td><%= res.getR() %></td>
                        <td><%= res.getX() %></td>
                        <td><%= res.getY() %></td>
                        <td><%= res.getTimestamp().toString() %></td>
                        <td><%= String.format("%.2f ms", execTimeMs) %></td>
                        <td><%= result %></td>
                    </tr>
                    <% } %>
                    </tbody>
                </table>
            </div>
        </div>

        <div class="navigation-card card">
            <a href="index.jsp" class="go-back-link">
                <button id="go-back-btn">Go Back</button>
            </a>
        </div>
    </div>
</main>

<footer></footer>

</body>
</html>