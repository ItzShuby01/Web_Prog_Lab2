package web.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web.model.ResultManager;
import java.io.IOException;

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
            // If the clear button was pressed, clear the session and redirect back to prevent re-submission.
            ResultManager.clearAllResults(request.getSession());

            // Redirect to the controller's base path, which will be index.jsp
            // A GET redirect and prevents issues with request dispatching after clearing session data.
            response.sendRedirect(request.getContextPath() + "/controller");
            return;
        }


        // Check for calculation request (form submission or graph click)
        String rParam = request.getParameter("r");

        if (rParam != null && !rParam.isEmpty()) {
            // Request contains coordinates -> Delegate to the AreaCheckServlet.
            getServletContext().getRequestDispatcher(AREA_CHECK_SERVLET).forward(request, response);
        } else {
            // Request does NOT contain coordinates -> Delegate to the JSP form.
            getServletContext().getRequestDispatcher(FORM_JSP).forward(request, response);
        }
    }
}
