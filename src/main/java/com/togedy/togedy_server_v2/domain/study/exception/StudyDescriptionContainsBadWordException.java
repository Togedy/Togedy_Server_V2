package com.togedy.togedy_server_v2.domain.study.exception;

import com.togedy.togedy_server_v2.global.error.CustomException;
import com.togedy.togedy_server_v2.global.error.ErrorCode;

public class StudyDescriptionContainsBadWordException extends CustomException {

    public StudyDescriptionContainsBadWordException() {
        super(ErrorCode.STUDY_DESCRIPTION_CONTAINS_BAD_WORD);
    }
}
