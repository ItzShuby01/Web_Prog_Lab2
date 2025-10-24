package web.util;

import web.util.ValidationException;

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



    public Params(String query) throws ValidationException {
        if (query == null || query.isEmpty()) {
            throw new ValidationException("Missing query string");
        }
        var params = splitQuery(query);
        validateParams(params);
        this.x = Float.parseFloat(params.get("x"));
        this.y = Float.parseFloat(params.get("y"));
        this.r = Float.parseFloat(params.get("r"));
    }

    private static Map<String, String> splitQuery(String query) {
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


    private static void validateParams(Map<String, String> params) throws ValidationException {
        //X VALIDATION
        var x = params.get("x");
        if (x == null || x.isEmpty()) {
            throw new ValidationException("x is invalid (missing)");
        }

        try {
            var xx = Float.parseFloat(x);
            // Expanded range to [-5, 5] to accommodate canvas clicks.
            // This single rule works for both form ([-3, 5]) and canvas clicks.
            if (xx < -5 || xx > 5) {
                throw new ValidationException("x has forbidden value (must be between -5 and 5)");
            }
        } catch (NumberFormatException e) {
            throw new ValidationException("x is not a number");
        }


        // Y VALIDATION
        var y = params.get("y");
        if (y == null || y.isEmpty()) {
            throw new ValidationException("y is invalid (missing)");
        }

        try {
            var yy = Float.parseFloat(y);
            // Expanded range to [-5, 5] to accommodate canvas clicks.
            if (yy < -5 || yy > 5) {
                throw new ValidationException("y has forbidden value (must be between -5 and 5)");
            }
        } catch (NumberFormatException e) {
            throw new ValidationException("y is not a number");
        }


        // R VALIDATION
        var r = params.get("r");
        if (r == null || r.isEmpty()) {
            throw new ValidationException("r is invalid (missing)");
        }

        try {
            var rr = Float.parseFloat(r);
            if (!ALLOWED_R_VALUES.contains(rr)) {
                throw new ValidationException("r has forbidden value (must be one of 1, 2, 3, 4, 5)");
            }
        } catch (NumberFormatException e) {
            throw new ValidationException("r is not a number");
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