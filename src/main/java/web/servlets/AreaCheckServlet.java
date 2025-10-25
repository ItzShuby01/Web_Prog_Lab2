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
import java.util.Map;

@WebServlet(name = "AreaCheckServlet", value = "/check")
public class AreaCheckServlet extends HttpServlet {
    private static final String FORM_JSP = "/index.jsp";
    //Path to the new results servlet.
    private static final String RESULTS_SERVLET = "/results";


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

            // On success, redirect to the results page.
            response.sendRedirect(request.getContextPath() + RESULTS_SERVLET);
            return; // Stop execution after redirect.

        } catch (ValidationException e) {
            // On validation error, set the error message and forward back to the main form page.
            request.setAttribute("validationError", e.getMessage());
        } catch (Exception e) {
            System.err.println("Server error during calculation: " + e.getMessage());
            request.setAttribute("validationError", "Internal server error: " + e.getMessage());
        }

        // This line is only reached if an exception was caught.
        getServletContext().getRequestDispatcher(FORM_JSP).forward(request, response);
    }

}