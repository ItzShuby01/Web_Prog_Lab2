package web.model;

public class AreaCalculator {
    public static boolean calculate(float x, float y, float r) {
        // 1. Quadrant IV: Rectangle (0 <= X <= R and -R/2 <= Y <= 0)
        boolean inRectangle = (x >= 0) && (x <= r) &&
                (y <= 0) && (y >= -r / 2.0f);


        // 2. Quadrant III : Quarter Circle (x <= 0, y <= 0, x^2 + y^2 <= (R/2)^2)
        float radiusHalfSquared = (r / 2.0f) * (r / 2.0f);
        boolean inQuarterCircle = (x <= 0) && (y <= 0) &&
                (Math.pow(x, 2) + Math.pow(y, 2) <= radiusHalfSquared);


        // 3. Quadrant II: Triangle (Vertices: (-R/2, 0), (0, 0), (0, R))
        // The point must be in the Q2 quadrant (x <= 0, y >= 0)
        // X boundary: -R/2 <= X <= 0
        // Y boundary: 0 <= Y <= R
        // Below the line Y = 2X + R (or Y - 2X - R <= 0)
        boolean inTriangle = (x <= 0) && (x >= -r / 2.0f) &&
                (y >= 0) && (y <= r) &&
                (y <= 2.0f * x + r);


        // The point is a "Hit" if it is in any of the three colored regions.
        return inRectangle || inQuarterCircle || inTriangle;
    }

}
