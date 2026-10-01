package com.togedy.togedy_server_v2.global.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.togedy.togedy_server_v2.domain.university.enums.AdmissionType;
import com.togedy.togedy_server_v2.global.enums.ErrorCode;
import com.togedy.togedy_server_v2.global.infrastructure.discord.DiscordNotifier;
import com.togedy.togedy_server_v2.global.response.ApiResponse;
import com.togedy.togedy_server_v2.global.response.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private DiscordNotifier discordNotifier;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void 잘못된_JSON_본문은_400_에러를_반환한다() {
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("JSON parse error");

        ResponseEntity<Object> response = globalExceptionHandler.handleHttpMessageNotReadable(
                exception,
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST,
                null
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertErrorResponse(response, ErrorCode.INVALID_INPUT_VALUE);
        then(discordNotifier).should(never()).notify(anyString(), any());
    }

    @Test
    void 4xx_CustomException은_해당_에러코드를_반환하고_알림을_보내지_않는다() {
        CustomException exception = new CustomException(ErrorCode.APP_CONFIG_NOT_FOUND);

        ResponseEntity<Object> response = globalExceptionHandler.handleCustomException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertErrorResponse(response, ErrorCode.APP_CONFIG_NOT_FOUND);
        then(discordNotifier).should(never()).notify(anyString(), any());
    }

    @Test
    void 5xx_CustomException은_해당_에러코드를_반환하고_알림을_보낸다() {
        CustomException exception = new CustomException(ErrorCode.STORAGE_UPLOAD_FAILED);

        ResponseEntity<Object> response = globalExceptionHandler.handleCustomException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertErrorResponse(response, ErrorCode.STORAGE_UPLOAD_FAILED);
        then(discordNotifier).should().notify(ErrorCode.STORAGE_UPLOAD_FAILED.getCode(), exception);
    }

    @Test
    void 예상하지_못한_예외는_500_에러를_반환하고_알림을_보낸다() {
        RuntimeException exception = new RuntimeException("unexpected");

        ResponseEntity<Object> response = globalExceptionHandler.handleUnexpectedException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertErrorResponse(response, ErrorCode.INTERNAL_SERVER_ERROR);
        then(discordNotifier).should().notify(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), exception);
    }

    @Test
    void 입시_유형_타입_변환_실패는_입시_유형_에러를_반환한다() throws Exception {
        MethodArgumentTypeMismatchException exception = new MethodArgumentTypeMismatchException(
                "WRONG", AdmissionType.class, "admissionType", methodParameter(), null
        );

        ResponseEntity<Object> response = globalExceptionHandler.handleMethodArgumentTypeMismatchException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertErrorResponse(response, ErrorCode.INVALID_ADMISSION_TYPE);
    }

    @Test
    void 그_외_타입_변환_실패는_400_에러를_반환한다() throws Exception {
        MethodArgumentTypeMismatchException exception = new MethodArgumentTypeMismatchException(
                "abc", Long.class, "studyId", methodParameter(), null
        );

        ResponseEntity<Object> response = globalExceptionHandler.handleMethodArgumentTypeMismatchException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertErrorResponse(response, ErrorCode.INVALID_INPUT_VALUE);
    }

    @Test
    void 검증_실패는_필드_에러_메시지를_포함한_400_에러를_반환한다() throws Exception {
        BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "name", "공백일 수 없습니다"));
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(
                methodParameter(), bindingResult
        );

        ResponseEntity<Object> response = globalExceptionHandler.handleMethodArgumentNotValid(
                exception,
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST,
                null
        );

        ErrorResponse errorResponse = errorResponse(response);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(errorResponse.getCode()).isEqualTo(ErrorCode.INVALID_INPUT_VALUE.getCode());
        assertThat(errorResponse.getMessage()).contains("name: 공백일 수 없습니다");
    }

    @Test
    void 존재하지_않는_API는_404_에러를_반환한다() {
        ResponseEntity<Object> response = globalExceptionHandler.handleExceptionInternal(
                new RuntimeException("No static resource"),
                null,
                new HttpHeaders(),
                HttpStatus.NOT_FOUND,
                null
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertErrorResponse(response, ErrorCode.NOT_FOUND_RESOURCE);
    }

    @Test
    void 지원하지_않는_HTTP_메서드는_405_에러를_반환한다() {
        ResponseEntity<Object> response = globalExceptionHandler.handleExceptionInternal(
                new HttpRequestMethodNotSupportedException("PATCH"),
                null,
                new HttpHeaders(),
                HttpStatus.METHOD_NOT_ALLOWED,
                null
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
        assertErrorResponse(response, ErrorCode.METHOD_NOT_ALLOWED);
    }

    @Test
    void 스프링_내부_5xx_예외는_500_에러를_반환하고_알림을_보낸다() {
        RuntimeException exception = new RuntimeException("service unavailable");

        ResponseEntity<Object> response = globalExceptionHandler.handleExceptionInternal(
                exception,
                null,
                new HttpHeaders(),
                HttpStatus.SERVICE_UNAVAILABLE,
                null
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertErrorResponse(response, ErrorCode.INTERNAL_SERVER_ERROR);
        then(discordNotifier).should().notify(eq(ErrorCode.INTERNAL_SERVER_ERROR.getCode()), eq(exception));
    }

    @Test
    void 그_외_스프링_내부_4xx_예외는_400_에러를_반환한다() {
        ResponseEntity<Object> response = globalExceptionHandler.handleExceptionInternal(
                new RuntimeException("unsupported media type"),
                null,
                new HttpHeaders(),
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                null
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertErrorResponse(response, ErrorCode.INVALID_INPUT_VALUE);
    }

    private void assertErrorResponse(ResponseEntity<Object> response, ErrorCode errorCode) {
        ApiResponse<?> body = (ApiResponse<?>) response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.isSuccess()).isFalse();

        ErrorResponse errorResponse = body.getErrorResponse();
        assertThat(errorResponse.getStatus()).isEqualTo(errorCode.getHttpStatus().value());
        assertThat(errorResponse.getCode()).isEqualTo(errorCode.getCode());
        assertThat(errorResponse.getMessage()).isEqualTo(errorCode.getMessage());
    }

    private ErrorResponse errorResponse(ResponseEntity<Object> response) {
        ApiResponse<?> body = (ApiResponse<?>) response.getBody();
        assertThat(body).isNotNull();
        return body.getErrorResponse();
    }

    private MethodParameter methodParameter() throws NoSuchMethodException {
        return new MethodParameter(String.class.getMethod("valueOf", Object.class), 0);
    }
}
