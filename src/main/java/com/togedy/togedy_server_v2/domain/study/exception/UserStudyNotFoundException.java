package com.togedy.togedy_server_v2.domain.study.exception;

import com.togedy.togedy_server_v2.global.enums.ErrorCode;
import com.togedy.togedy_server_v2.global.exception.CustomException;

public class UserStudyNotFoundException extends CustomException {

    public UserStudyNotFoundException() {
        super(ErrorCode.USER_STUDY_NOT_FOUND);
    }
}
