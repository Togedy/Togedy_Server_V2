package com.togedy.togedy_server_v2.global.util;

import com.togedy.togedy_server_v2.global.response.ApiResponse;
import com.togedy.togedy_server_v2.global.response.ErrorResponse;

public class ApiUtil {

    private ApiUtil(){
    }

    public static <T> ApiResponse<T> success(T response) {
        return ApiResponse.<T>builder()
                .success(true)
                .response(response)
                .build();
    }

    public static ApiResponse<Void> successOnly() {
        return ApiResponse.<Void>builder()
                .success(true)
                .build();
    }

    public static ApiResponse<?> error(ErrorResponse errorResponse) {
        return ApiResponse.builder()
                .success(false)
                .errorResponse(errorResponse)
                .build();
    }

}
