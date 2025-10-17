package web.model;

import java.io.Serializable;
import java.time.LocalDateTime;

// Must implement Serializable to be stored in HttpSession
public class CalculationResult implements Serializable {

    private final float x;
    private final float y;
    private final float r;
    private final boolean hit;
    private final LocalDateTime timestamp;
    private final long executionTimeNanos;

    // Constructor for new results
    public CalculationResult(float x, float y, float r, boolean hit, LocalDateTime timestamp, long executionTimeNanos) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.hit = hit;
        this.timestamp = timestamp;
        this.executionTimeNanos = executionTimeNanos;
    }

    // Getters
    public float getX() { return x; }
    public float getY() { return y; }
    public float getR() { return r; }
    public boolean isHit() { return hit; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public long getExecutionTimeNanos() { return executionTimeNanos; }

}