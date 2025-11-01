<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ page import="web.model.CalculationResult" %>
<%@ page import="web.model.ResultManager" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Web_ProgrammingLab 2</title>
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
    <div class="split-container">
        <div class="form-section">
            <div class="card form-card">
                <%-- Form uses GET method and submits to the ControllerServlet --%>
                <form id="data-form" action="controller" method="GET">
                    <h3 class="form-header card-title">Parameters</h3>
                    <input type="hidden" name="source" value="form">

                <div class="form-group">
                    <label class="r-param-label">R Parameter:</label>
                    <%-- R is standard Radio buttons (1, 2, 3, 4, 5) --%>
                    <div class="radio-group" id="r-radio-group">
                        <%
                            // Iterate over the R values
                            for (int rVal = 1; rVal <= 5; rVal++) {
                                // Default check '2' as it was the previous hidden value
                                String checked = (rVal == 2) ? "checked" : "";
                        %>
                        <label class="radio-label" for="r_radio_<%= rVal %>">
                            <input type="radio"
                                   id="r_radio_<%= rVal %>"
                                   name="r"         <%-- 'name="r"' submits the selected R value --%>
                                   class="r-radio-input"
                                   value="<%= rVal %>.0"
                                    <%= checked %>>
                            <%= rVal %>
                        </label>
                        <% } %>
                    </div>
                    <div class="validation-hint">Select a value for R (1, 2, 3, 4, 5)</div>
                </div>

                <div class="form-group">
                    <label class="x-coord-label">X Coordinate:</label>
                    <div class="radio-group" id="x-radio-group">
                        <%
                            // X is standard Radio buttons {-3...5}
                            for (int val = -3; val <= 5; val++) {
                                String checked = (val == 0) ? "checked" : "";
                        %>
                        <label class="radio-label" for="x_radio_<%= val %>">
                            <input type="radio"
                                   id="x_radio_<%= val %>"
                                   name="x"        <%-- 'name="x"' submits the selected X value --%>
                                   class="x-radio-input"
                                   value="<%= val %>.0"
                                    <%= checked %>>
                            <%= val %>
                        </label>
                        <% } %>
                    </div>
                    <div class="validation-hint">Select a value for X (-3 to 5)</div>
                </div>

                <div class="form-group">
                    <label class="y-coord-label" for="y-param-input">Y Coordinate:</label>
                    <%-- Y remains a Text field (-3...5) --%>
                    <input type="text" id="y-param-input" name="y" placeholder="Enter Y value" value="0" />
                    <div class="validation-hint">Value must be between -3 and 5</div>
                </div>

                <button id="form-btn" type="submit">Check Point</button>
                <%-- Display server-side error from AreaCheckServlet --%>
                <p id="error" class="error-message ${!empty validationError ? 'visible' : ''}">
                    ${validationError}
                </p>
            </form>
        </div>

        <div class="live-info card">
            <h3 class="live-info-header card-title">Live Data</h3>
            <div class="live-info-details">
                <p>Current Time: <span id="curr-time">Loading...</span></p>
            </div>
        </div>
    </div>

    <div class="results-section">
        <div class="img-div card">
            <h3 class="card-title">Graph</h3>
            <canvas id="graph" width="400" height="400"></canvas>
        </div>

        <%-- RESULTS TABLE AND CLEAR BUTTON --%>
        <div class="results-div card">
            <div class="results-div-header">
                <h3 class="results-header card-title">Results Table</h3>
                <%-- Specific action to clear results, points to the ControllerServlet --%>
                <form action="controller" method="GET" style="display:inline;">
                    <input type="hidden" name="clear_results" value="true" />
                    <button id="clear-results" type="submit">Clear Table</button>
                </form>
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
                        <%-- Loop through the session resultsList to populate the table --%>
                        <c:forEach var="res" items="${resultsList}">
                            <tr>
                                <td>${res.r()}</td>
                                <td>${res.x()}</td>
                                <td>${res.y()}</td>
                                <td>${res.timestamp()}</td>
                                <td>
                                    <%-- Format the number for clean display --%>
                                    <fmt:formatNumber value="${res.executionTimeNanos() / 1000000.0}" maxFractionDigits="2" /> ms
                                </td>
                                <td>${res.hit() ? 'Hit' : 'Miss'}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

</main>
<footer></footer>
<%-- Pass session data to JavaScript for graph drawing --%>
<script>
    // Initialize the points array using data from the session
   window.initialPoints = [];
       <c:forEach var="res" items="${resultsList}">
           window.initialPoints.push({
               x: ${res.x()}, // Using record-style accessors
               y: ${res.y()},
               hit: ${res.hit()}
           });
       </c:forEach>

    // Get the initial R value from the currently CHECKED radio button
    const initialRRadio = document.querySelector('input[name="r"]:checked');
    window.initialR = initialRRadio ? parseFloat(initialRRadio.value) : 2.0;
</script>
<script src="js/script.js" type="text/javascript"></script>
</body>
</html>