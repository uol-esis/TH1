package de.uol.pgdoener.th1.api.controller;

import de.uol.pgdoener.th1.api.ConverterApiDelegate;
import de.uol.pgdoener.th1.business.dto.TableStructureDto;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.ConverterResult;
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
    public ResponseEntity<List<List<String>>> previewConvertTable(MultipartFile file, TableStructureDto request) {
        ConverterResult result = convertFileService.convertTest(request, file);
        List<List<String>> previewLines = result.dataAsListOfLists().stream().limit(10).toList();
        return ResponseEntity.ok(previewLines);
    }

    /// TODO: immer den aktuellen Datentyp setzen.
    @Override
    public ResponseEntity<Resource> fileConvertTable(MultipartFile file, TableStructureDto request) {
        ConverterResult result = convertFileService.convertTest(request, file);

        // Return the full converted file as a download
        ByteArrayOutputStream outputStream;
        try {
            outputStream = result.dataAsCsvStream();
        } catch (IOException e) {
            throw new RuntimeException("Error while preparing file for download", e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("converted_file.csv")
                .build());
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .headers(headers)
                .body(new ByteArrayResource(outputStream.toByteArray()));
    }

}
