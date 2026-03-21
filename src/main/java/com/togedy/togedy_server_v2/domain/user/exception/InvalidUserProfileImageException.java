package com.togedy.togedy_server_v2.domain.user.exception;

import com.togedy.togedy_server_v2.global.error.CustomException;
import com.togedy.togedy_server_v2.global.error.ErrorCode;

public class InvalidUserProfileImageException extends CustomException {

    public InvalidUserProfileImageException() {
        super(ErrorCode.INVALID_USER_PROFILE_IMAGE);
    }
}
