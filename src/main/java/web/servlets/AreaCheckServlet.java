package web.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web.util.Params;
import web.util.ValidationException;
import web.model.CalculationResult;
import web.model.ResultManager;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@WebServlet(name = "AreaCheckServlet", value = "/check")
public class AreaCheckServlet extends HttpServlet {
    private static final String FORM_JSP = "/index.jsp";
    //Path to the new results page.
    private static final String RESULTS_JSP = "/results.jsp";


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

            // Calculation
            var startTime = Instant.now();
            boolean hit = calculate(params.getX(), params.getY(), params.getR());
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
            response.sendRedirect(request.getContextPath() + "/results");
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

    private boolean calculate(float x, float y, float r) {
        // 1. Quadrant IV: Rectangle (0 <= X <= R and -R/2 <= Y <= 0)
        boolean inRectangle = (x >= 0) && (x <= r) &&
                (y <= 0) && (y >= -r / 2.0f);


        // 2. Quadrant III : Quarter Circle (x <= 0, y <= 0, x^2 + y^2 <= (R/2)^2)
        float radiusHalfSquared = (r / 2.0f) * (r / 2.0f);
        boolean inQuarterCircle = (x <= 0) && (y <= 0) &&
                (Math.pow(x, 2) + Math.pow(y, 2) <= radiusHalfSquared);


        // 3. Quadrant II: Triangle (Vertices: (-R/2, 0), (0, 0), (0, R))
        // The point must be in the Q2 quadrant (x <= 0, y >= 0)
        // X boundary: -R/2 <= X <= 0
        // Y boundary: 0 <= Y <= R
        // Below the line Y = 2X + R (or Y - 2X - R <= 0)
        boolean inTriangle = (x <= 0) && (x >= -r / 2.0f) &&
                (y >= 0) && (y <= r) &&
                (y <= 2.0f * x + r);


        // The point is a "Hit" if it is in any of the three colored regions.
        return inRectangle || inQuarterCircle || inTriangle;
    }
}