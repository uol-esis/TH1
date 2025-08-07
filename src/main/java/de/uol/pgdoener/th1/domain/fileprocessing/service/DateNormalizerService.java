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
            DateTimeFormatter.ofPattern("dd.MM.yyyy"),                    // z. B. 07.08.2025
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),                    // z. B. 07/08/2025
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),                    // z. B. 07-08-2025
            DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH),  // z. B. 07-Aug-2025
            DateTimeFormatter.ofPattern("yyyy.MM.dd"),                    // z. B. 2025.08.07
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),                    // z. B. 2025-08-07 (ISO)
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),
            DateTimeFormatter.ofPattern("yy.MM.dd"),                      // z. B. 25.08.07
            DateTimeFormatter.ofPattern("d/M/yyyy"),                      // z. B. 8/7/2025
            DateTimeFormatter.ofPattern("MM/dd/yyyy")                     // z. B. 08/07/2025 (US)
    );

    private static final DateTimeFormatter DEFAULT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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
