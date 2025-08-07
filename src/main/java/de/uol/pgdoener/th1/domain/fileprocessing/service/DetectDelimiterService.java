package de.uol.pgdoener.th1.domain.fileprocessing.service;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Arrays;

@Service
public class DetectDelimiterService {

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
