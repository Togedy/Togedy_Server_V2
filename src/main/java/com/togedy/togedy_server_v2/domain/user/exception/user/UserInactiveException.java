package com.togedy.togedy_server_v2.domain.user.exception.user;

import com.togedy.togedy_server_v2.global.enums.ErrorCode;
import com.togedy.togedy_server_v2.global.exception.CustomException;

public class UserInactiveException extends CustomException {

    public UserInactiveException() {
        super(ErrorCode.USER_INACTIVE);
    }
}
