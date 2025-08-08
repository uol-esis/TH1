package de.uol.pgdoener.th1.domain.fileprocessing.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
public class DateNormalizerService {

    private final Pattern DATE_PATTERN = Pattern.compile("\\d{1,4}[-./\\s][a-zA-Z0-9]{1,4}[-./\\s][a-zA-Z0-9]{1,4}");

    private static final List<DateTimeFormatter> DATE_FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("dd.MM.yyyy"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("yyyy.MM.dd"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),
            DateTimeFormatter.ofPattern("yy.MM.dd"),
            DateTimeFormatter.ofPattern("d/M/yyyy"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy")
    );

    private static final DateTimeFormatter DEFAULT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Tries to normalize a date string to the default format ("yyyy-MM-dd").
     * <p>
     * Steps:
     * <ul>
     *   <li>Quickly checks if the value matches a basic date pattern</li>
     *   <li>Tries parsing with each known date format</li>
     *   <li>If parsing succeeds, returns the normalized date string</li>
     *   <li>If no format matches, returns {@code null}</li>
     * </ul>
     *
     * @param value the date string to normalize
     * @return the normalized date string, or {@code null} if parsing failed
     */
    public String tryNormalize(String value) {
        if (value == null) return null;
        if (!DATE_PATTERN.matcher(value).matches()) return null;

        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                LocalDate date = LocalDate.parse(value, formatter);
                return date.format(DEFAULT_FORMAT);
            } catch (DateTimeParseException ignored) {
                // try next format
            }
        }
        return null;
    }
}
