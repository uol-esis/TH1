package de.uol.pgdoener.th1.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class SnapshotService {

    @Value("${snapshot.dir}")
    private String BASE_PATH;

    public void saveAsCsv(String tableName, int version, String[][] matrix) throws IOException {
        String fileName = tableName + "_v" + version + "_" + UUID.randomUUID() + ".csv";
        Path path = Paths.get(BASE_PATH, fileName);

        Files.createDirectories(path.getParent());

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (String[] row : matrix) {
                writer.write(String.join(",", row));
                writer.newLine();
            }
        }

    }
}

