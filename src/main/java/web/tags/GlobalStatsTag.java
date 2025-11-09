package web.tags;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

// Custom tag handler to display the real-time global statistics panel.
// outputs the necessary HTML structure (which will be updated by stats.js).
public class GlobalStatsTag extends SimpleTagSupport {

    @Override
    public void doTag() throws JspException, IOException {
        JspWriter out = getJspContext().getOut();

        // The HTML structure for the global stats panel.
        String htmlOutput = """
            <div class="global-stats-section">
                <h4 class="global-stats-header">Global Statistics (All Users)</h4>
                <div class="stat-item">
                    <p>Total Requests:</p> 
                    <p id="total-requests" class="stat-value">0</p>
                </div>
                <div class="stat-item hit-stat">
                    <p>Total Hits:</p> 
                    <p id="total-hits" class="stat-value">0</p>
                </div>
                <div class="stat-item miss-stat">
                    <p>Total Misses:</p> 
                    <p id="total-misses" class="stat-value">0</p>
                </div>
            </div>
            """;

        out.write(htmlOutput);
    }
}