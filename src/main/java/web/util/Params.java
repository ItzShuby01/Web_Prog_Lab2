package web.util;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Params {
    private final float x;
    private final float y;
    private final float r;

    private static final Set<Float> ALLOWED_R_VALUES = Set.of(1.0f, 2.0f, 3.0f, 4.0f, 5.0f);
    //Define the specific allowed X values for a form submission
    private static final Set<Float> ALLOWED_X_VALUES_FORM = Set.of(-3.0f, -2.0f, -1.0f, 0.0f, 1.0f, 2.0f, 3.0f, 4.0f, 5.0f);



    // Constructor now just parses, no validation.
    public Params(Map<String, String> params) {
        this.x = Float.parseFloat(params.get("x"));
        this.y = Float.parseFloat(params.get("y"));
        this.r = Float.parseFloat(params.get("r"));
    }

    public static Map<String, String> splitQuery(String query) {
        return Arrays.stream(query.split("&"))
                .map(pair -> pair.split("="))
                .collect(
                        Collectors.toMap(
                                pairParts -> URLDecoder.decode(pairParts[0], StandardCharsets.UTF_8),
                                pairParts -> URLDecoder.decode(pairParts[1], StandardCharsets.UTF_8),
                                (a, b) -> b,
                                HashMap::new
                        )
                );
    }


    // Validation logic specifically for form submissions
    public static void validateForm(Map<String, String> params) throws ValidationException {
        // X VALIDATION (must be one of the allowed integers)
        try {
            var x = Float.parseFloat(params.get("x"));
            if (!ALLOWED_X_VALUES_FORM.contains(x)) {
                throw new ValidationException("For form submissions, X must be one of the selected values.");
            }
        } catch (NullPointerException | NumberFormatException e) {
            throw new ValidationException("X is not a valid number.");
        }

        // Y VALIDATION (must be between -3 and 5)
        try {
            var y = Float.parseFloat(params.get("y"));
            if (y < -3 || y > 5) {
                throw new ValidationException("For form submissions, Y must be between -3 and 5.");
            }
        } catch (NullPointerException | NumberFormatException e) {
            throw new ValidationException("Y is not a valid number.");
        }

        // R VALIDATION
        validateR(params);
    }

    //Validation logic for canvas clicks
    public static void validateCanvas(Map<String, String> params) throws ValidationException {
        // X VALIDATION (must be between -5 and 5)
        try {
            var x = Float.parseFloat(params.get("x"));
            if (x < -5 || x > 5) {
                throw new ValidationException("For canvas clicks, X must be between -5 and 5.");
            }
        } catch (NullPointerException | NumberFormatException e) {
            throw new ValidationException("X is not a valid number.");
        }

        // Y VALIDATION (must be between -5 and 5)
        try {
            var y = Float.parseFloat(params.get("y"));
            if (y < -5 || y > 5) {
                throw new ValidationException("For canvas clicks, Y must be between -5 and 5.");
            }
        } catch (NullPointerException | NumberFormatException e) {
            throw new ValidationException("Y is not a valid number.");
        }

        // R VALIDATION
        validateR(params);
    }

    // Helper method for R validation.
    private static void validateR(Map<String, String> params) throws ValidationException {
        try {
            var r = Float.parseFloat(params.get("r"));
            if (!ALLOWED_R_VALUES.contains(r)) {
                throw new ValidationException("R has a forbidden value (must be one of 1, 2, 3, 4, 5)");
            }
        } catch (NullPointerException | NumberFormatException e) {
            throw new ValidationException("R is not a valid number.");
        }
    }


    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getR() {
        return r;
    }

}