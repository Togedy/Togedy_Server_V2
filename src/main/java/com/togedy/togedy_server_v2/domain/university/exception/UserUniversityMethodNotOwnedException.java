package com.togedy.togedy_server_v2.domain.university.exception;

import com.togedy.togedy_server_v2.global.enums.ErrorCode;
import com.togedy.togedy_server_v2.global.exception.CustomException;

public class UserUniversityMethodNotOwnedException extends CustomException {

    public UserUniversityMethodNotOwnedException() {
        super(ErrorCode.USER_UNIVERSITY_METHOD_NOT_OWNED);
    }
}
