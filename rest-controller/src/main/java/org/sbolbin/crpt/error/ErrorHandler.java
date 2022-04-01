package org.sbolbin.crpt.error;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Map;
import java.util.TreeMap;

@Slf4j
@ControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException error) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildValidationErrorMessage(error));
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleInvalidFormatException(HttpMessageNotReadableException error) {
        log.warn("Invalid request body", error);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("The request body is not a valid JSON"));
    }

    @ExceptionHandler(value = HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Object> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException error) {
        log.warn("Unsupported content type: {}", error.getContentType());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Unsupported Content-Type"));
    }

    @ExceptionHandler(value = {HttpRequestMethodNotSupportedException.class, NoHandlerFoundException.class})
    public ResponseEntity<Object> handleNotFoundException(Exception ignore) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Not found"));
    }

    @ExceptionHandler(value = Throwable.class)
    public ResponseEntity<Object> handleGeneralException(Throwable error) {
        log.error("Unexpected error: ", error);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Internal Server Error"));
    }

    private Map<String, String> buildValidationErrorMessage(MethodArgumentNotValidException error) {
        BindingResult bindingResult = error.getBindingResult();
        Map<String, String> map = new TreeMap<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            map.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return map;
    }

    @Value
    public static class ErrorResponse {
        String message;
    }
}
