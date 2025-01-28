package de.uol.pgdoener.th1.api.controller;

import de.uol.pgdoener.th1.api.payload.request.CreateTableStructure;
import de.uol.pgdoener.th1.business.dto.TableStructureDto;
import de.uol.pgdoener.th1.business.mapper.TableStructureMapper;
import de.uol.pgdoener.th1.business.service.ConvertFileService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("v1")
@RequiredArgsConstructor
@Slf4j
public class ConvertFileController {

    private final ConvertFileService convertFileService;

    @PostMapping
    @RequestMapping("/convert/{tableStructureId}")
    public ResponseEntity<String> convertFile(
            @PathVariable("tableStructureId") @NotNull @Positive Long tableStructureId,
            @RequestPart("file") MultipartFile file) {
        log.info("Converting file {}", file.getOriginalFilename());
        convertFileService.convertAndSaveInDB(tableStructureId, file);

        return ResponseEntity.ok("TableStructure created");
    }

    //TODO: File kleiner machen, muss nicht nur 10 zurück geben sondern auch weniger datenpunkte umwandeln
    @PostMapping
    @RequestMapping("/convert-test")
    public ResponseEntity<?> convertFileTest(
            @RequestPart("createTableStructure") @Valid CreateTableStructure request,
            @RequestPart("file") MultipartFile file,
            @RequestParam(name = "preview", defaultValue = "false") boolean preview
    ) {
        TableStructureDto tableStructureDto = TableStructureMapper.toDto(request);
        List<String> convertedLines = convertFileService.convertTest(tableStructureDto, file);

        if (preview) {
            // Return the first 10 lines as a JSON response
            List<String> previewLines = convertedLines.stream().limit(10).collect(Collectors.toList());
            return ResponseEntity.ok(previewLines);
        }

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
                .body(outputStream.toByteArray());
    }
}
