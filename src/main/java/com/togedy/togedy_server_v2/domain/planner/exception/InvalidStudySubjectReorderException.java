package com.togedy.togedy_server_v2.domain.planner.exception;

import com.togedy.togedy_server_v2.global.enums.ErrorCode;
import com.togedy.togedy_server_v2.global.exception.CustomException;

public class InvalidStudySubjectReorderException extends CustomException {
    public InvalidStudySubjectReorderException() {
        super(ErrorCode.INVALID_STUDY_SUBJECT_REORDER);
    }
}
