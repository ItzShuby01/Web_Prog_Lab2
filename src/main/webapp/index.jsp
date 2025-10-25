<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
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
                        <%-- R is Radio buttons (1, 2, 3, 4, 5) --%>
                        <div class="radio-group" id="r-button-group">
                            <input type="button" class="r-button" value="1" data-r="1.0">
                            <input type="button" class="r-button" value="2" data-r="2.0">
                            <input type="button" class="r-button" value="3" data-r="3.0">
                            <input type="button" class="r-button" value="4" data-r="4.0">
                            <input type="button" class="r-button" value="5" data-r="5.0">
                        </div>
                        <input type="hidden" id="r-param-input" name="r" value="2.0" />
                        <div class="validation-hint">Value must be one of: 1, 2, 3, 4, 5</div>
                    </div>

                    <div class="form-group">
                        <label class="x-coord-label">X Coordinate:</label>
                        <div class="radio-group" id="x-radio-group">
                            <%
                                // X is Radio buttons {-3...5}
                                for (int val = -3; val <= 5; val++) {
                                    String checked = (val == 0) ? "checked" : "";
                            %>
                            <label class="radio-label">
                                <input type="radio"
                                       name="x_radio"
                                       class="x-radio"
                                       value="<%= val %>"
                                       data-x="<%= val %>.0"
                                        <%= checked %>>
                                <%= val %>
                            </label>
                            <% } %>
                        </div>
                        <%-- Hidden input to store the selected X value for submission --%>
                        <input type="hidden" id="x-param-input" name="x" value="0.0" />
                        <div class="validation-hint">Select a value for X</div>
                    </div>

                    <div class="form-group">
                        <label class="y-coord-label" for="y-param-input">Y Coordinate:</label>
                        <%-- Y is Text field (-3...5) --%>
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

    // Set the initial R value from the input or default
    window.initialR = parseFloat(document.getElementById('r-param-input').value);
</script>
<script src="js/script.js" type="text/javascript"></script>
</body>
</html>