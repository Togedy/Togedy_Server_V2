package com.togedy.togedy_server_v2.domain.planner.exception;

import com.togedy.togedy_server_v2.global.error.CustomException;
import com.togedy.togedy_server_v2.global.error.ErrorCode;

public class StudySubjectNotOwnedException extends CustomException {

    public StudySubjectNotOwnedException() { super(ErrorCode.STUDY_SUBJECT_NOT_OWNED); }
}
