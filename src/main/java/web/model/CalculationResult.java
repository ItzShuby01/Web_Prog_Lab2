package web.model;

import java.io.Serializable;
import java.time.LocalDateTime;

// Use record to represent an immutable calculation result and automatically generate boilerplate code
// Implement Serializable to be stored in HttpSession
public record CalculationResult(
    float x,
    float y,
    float r,
    boolean hit,
    LocalDateTime timestamp,
    long executionTimeNanos
) implements Serializable {

}