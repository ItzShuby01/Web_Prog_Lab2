package web.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web.model.GlobalResultManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.*;

// This new servlet is the real-time Server-Sent Events (SSE) endpoint.
// It holds the connection open and uses a scheduler to push the global stats from the
// GlobalResultManager to all connected clients every second.

@WebServlet(name = "StatsSseServlet", urlPatterns = "/stats_stream", asyncSupported = true)
public class StatsSseServlet extends HttpServlet {

    // Scheduler to periodically send updates
    private ScheduledExecutorService scheduler;

    // Periodic updates after 1 second
    private static final long UPDATE_PERIOD_SECONDS = 1;

    @Override
    public void init() throws ServletException {
        super.init();
        // Initializng the scheduler to run a task periodically
        scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Setting headers for Server Side Events (SSE)
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");

        final PrintWriter writer = response.getWriter();

        // Runnable task that sends the statistics
        Runnable sendStats = () -> {
            try {
                String statsJson = GlobalResultManager.getGlobalStatsJson();

                // SSE format
                writer.write("data: " + statsJson + "\n\n");

                // Flushing the writer to ensure the data is sent immediately
                if (writer.checkError()) {
                    System.out.println("Client disconnected from SSE stream.");
                    throw new IOException("Client disconnected");
                }
                writer.flush();

            } catch (Exception e) {
                // When the client disconnects, stop the task
                System.out.println("SSE error. Stopping stream task.");
                Thread.currentThread().interrupt(); // Signal to stop the thread
            }
        };

        // Schedule the task to run periodically
        ScheduledFuture<?> task = scheduler.scheduleAtFixedRate(
                sendStats, 0, UPDATE_PERIOD_SECONDS, TimeUnit.SECONDS);

        // Keeps the connection open until client disconnects
        try {
            // Blocks the current thread to keep the response open until client disconnects
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            System.out.println("SSE Servlet thread interrupted.");
        } finally {
            // Cancel the scheduled task when the connection closes
            task.cancel(true);
            writer.close();
        }
    }

    @Override
    public void destroy() {
        // Shut down the scheduler when the servlet is destroyed
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
        super.destroy();
    }
}