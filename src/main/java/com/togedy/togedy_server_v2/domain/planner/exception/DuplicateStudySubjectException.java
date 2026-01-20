package com.togedy.togedy_server_v2.domain.planner.exception;

import com.togedy.togedy_server_v2.global.error.CustomException;
import com.togedy.togedy_server_v2.global.error.ErrorCode;

public class DuplicateStudySubjectException extends CustomException {

    public DuplicateStudySubjectException() { super(ErrorCode.DUPLICATE_STUDY_SUBJECT); }
}
