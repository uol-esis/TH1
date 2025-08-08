package de.uol.pgdoener.th1.domain.fileprocessing.service;

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

@Service
@RequiredArgsConstructor
public class CsvParsingService {

    private final NumberNormalizer numberNormalizer;
    private final DateNormalizerService dateNormalizerService;

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

    // ----------------- Private helper methods ----------------- //

    private String getValue(String raw) {
        if (raw == null || raw.isBlank()) return "";
        raw = raw.trim();

        String maybeDate = dateNormalizerService.tryNormalize(raw);
        if (maybeDate != null) return maybeDate;

        if (raw.matches(".*[a-zA-Z].*")) return raw;
        String normalizedNumber = numberNormalizer.normalizeFormat(raw);
        if (normalizedNumber == null) return raw;

        try {
            double value = Double.parseDouble(normalizedNumber);
            /// TODO: Better Solution ??
            if (raw.contains("%")) value /= 100.0;
            return numberNormalizer.formatNumeric(value);
        } catch (NumberFormatException ignored) {
        }

        return raw;
    }
}
