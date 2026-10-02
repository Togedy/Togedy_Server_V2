package com.togedy.togedy_server_v2.domain.planner.exception;

import com.togedy.togedy_server_v2.global.enums.ErrorCode;
import com.togedy.togedy_server_v2.global.exception.CustomException;

public class StudySubjectNotFoundException extends CustomException {

    public StudySubjectNotFoundException() {
        super(ErrorCode.STUDY_SUBJECT_NOT_FOUND);
    }
}
