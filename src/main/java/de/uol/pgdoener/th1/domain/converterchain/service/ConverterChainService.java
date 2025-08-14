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
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConverterChainService {
    private final FileProcessingService fileProcessingService;

    /**
     * Performs a transformation on the contents of an uploaded file using the specified converter chain.
     * <p>
     * This method is a convenience overload that delegates to
     * {@link #performTransformation(MultipartFile, ConverterChain, Optional)}
     * with no specific page number (processes the default or entire file).
     *
     * @param file           The uploaded file to transform (e.g., PDF, image, spreadsheet).
     * @param converterChain The chain of converters that will be applied to the extracted matrix.
     * @return A two-dimensional {@code String} array representing the transformed table data.
     * The first dimension corresponds to rows, and the second to columns.
     * @throws TransformationException If the file cannot be processed or the transformation fails.
     */
    public String[][] performTransformation(
            @NonNull MultipartFile file, ConverterChain converterChain
    ) throws TransformationException {
        return performTransformation(file, converterChain, Optional.empty());
    }

    /**
     * Performs a transformation on the contents of an uploaded file using the specified converter chain,
     * optionally targeting a specific page.
     * <p>
     * Steps:
     * <ol>
     *     <li>Processes the uploaded file into a 2D string matrix (rows × columns), optionally restricted to a page.</li>
     *     <li>Executes the provided {@link ConverterChain} on the extracted matrix.</li>
     *     <li>Returns the transformed matrix as a {@code String[][]}.</li>
     * </ol>
     *
     * @param file           The uploaded file to transform.
     * @param converterChain The chain of converters that will be applied to the extracted matrix.
     * @param page           The optional page number to process (empty = first page).
     * @return A two-dimensional {@code String} array representing the transformed table data.
     * The first dimension corresponds to rows, and the second to columns.
     * @throws TransformationException If the file cannot be processed or the transformation fails.
     */
    public String[][] performTransformation(
            @NonNull MultipartFile file, ConverterChain converterChain, Optional<Integer> page
    ) throws TransformationException {
        String[][] inputMatrix;
        try {
            inputMatrix = fileProcessingService.process(file, page);
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
