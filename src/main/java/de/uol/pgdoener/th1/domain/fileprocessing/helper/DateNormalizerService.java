package de.uol.pgdoener.th1.domain.fileprocessing.helper;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DateNormalizerService {
    private static final DateTimeFormatter DEFAULT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /// TODO: Option for Us Format interpretation
    private static final DateTimeFormatter MULTI_FORMATTER = new DateTimeFormatterBuilder()
            // EU
            .appendOptional(DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT))
            .appendOptional(DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT))
            .appendOptional(DateTimeFormatter.ofPattern("dd.MM.uuuu").withResolverStyle(ResolverStyle.STRICT))
            .appendOptional(DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT))
            // ISO
            .appendOptional(DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT))
            .appendOptional(DateTimeFormatter.ofPattern("uuuu/MM/dd").withResolverStyle(ResolverStyle.STRICT))
            .appendOptional(DateTimeFormatter.ofPattern("uuuu.MM.dd").withResolverStyle(ResolverStyle.STRICT))
            .appendOptional(DateTimeFormatter.ofPattern("yy.MM.dd").withResolverStyle(ResolverStyle.STRICT))
            .toFormatter();

    private static final DateTimeFormatter[] DATE_FORMATTERS = new DateTimeFormatter[]{
            MULTI_FORMATTER,
            DateTimeFormatter.ofPattern("dd-MMM-uuuu", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("M/d/uuuu")
    };

    private final Map<String, String> cache = new ConcurrentHashMap<>();

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

        final String trimmed = value.trim();

        return cache.computeIfAbsent(trimmed, v -> {
            for (DateTimeFormatter formatter : DATE_FORMATTERS) {
                try {
                    LocalDate date = LocalDate.parse(v, formatter);
                    return date.format(DEFAULT_FORMAT);
                } catch (Exception ignored) {
                }
            }
            return null;
        });
    }

    /**
     * Converts a {@link java.util.Date} object into a normalized date string
     * using the default format ("yyyy-MM-dd").
     * <p>
     * Steps:
     * <ul>
     *   <li>Checks if the input is {@code null} – returns {@code null} if so</li>
     *   <li>Converts the {@link java.util.Date} to a {@link java.time.LocalDate}
     *       using the system default time zone</li>
     *   <li>Formats the LocalDate using the default date format</li>
     * </ul>
     *
     * @param date the date to normalize
     * @return the normalized date string in "yyyy-MM-dd" format,
     * or {@code null} if the input was {@code null}
     */
    public String tryNormalize(Date date) {
        if (date == null) return null;
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate.format(DEFAULT_FORMAT);
    }
}
