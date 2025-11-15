package web.tags;

import jakarta.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

// Custom tag handler to display the real-time global statistics panel.
// outputs the necessary HTML structure
public class GlobalStatsTag extends SimpleTagSupport {

    private static final String STATS_HTML = """
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

    // // The entire javascript is now wrapped in  <script> block
    private static final String STATS_SCRIPT = """
            <script>
            // Real-Time Global Stats Client using Server-Sent Events (SSE).
            // Connects to the StatsSseServlet endpoint to receive global request counts
            // and updates the corresponding DOM elements.
            
            document.addEventListener('DOMContentLoaded', () => {
                // First check if the browser supports SSE
                if (!window.EventSource) {
                    console.error("Browser does not support Server-Sent Events.");
                    return;
                }
            
                // Connect to the SSE stream endpoint
                const eventSource = new EventSource('stats_stream');
            
                const totalRequestsElement = document.getElementById('total-requests');
                const totalHitsElement = document.getElementById('total-hits');
                const totalMissesElement = document.getElementById('total-misses');
            
                // Event listener for incoming messages from the server
                eventSource.onmessage = function(event) {
                    try {
                        // Parse the JSON string received from the server
                        const data = JSON.parse(event.data);
            
                        if (totalRequestsElement) {
                            totalRequestsElement.textContent = data.total || 0;
                        }
                        if (totalHitsElement) {
                            totalHitsElement.textContent = data.hits || 0;
                        }
                        if (totalMissesElement) {
                            totalMissesElement.textContent = data.misses || 0;
                        }
            
                    } catch (e) {
                        console.error("Error parsing SSE data:", e);
                    }
                };
            
                eventSource.onerror = function(error) {
                    console.error("SSE Error:", error);
                    // Attempt to re-establish connection after a delay (browser handles this automatically for SSE)
                };
            });
            </script>
            """;

    @Override
    public void doTag() throws IOException {
        String output = STATS_HTML + STATS_SCRIPT;

        // Write the combined output to the JSP
        getJspContext().getOut().write(output);
    }
}