package com.togedy.togedy_server_v2.domain.study.exception;

import com.togedy.togedy_server_v2.global.enums.ErrorCode;
import com.togedy.togedy_server_v2.global.exception.CustomException;

public class StudyDescriptionContainsBadWordException extends CustomException {

    public StudyDescriptionContainsBadWordException() {
        super(ErrorCode.STUDY_DESCRIPTION_CONTAINS_BAD_WORD);
    }
}
