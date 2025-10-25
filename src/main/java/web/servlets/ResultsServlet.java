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

@WebServlet(name = "ResultsServlet", value = "/results")
public class ResultsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get the list of results from the session (the Model).
        List<CalculationResult> resultsList = ResultManager.getResults(request.getSession());

        //Set the list as a request attribute so the JSP can access it.
        request.setAttribute("resultsList", resultsList);

        // Forward the request to the JSP (the View).
        getServletContext().getRequestDispatcher("/results.jsp").forward(request, response);
    }
}