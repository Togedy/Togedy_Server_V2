package com.togedy.togedy_server_v2.domain.study.exception;

import com.togedy.togedy_server_v2.global.enums.ErrorCode;
import com.togedy.togedy_server_v2.global.exception.CustomException;

public class StudyNameContainsBadWordException extends CustomException {

    public StudyNameContainsBadWordException() {
        super(ErrorCode.STUDY_NAME_CONTAINS_BAD_WORD);
    }
}
