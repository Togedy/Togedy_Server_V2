package com.togedy.togedy_server_v2.global.util;

import com.togedy.togedy_server_v2.global.error.ErrorCode;
import com.togedy.togedy_server_v2.global.response.ApiResponse;
import com.togedy.togedy_server_v2.global.response.ErrorResponse;
import org.springframework.stereotype.Component;

@Component
public class ApiUtil {

    public static <T> ApiResponse<T> success(T response) {
        return ApiResponse.<T>builder()
                .response(response)
                .build();
    }

    public static ApiResponse<Void> successOnly() {
        return ApiResponse.<Void>builder()
                .build();
    }

    public static ErrorResponse error(ErrorCode errorCode) {
        return ErrorResponse.builder()
                .status(errorCode.getHttpStatus().value())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
    }

    public static ErrorResponse errorWithMessage (ErrorCode errorCode, String message) {
        return ErrorResponse.builder()
                .status(errorCode.getHttpStatus().value())
                .code(errorCode.getCode())
                .message(errorCode.getMessage() + " " + message)
                .build();
    }
}
