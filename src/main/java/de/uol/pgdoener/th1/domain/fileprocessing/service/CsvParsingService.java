package de.uol.pgdoener.th1.domain.fileprocessing.service;

import de.uol.pgdoener.th1.domain.fileprocessing.helper.DateNormalizerService;
import de.uol.pgdoener.th1.domain.fileprocessing.helper.NumberNormalizerService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CsvParsingService {

    private static final Pattern TEXT_PATTERN = Pattern.compile(".*[a-zA-Z].*");

    private final NumberNormalizerService numberNormalizerService;
    private final DateNormalizerService dateNormalizerService;

    /**
     * Parses a CSV file from an InputStream into a 2D String array.
     * Automatically trims values, ignores empty lines, and normalizes dates and numbers.
     *
     * @param originalInputStream the InputStream of the CSV file
     * @param delimiter           the CSV delimiter character (e.g. "," or ";")
     * @return a 2D array of Strings containing the CSV data
     * @throws IOException if an error occurs while reading the stream
     */
    public String[][] parseCsv(InputStream originalInputStream, String delimiter) throws IOException {
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setDelimiter(delimiter.charAt(0))
                .setQuote('"')
                .setIgnoreEmptyLines(true)
                .setTrim(true)
                .setAllowMissingColumnNames(true)
                .get();

        try (
                Reader reader = new InputStreamReader(originalInputStream);
                CSVParser parser = format.parse(reader)
        ) {
            List<String[]> rows = new ArrayList<>();
            for (CSVRecord record : parser) {
                int size = record.size();
                String[] row = new String[size];

                for (int i = 0; i < size; i++) {
                    row[i] = getValue(record.get(i));
                }
                rows.add(row);
            }
            return rows.toArray(new String[0][0]);
        }
    }

    // ----------------- Private Helper Methods ----------------- //

    /**
     * Cleans and normalizes a single CSV field value.
     * Tries to:
     * - Normalize dates if detected
     * - Normalize numbers if no letters are present
     * - Convert percentages to decimal values
     *
     * @param raw the original field value
     * @return the cleaned and normalized value
     */
    private String getValue(String raw) {
        if (raw == null || raw.isBlank()) return "";
        raw = raw.trim();

        String maybeDate = dateNormalizerService.tryNormalize(raw);
        if (maybeDate != null) return maybeDate;

        if (TEXT_PATTERN.matcher(raw).matches()) return raw;
        String normalizedNumber = numberNormalizerService.normalizeFormat(raw);
        if (normalizedNumber != null) return normalizedNumber;

        return raw;
    }
}
