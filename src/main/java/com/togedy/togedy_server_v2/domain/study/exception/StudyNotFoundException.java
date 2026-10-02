package com.togedy.togedy_server_v2.domain.study.exception;

import com.togedy.togedy_server_v2.global.enums.ErrorCode;
import com.togedy.togedy_server_v2.global.exception.CustomException;

public class StudyNotFoundException extends CustomException {

    public StudyNotFoundException() {
        super(ErrorCode.STUDY_NOT_FOUND);
    }
}
