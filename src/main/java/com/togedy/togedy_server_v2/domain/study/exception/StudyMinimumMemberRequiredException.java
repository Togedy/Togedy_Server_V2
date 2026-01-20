package com.togedy.togedy_server_v2.domain.study.exception;

import com.togedy.togedy_server_v2.global.error.CustomException;
import com.togedy.togedy_server_v2.global.error.ErrorCode;

public class StudyMinimumMemberRequiredException extends CustomException {

    public StudyMinimumMemberRequiredException() {
        super(ErrorCode.STUDY_MINIMUM_MEMBER_REQUIRED);
    }
}
