package de.uol.pgdoener.th1.business.infrastructure.csv_converter;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

enum FileType {

    CSV(List.of("text/csv"), List.of(".csv")),
    EXCEL(List.of("application/vnd.ms-excel"), List.of(".xls", ".xlsx"));

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

    static FileType getType(MultipartFile file) {
        for (FileType fileType : FileType.values()) {
            if (fileType.isType(file)) {
                return fileType;
            }
        }
        throw new IllegalArgumentException("Unsupported file type");
    }

}
