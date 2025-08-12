package de.uol.pgdoener.th1.domain.converterchain.service;

import de.uol.pgdoener.th1.domain.converterchain.exception.TransformationException;
import de.uol.pgdoener.th1.domain.converterchain.model.Converter;
import de.uol.pgdoener.th1.domain.converterchain.model.ConverterChain;
import de.uol.pgdoener.th1.domain.fileprocessing.service.FileProcessingService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConverterChainService {
    private final FileProcessingService fileProcessingService;

    public String[][] performTransformation(
            @NonNull MultipartFile file, ConverterChain converterChain
    ) throws TransformationException {
        String[][] inputMatrix;
        try {
            inputMatrix = fileProcessingService.process(file);
        } catch (IOException e) {
            throw new TransformationException("Could not process file", e);
        }
        return performTransformation(inputMatrix, converterChain);
    }

    /**
     * Performs the transformation on the input file.
     * <p>
     * This method reads the input file, applies the transformation defined in the converter chain,
     * and returns the result.
     * If the input file is empty or no converter is found, it returns the original data.
     * If an error occurs during the transformation, it throws a TransformationException.
     * To get more information about the error, check the cause of the exception.
     *
     * @param rawMatrix the input files matrix to be transformed
     * @return the result of the transformation
     * @throws TransformationException if an error occurs during the transformation
     */
    public String[][] performTransformation(
            @NonNull String[][] rawMatrix, ConverterChain converterChain
    ) throws TransformationException {
        String[][] transformedMatrix;
        if (rawMatrix.length == 0 || rawMatrix[0].length == 0) {
            log.debug("No data found in the input file");
            return rawMatrix;
        }
        Converter first = converterChain.getFirst();
        if (first == null) {
            log.debug("No converter found");
            return rawMatrix;
        }
        transformedMatrix = first.handleRequest(rawMatrix);
        return transformedMatrix;
    }
}
