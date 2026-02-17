package kz.natooa.common.exception;

import kz.natooa.common.dto.ExceptionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(exception = {
            ProductNotFoundException.class
    })
    public ResponseEntity<ExceptionDTO> handleProductNotFoundException(ProductNotFoundException e){
        log.error("Product not found", e);
        var exceptionDTO = new ExceptionDTO(
                "Wrong ID or Name",
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionDTO);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDTO> handleException(Exception e){
        log.error("Unexpected error", e);
        var exceptionDTO = new ExceptionDTO(
                "Unexpected error, server error",
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionDTO);
    }

    @ExceptionHandler(exception = {
            IllegalStateException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ExceptionDTO> handleBadRequestException(Exception e){
        log.error("Invalid request", e);
        var exceptionDTO = new ExceptionDTO(
                "Bad request",
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionDTO);
    }
}
