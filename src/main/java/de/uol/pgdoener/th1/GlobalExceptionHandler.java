package de.uol.pgdoener.th1;

import de.uol.pgdoener.th1.business.infrastructure.converterchain.TransformationException;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.ConverterException;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.TableStructureGenerationException;
import de.uol.pgdoener.th1.business.service.ServiceException;
import de.uol.pgdoener.th1.metabase.MetabaseException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // Add request Id and code ?
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<Object> handleServiceException(ServiceException ex, HttpServletRequest request) {
        Map<String, Object> errorBody = new LinkedHashMap<>();
        //errorBody.put("code", ex.getHttpStatus().value());
        errorBody.put("message", ex.getMessage());
        errorBody.put("details", ex.getDetails());
        errorBody.put("timestamp", Instant.now().toString());
        errorBody.put("path", request.getRequestURI());
        errorBody.put("suggestion", ex.getSuggestion());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", ex.getHttpStatus().isError() ? "error" : "success");
        body.put("statusCode", ex.getHttpStatus().value());
        body.put("error", errorBody);
        //body.put("documentation_url", "https://example.com/docs/errors#" + ex.getHttpStatus().value());

        return ResponseEntity.status(ex.getHttpStatus()).body(body);
    }

    @ExceptionHandler(MetabaseException.class)
    public ResponseEntity<Object> handleMetabaseException(MetabaseException ex) {
        ErrorResponse errorResponse = ErrorResponse.create(ex, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        log.debug("MetabaseException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse.getBody());
    }

    @ExceptionHandler(ArrayIndexOutOfBoundsException.class)
    public ResponseEntity<Object> handleArrayIndexOutOfBoundsException(ArrayIndexOutOfBoundsException ex) {
        ErrorResponse errorResponse = ErrorResponse.create(ex, HttpStatus.BAD_REQUEST, ex.getMessage());
        log.debug("ArrayIndexOutOfBoundsException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse.getBody());
    }

    @ExceptionHandler(TransformationException.class)
    public ResponseEntity<Object> handleTransformationException(TransformationException ex) {
        String detail = ex.getMessage();
        detail += ex.getCause() != null ? ": " + ex.getCause().getMessage() : "";
        ErrorResponse errorResponse = ErrorResponse.create(ex, HttpStatus.BAD_REQUEST, detail);
        log.debug("TransformationException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse.getBody());
    }

    @ExceptionHandler(ConverterException.class)
    public ResponseEntity<Object> handleConverterException(ConverterException ex) {
        ErrorResponse errorResponse = ErrorResponse.create(ex, HttpStatus.BAD_REQUEST, "Index: " + ex.getConverterIndex() + ": " + ex.getMessage());
        log.debug("ConverterException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse.getBody());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        ErrorResponse errorResponse = ErrorResponse.create(ex, HttpStatus.CONFLICT, ex.getMessage());
        log.debug("DataIntegrityViolationException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse.getBody());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        ErrorResponse errorResponse = ErrorResponse.create(ex, HttpStatus.BAD_REQUEST, ex.getMessage());
        log.debug("HttpMessageNotReadableException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse.getBody());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse errorResponse = ErrorResponse.create(ex, HttpStatus.BAD_REQUEST, ex.getMessage());
        log.debug("IllegalArgumentException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse.getBody());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex) {
        ErrorResponse errorResponse = ErrorResponse.create(ex, HttpStatus.BAD_REQUEST, ex.getMessage());
        log.debug("EntityNotFoundException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse.getBody());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.debug("MethodArgumentNotValidException: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(TableStructureGenerationException.class)
    public ResponseEntity<Object> handleTableStructureGenerationException(TableStructureGenerationException ex) {
        ErrorResponse errorResponse = ErrorResponse.create(ex, HttpStatus.BAD_REQUEST, ex.getMessage());
        log.debug("TableStructureGenerationException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse.getBody());
    }

}


