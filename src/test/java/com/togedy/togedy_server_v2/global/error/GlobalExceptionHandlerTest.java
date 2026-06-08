package com.togedy.togedy_server_v2.global.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.togedy.togedy_server_v2.global.response.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Test
    void 잘못된_JSON_본문은_400_에러를_반환한다() {
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("JSON parse error");

        ResponseEntity<ApiResponse<?>> response = globalExceptionHandler.handleHttpMessageNotReadableException(
                exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getErrorResponse()).isNotNull();
        assertThat(response.getBody().getErrorResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getBody().getErrorResponse().getCode()).isEqualTo(ErrorCode.INVALID_INPUT_VALUE.getCode());
        assertThat(response.getBody().getErrorResponse().getMessage())
                .isEqualTo(ErrorCode.INVALID_INPUT_VALUE.getMessage());
    }
}
