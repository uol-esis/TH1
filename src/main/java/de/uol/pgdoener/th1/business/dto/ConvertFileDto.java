package de.uol.pgdoener.th1.business.dto;

import org.springframework.web.multipart.MultipartFile;

public record ConvertFileDto(
        long tableStructureId,
        MultipartFile file
) {}
