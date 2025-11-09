package web.model;

import java.util.concurrent.atomic.AtomicLong;

// Manages statistics that are global across all user sessions.
// This class is thread-safe using AtomicLongs for performance.

public class GlobalResultManager {

    private static final AtomicLong totalRequests = new AtomicLong(0);
    private static final AtomicLong totalHits = new AtomicLong(0);
    private static final AtomicLong totalMisses = new AtomicLong(0);

    private GlobalResultManager() {
    }

    // Updates global statistics based on a new calculation result.
    public static void addResult(CalculationResult result) {
        totalRequests.incrementAndGet();
        if (result.hit()) {
            totalHits.incrementAndGet();
        } else {
            totalMisses.incrementAndGet();
        }
    }

    // Decrements the global counters by the specified amounts.
    // This is used when a user clears their session history.
    public static void decrementStats(long hitsToSubtract, long missesToSubtract, long totalToSubtract) {
        // Subtract the stats values using addAndGet with a -ve number
        totalRequests.addAndGet(-totalToSubtract);
        totalHits.addAndGet(-hitsToSubtract);
        totalMisses.addAndGet(-missesToSubtract);

        // Safety check: ensure counters never drop below zero,
        if (totalRequests.get() < 0) totalRequests.set(0);
        if (totalHits.get() < 0) totalHits.set(0);
        if (totalMisses.get() < 0) totalMisses.set(0);
    }

    // Gets the current global stats as a formatted JSON string.
    public static String getGlobalStatsJson() {
        return String.format(
                "{\"total\": %d, \"hits\": %d, \"misses\": %d}",
                totalRequests.get(),
                totalHits.get(),
                totalMisses.get()
        );
    }
}