package de.uol.pgdoener.th1.domain.fileprocessing.service;

import de.uol.pgdoener.th1.domain.dataframe.*;
import de.uol.pgdoener.th1.domain.fileprocessing.helper.DateNormalizerService;
import de.uol.pgdoener.th1.domain.fileprocessing.helper.NumberNormalizerService;
import de.uol.pgdoener.th1.domain.fileprocessing.helper.TypeDetector;
import de.uol.pgdoener.th1.domain.fileprocessing.helper.ValueType;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

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
    public DataFrame createDataFrame(InputStream originalInputStream, CSVFormat format) throws IOException {
        try (
                Reader reader = new InputStreamReader(originalInputStream);
                CSVParser parser = format.parse(reader)
        ) {
            List<String> headers = parser.getHeaderNames();
            int colCount = headers.size();

            DataColMeta[] dataColMetas = new DataColMeta[colCount];
            for (int i = 0; i < colCount; i++) {
                dataColMetas[i] = new DataColMeta(headers.get(i));
            }

            int rowCount = 0;
            for (CSVRecord csvRecord : parser) {
                for (int j = 0; j < csvRecord.size(); j++) {
                    String rawValue = csvRecord.get(j);
                    if (rawValue == null || rawValue.isBlank()) continue;
                    ValueType valueType = typeDetector.detect(rawValue);
                    dataColMetas[j].mergeType(valueType);
                }
                rowCount++;
            }

            DataFrame df = new DataFrame();
            for (DataColMeta dataColMeta : dataColMetas) {
                DataColumn col = createColumnForType(dataColMeta.getName(), dataColMeta.getValueType(), rowCount);
                df.addColumn(dataColMeta.getName(), col);
            }

            return df;
        }
    }

    public DataFrame parseCsv(InputStream originalInputStream, CSVFormat format, DataFrame df) throws IOException {
        try (
                Reader reader = new InputStreamReader(originalInputStream);
                CSVParser parser = format.parse(reader)
        ) {
            for (CSVRecord csvRecord : parser) {
                for (int j = 0; j < csvRecord.size(); j++) {
                    String rawValue = csvRecord.get(j);

                    df.getColumn()
                }
            }
            return df;
        }
    }


    // ----------------- Private Helper Methods ----------------- //

    private DataColumn createColumnForType(String name, ValueType valueType, int capacity) {
        return switch (valueType) {
            case NUMBER -> new IntColumn(name, capacity);
            case DATE -> new DateColumn(name, capacity);
            case TEXT, TIMESTAMP, BOOLEAN, UUID -> new StringColumn(name, capacity);
        };
    }

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
            case NUMBER -> numberNormalizerService.normalizeFormat(raw);
            case DATE -> dateNormalizerService.tryNormalize(raw);
            case TEXT, TIMESTAMP, BOOLEAN, UUID -> raw;
        };
    }
}
