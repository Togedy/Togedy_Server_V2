package com.togedy.togedy_server_v2.global.error;

import com.togedy.togedy_server_v2.global.response.ApiResponse;
import com.togedy.togedy_server_v2.global.response.ErrorResponse;
import com.togedy.togedy_server_v2.global.util.ApiUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<?>> handleCustomException(CustomException e) {
        return handleException(e, ErrorResponse.from(e.getErrorCode()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<?>> handleRuntimeException(RuntimeException e) {
        return handleException(e, ErrorResponse.from(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return handleException(e, ErrorResponse.of(
                ErrorCode.INVALID_INPUT_VALUE,
                errorMessage)
        );
    }

    private ResponseEntity<ApiResponse<?>> handleException(Exception e, ErrorResponse errorResponse) {
        log.error("[{}] {}: {}", errorResponse.getCode(), e.getClass().getSimpleName(), e.getMessage());
        return ResponseEntity
                .status(errorResponse.getStatus())
                .body(ApiUtil.error(errorResponse));
    }

}
