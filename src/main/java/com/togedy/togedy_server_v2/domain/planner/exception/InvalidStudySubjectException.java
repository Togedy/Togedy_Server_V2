package com.togedy.togedy_server_v2.domain.planner.exception;

import com.togedy.togedy_server_v2.global.error.CustomException;
import com.togedy.togedy_server_v2.global.error.ErrorCode;

public class InvalidStudySubjectException extends CustomException {

    public InvalidStudySubjectException() {
        super(ErrorCode.INVALID_STUDY_SUBJECT);
    }
}
