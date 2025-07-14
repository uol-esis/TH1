package de.uol.pgdoener.th1.application.infrastructure;

import de.uol.pgdoener.th1.application.infrastructure.exceptions.InputFileException;
import lombok.NonNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

enum FileType {

    CSV(List.of("text/csv"), List.of(".csv")),
    EXCEL_OLE2(List.of(), List.of(".xls")),
    EXCEL_OOXML(List.of(), List.of(".xlsx"));

    private final List<String> contentTypes;
    private final List<String> fileExtensions;

    FileType(List<String> contentTypes, List<String> fileExtensions) {
        this.contentTypes = contentTypes;
        this.fileExtensions = fileExtensions;
    }

    private boolean isType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType != null && contentTypes.contains(contentType.toLowerCase())) {
            return true;
        }
        String originalFilename = file.getOriginalFilename();
        return originalFilename != null && fileExtensions.stream()
                .anyMatch(originalFilename.toLowerCase()::endsWith);
    }

    static FileType getType(@NonNull MultipartFile file) {
        for (FileType fileType : FileType.values()) {
            if (fileType.isType(file)) {
                return fileType;
            }
        }
        throw new InputFileException("Unsupported file type");
    }

}
