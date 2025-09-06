package com.togedy.togedy_server_v2.domain.study.exception;

import com.togedy.togedy_server_v2.global.error.CustomException;
import com.togedy.togedy_server_v2.global.error.ErrorCode;

public class DuplicateStudyNameException extends CustomException {

    public DuplicateStudyNameException() {
        super(ErrorCode.DUPLICATE_STUDY_NAME);
    }
}
