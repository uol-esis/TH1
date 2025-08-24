package de.uol.pgdoener.th1.domain.fileprocessing.service;

import de.uol.pgdoener.th1.domain.fileprocessing.helper.DateNormalizerService;
import de.uol.pgdoener.th1.domain.fileprocessing.helper.NumberNormalizerService;
import de.uol.pgdoener.th1.domain.fileprocessing.helper.TypeDetector;
import de.uol.pgdoener.th1.domain.fileprocessing.helper.ValueType;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

@Service
@RequiredArgsConstructor
public class CsvParsingService {

    private final NumberNormalizerService numberNormalizerService;
    private final DateNormalizerService dateNormalizerService;
    private final TypeDetector typeDetector;

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
            return parser.stream()
                    .map(r -> {
                        int size = r.size();
                        String[] row = new String[size];
                        for (int i = 0; i < size; i++) {
                            row[i] = getValue(r.get(i));
                        }
                        return row;
                    })
                    .toArray(String[][]::new);
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
        ValueType valueType = typeDetector.detect(raw);

        return switch (valueType) {
            case NUMBER:
                yield numberNormalizerService.normalizeFormat(raw);
            case DATE:
                yield dateNormalizerService.tryNormalize(raw);
            case TEXT, TIMESTAMP, BOOLEAN, UUID:
                yield raw;
        };
    }
}
