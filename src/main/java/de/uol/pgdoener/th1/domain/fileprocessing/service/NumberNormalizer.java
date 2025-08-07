package de.uol.pgdoener.th1.domain.fileprocessing.service;

import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class NumberNormalizer {

    public String formatNumeric(double number) {
        if (number == (long) number) {
            return String.format(Locale.US, "%.2f", number);
        } else {
            return String.format(Locale.US, "%.10f", number)
                    .replaceAll("0+$", "")
                    .replaceAll("\\.$", "");
        }
    }

    public String normalizeFormat(String raw) {
        if (raw == null || raw.matches(".*[a-zA-Z].*")) {
            return null;
        }

        String input = raw.replaceAll("[^\\d.,-]", ""); // nur Ziffern, Punkt, Komma und Minuszeichen bleiben

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