package web.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web.util.Params;
import web.util.ValidationException;
import web.model.*;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@WebServlet(name = "AreaCheckServlet", value = "/check")
public class AreaCheckServlet extends HttpServlet {
    private static final String FORM_JSP = "/index.jsp";


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            //Parse the query into a map of parameters
            String query = request.getQueryString();
            Map<String, String> paramsMap = Params.splitQuery(query);

            //Choose validation strategy based on the 'source' parameter
            String source = paramsMap.get("source");
            if ("form".equals(source)) {
                // If 'source' is 'form', use the form validation
                Params.validateForm(paramsMap);
            } else {
                // for canvas clicks, use the canvas validation
                Params.validateCanvas(paramsMap);
            }

            // If validation passes, create the Params object
            Params params = new Params(paramsMap);

            // The servlet now DELEGATES the business logic to the AreaCalculator.
            var startTime = Instant.now();
            boolean hit = AreaCalculator.calculate(params.getX(), params.getY(), params.getR());
            var endTime = Instant.now();
            long executionTimeNanos = ChronoUnit.NANOS.between(startTime, endTime);
            LocalDateTime currentServerTime = LocalDateTime.now();

            // Create and Save to Session
            CalculationResult newCalcResult = new CalculationResult(
                    params.getX(), params.getY(), params.getR(), hit,
                    currentServerTime, executionTimeNanos
            );
            ResultManager.saveResult(request.getSession(), newCalcResult);

            // === Updates the  Global Stats ===
            GlobalResultManager.addResult(newCalcResult);


            // Store the current result in request scope for results.jsp
            request.getSession().setAttribute("currentResult", newCalcResult);

            //Forward to the results.jsp page -> shows only the current result -> user clicks "Go Back" to index.jsp.
            getServletContext().getRequestDispatcher("/results.jsp").forward(request, response);

            return; // Stop execution after redirect.

        } catch (ValidationException e) {
            // On validation error, set the error message and forward back to the main form page.
            request.setAttribute("validationError", e.getMessage());
        } catch (Exception e) {
            System.err.println("Server error during calculation: " + e.getMessage());
            request.setAttribute("validationError", "Internal server error: " + e.getMessage());
        }

        // Load the resultsList for index.jsp before forwarding.
        List<CalculationResult> resultsList = ResultManager.getResults(request.getSession());
        request.setAttribute("resultsList", resultsList);
        getServletContext().getRequestDispatcher(FORM_JSP).forward(request, response);
    }

}