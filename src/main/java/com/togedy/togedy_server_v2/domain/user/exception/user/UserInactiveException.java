package com.togedy.togedy_server_v2.domain.user.exception.user;

import com.togedy.togedy_server_v2.global.error.CustomException;
import com.togedy.togedy_server_v2.global.error.ErrorCode;

public class UserInactiveException extends CustomException {

    public UserInactiveException() {
        super(ErrorCode.USER_INACTIVE);
    }
}
