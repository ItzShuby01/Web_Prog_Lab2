package web.model;

import jakarta.servlet.http.HttpSession;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Key used to store the results list in the session
public class ResultManager implements Serializable {
    private static final String RESULTS_ATTRIBUTE = "pastResults";

    // Public API to get the current list of results from the session
    @SuppressWarnings("unchecked")
    public static List<CalculationResult> getResults(HttpSession session) {
        // Retrieve the list from the session, casting it back to List<CalculationResult>
        Object results = session.getAttribute(RESULTS_ATTRIBUTE);
        if (results == null) {
            // If the list is not yet in the session, create a new empty list
            List<CalculationResult> newList = Collections.synchronizedList(new ArrayList<>());
            session.setAttribute(RESULTS_ATTRIBUTE, newList);
            return newList;
        }
        return (List<CalculationResult>) results;
    }

    // Public API to save a new result to the session list
    public static void saveResult(HttpSession session, CalculationResult result) {
        List<CalculationResult> results = getResults(session);
        // Add the new result to the beginning of the list
        results.add(0, result);
    }

    // Public API to clear all results from the session
    public static void clearAllResults(HttpSession session) {
        session.removeAttribute(RESULTS_ATTRIBUTE);
    }
}