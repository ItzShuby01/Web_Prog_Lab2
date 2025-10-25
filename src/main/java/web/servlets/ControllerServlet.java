package web.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web.model.CalculationResult;
import web.model.ResultManager;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "ControllerServlet", value = "/controller")
public class ControllerServlet extends HttpServlet {
    private static final String AREA_CHECK_SERVLET = "/check";
    private static final String FORM_JSP = "/index.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check for the CLEAR RESULTS request
        String clearResults = request.getParameter("clear_results");

        if ("true".equals(clearResults)) {
            // If the clear button was pressed, clear the session results.
            ResultManager.clearAllResults(request.getSession());

            // Redirect directly to the main page after clearing.
            response.sendRedirect(request.getContextPath() + FORM_JSP);
            return;
        }


        // Check for calculation request (form submission or graph click)
        String rParam = request.getParameter("r");

        if (rParam != null && !rParam.isEmpty()) {
            // Request contains coordinates -> Delegate to the AreaCheckServlet.
            getServletContext().getRequestDispatcher(AREA_CHECK_SERVLET).forward(request, response);
        } else {
            //KEY: Now  Request does NOT contain coordinates

            // Get the list of previous results from the session.
            List<CalculationResult> resultsList = ResultManager.getResults(request.getSession());

            // Set the list as a request attribute so the JSP can access it.
            request.setAttribute("resultsList", resultsList);

            // Forward to the JSP page. This is now just a simple view.
            getServletContext().getRequestDispatcher(FORM_JSP).forward(request, response);
        }
    }
}