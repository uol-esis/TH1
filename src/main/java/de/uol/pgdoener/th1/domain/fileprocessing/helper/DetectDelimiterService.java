package de.uol.pgdoener.th1.domain.fileprocessing.helper;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Arrays;

@Service
public class DetectDelimiterService {

    /**
     * Detects the delimiter used in a CSV file by analyzing its beginning bytes.
     * <p>
     * Supported delimiters: semicolon (;), comma (,), tab (\t), and pipe (|).
     *
     * @param inputStream the InputStream of the CSV file
     * @return the detected delimiter as a String (e.g., ",", ";", "\t", "|")
     * @throws IOException if an error occurs while reading the stream
     */
    public String detect(InputStream inputStream) throws IOException {
        byte[] headBytes = readHeadBytes(inputStream);
        CsvParserSettings settings = new CsvParserSettings();
        settings.detectFormatAutomatically(';', ',', '\t', '|');

        CsvParser parser = new CsvParser(settings);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(headBytes)))) {
            parser.parseAll(reader);
            parser.stopParsing();
        }
        return parser.getDetectedFormat().getDelimiterString();
    }

    // ----------------- Private Helper Methods ----------------- //

    /**
     * Reads the first up to 8192 bytes from the input stream without consuming more data than necessary.
     * <p>
     * This is used to avoid loading the entire file into memory when only a small portion
     * is needed for delimiter detection.
     *
     * @param inputStream the InputStream to read from
     * @return a byte array containing the head bytes
     * @throws IOException if an error occurs while reading
     */
    private byte[] readHeadBytes(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[8192];
        int bytesRead = 0;
        int totalRead = 0;

        while ((bytesRead = inputStream.read(buffer, totalRead, 8192 - totalRead)) != -1) {
            totalRead += bytesRead;
            if (totalRead >= 8192) break;
        }
        if (totalRead < 8192) {
            return Arrays.copyOf(buffer, totalRead);
        }
        return buffer;
    }

}
