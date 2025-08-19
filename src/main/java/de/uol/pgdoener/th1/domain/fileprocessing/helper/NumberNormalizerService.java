package de.uol.pgdoener.th1.domain.fileprocessing.helper;

import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.regex.Pattern;

@Service
public class NumberNormalizerService {

    private static final Pattern ZEROS_PATTERN = Pattern.compile("0+$");
    private static final Pattern TRAILING_DECIMAL_PATTERN = Pattern.compile("\\.$");
    private static final Pattern TEXT_PATTERN = Pattern.compile(".*[a-zA-Z].*");
    private static final Pattern NON_NUMERIC_PATTERN = Pattern.compile("[^\\d.,-]");

    /**
     * Formats a numeric value into a string using US locale.
     * <p>
     * - If the number is an integer, it is formatted without decimals. <br>
     * - If it has a fractional part, it is formatted with up to 10 decimal places,
     * removing unnecessary trailing zeros and any trailing decimal point.
     *
     * @param number the numeric value to format
     * @return a formatted string representation of the number
     */
    public String formatNumeric(double number) {
        if (number == (long) number) {
            return Long.toString((long) number);
        } else {
            String result = String.format(Locale.US, "%.10f", number);
            result = ZEROS_PATTERN.matcher(result).replaceAll(""); // FIXME relevant?
            return TRAILING_DECIMAL_PATTERN.matcher(result).replaceAll(""); // FIXME relevant?
        }
    }

    /**
     * Attempts to normalize a numeric string into a standard format suitable for parsing.
     * <p>
     * - Removes all non-numeric characters except digits, commas, dots, and minus signs. <br>
     * - Detects whether commas or dots are being used as decimal or thousands separators. <br>
     * - Converts the detected decimal separator to a dot (".") for consistency.
     * <p>
     * This method does not format the number; it only prepares the raw string for parsing.
     *
     * @param raw the raw string containing a number
     * @return a normalized numeric string, or {@code null} if the input contains letters or is invalid
     */
    public String normalizeFormat(String raw) {
        if (raw == null || TEXT_PATTERN.matcher(raw).matches()) {
            return null;
        }

        String input = NON_NUMERIC_PATTERN.matcher(raw).replaceAll(""); // nur Ziffern, Punkt, Komma und Minuszeichen bleiben

        boolean hasComma = input.contains(",");
        boolean hasDot = input.contains(".");
        int lastComma = input.lastIndexOf(',');
        int lastDot = input.lastIndexOf('.');

        if (hasComma && hasDot) {
            if (lastComma > lastDot) {
                input = input.replace(".", "").replace(",", ".");
            } else {
                input = input.replace(",", "");
            }
        } else if (hasComma) {
            if (input.indexOf(',') != lastComma) {
                input = input.replace(",", "");
            } else {
                input = input.replace(",", ".");
            }
        } else if (hasDot) {
            if (input.indexOf('.') != lastDot) {
                input = input.replace(".", "");
            }
        }

        return input;
    }
}