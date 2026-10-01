package com.togedy.togedy_server_v2.global.exception;

import com.togedy.togedy_server_v2.domain.university.enums.AdmissionType;
import com.togedy.togedy_server_v2.global.enums.ErrorCode;
import com.togedy.togedy_server_v2.global.infrastructure.discord.DiscordNotifier;
import com.togedy.togedy_server_v2.global.response.ErrorResponse;
import com.togedy.togedy_server_v2.global.util.ApiUtil;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final DiscordNotifier discordNotifier;

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Object> handleCustomException(CustomException e) {
        return handleException(e, ErrorResponse.from(e.getErrorCode()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnexpectedException(Exception e) {
        return handleException(e, ErrorResponse.from(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e
    ) {
        if (e.getRequiredType() == AdmissionType.class) {
            return handleException(e, ErrorResponse.from(ErrorCode.INVALID_ADMISSION_TYPE));
        }

        return handleException(e, ErrorResponse.from(ErrorCode.INVALID_INPUT_VALUE));
    }

    private ResponseEntity<Object> handleException(Exception e, ErrorResponse errorResponse) {
        logException(e, errorResponse);
        return ResponseEntity
                .status(errorResponse.getStatus())
                .body(ApiUtil.error(errorResponse));
    }

    private void logException(Exception e, ErrorResponse errorResponse) {
        if (errorResponse.getStatus() >= 500) {
            log.error("[{}] {}: {}", errorResponse.getCode(), e.getClass().getSimpleName(), e.getMessage(), e);
            discordNotifier.notify(errorResponse.getCode(), e);
            return;
        }

        log.warn("[{}] {}: {}", errorResponse.getCode(), e.getClass().getSimpleName(), e.getMessage());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return handleException(ex, ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, errorMessage));
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        return handleException(ex, ErrorResponse.from(ErrorCode.INVALID_INPUT_VALUE));
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, Object body,
            HttpHeaders headers,
            HttpStatusCode statusCode,
            WebRequest request
    ) {
        if (statusCode.value() == 404) {
            return handleException(ex, ErrorResponse.from(ErrorCode.NOT_FOUND_RESOURCE));
        }

        if (statusCode.value() == 405) {
            return handleException(ex, ErrorResponse.from(ErrorCode.METHOD_NOT_ALLOWED));
        }

        if (statusCode.is5xxServerError()) {
            return handleException(ex, ErrorResponse.from(ErrorCode.INTERNAL_SERVER_ERROR));
        }

        return handleException(ex, ErrorResponse.from(ErrorCode.INVALID_INPUT_VALUE));
    }
}
