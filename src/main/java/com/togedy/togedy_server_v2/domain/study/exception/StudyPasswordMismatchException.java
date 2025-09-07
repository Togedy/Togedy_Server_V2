package com.togedy.togedy_server_v2.domain.study.exception;

import com.togedy.togedy_server_v2.global.error.CustomException;
import com.togedy.togedy_server_v2.global.error.ErrorCode;

public class StudyPasswordMismatchException extends CustomException {

    public StudyPasswordMismatchException() {
        super(ErrorCode.STUDY_PASSWORD_MISMATCH);
    }
}
