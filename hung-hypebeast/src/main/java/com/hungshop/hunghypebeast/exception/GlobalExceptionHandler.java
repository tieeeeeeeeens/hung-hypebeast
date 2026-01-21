package com.hungshop.hunghypebeast.exception;

import com.hungshop.hunghypebeast.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return buildResponse(ex, HttpStatus.NOT_FOUND, request, null);
    }

    @ExceptionHandler(OutOfStockException.class)
    public ResponseEntity<ErrorResponse> handleOutOfStock(OutOfStockException ex, HttpServletRequest request) {
        return buildResponse(ex, HttpStatus.CONFLICT, request, null);
    }

    @ExceptionHandler(ReservationExpiredException.class)
    public ResponseEntity<ErrorResponse> handleReservationExpired(ReservationExpiredException ex, HttpServletRequest request) {
        return buildResponse(ex, HttpStatus.GONE, request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Validation error");

        ErrorResponse body = ErrorResponse.builder()
                .error("VALIDATION_ERROR")
                .message(message)
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .retryAfter(0)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        return buildResponse(ex, HttpStatus.BAD_REQUEST, request, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        return buildResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, request, null);
    }

    private ResponseEntity<ErrorResponse> buildResponse(Exception ex,
                                                       HttpStatus status,
                                                       HttpServletRequest request,
                                                       Long retryAfterSeconds) {
        String errorCode = status.name();

        ErrorResponse body = ErrorResponse.builder()
                .error(errorCode)
                .message(ex.getMessage())
                .status(status.value())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .retryAfter(retryAfterSeconds != null ? retryAfterSeconds : 0L)
                .build();

        return ResponseEntity.status(status).body(body);
    }
}
