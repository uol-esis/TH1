package de.uol.pgdoener.th1.api.controller;

import de.uol.pgdoener.th1.api.ConverterApiDelegate;
import de.uol.pgdoener.th1.business.dto.TableStructureDto;
import de.uol.pgdoener.th1.business.service.ConvertFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConvertFileController implements ConverterApiDelegate {

    private final ConvertFileService convertFileService;

    @Override
    public ResponseEntity<Void> convertTable(Long tableStructureId, MultipartFile file) {
        log.info("Converting file {}", file.getOriginalFilename());
        convertFileService.convertAndSaveInDB(tableStructureId, file);

        return ResponseEntity.ok().build();
    }

    //TODO: File kleiner machen, muss nicht nur 10 zurück geben sondern auch weniger datenpunkte umwandeln
    @Override
    public ResponseEntity<List<String>> previewConvertTable(MultipartFile file, TableStructureDto request) {
        List<String> convertedLines = convertFileService.convertTest(request, file);

        // Return the first 10 lines as a JSON response
        List<String> previewLines = convertedLines.stream().limit(10).toList();
        return ResponseEntity.ok(previewLines);
    }

    @Override
    public ResponseEntity<Resource> fileConvertTable(MultipartFile file, TableStructureDto request) {
        List<String> convertedLines = convertFileService.convertTest(request, file);

        // Return the full converted file as a download
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(String.join("\n", convertedLines).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException("Error while preparing file for download", e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("converted_file.txt")
                .build());
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .headers(headers)
                .body(new ByteArrayResource(outputStream.toByteArray()));
    }

}
