package com.togedy.togedy_server_v2.domain.study.exception;

import com.togedy.togedy_server_v2.global.enums.ErrorCode;
import com.togedy.togedy_server_v2.global.exception.CustomException;

public class StudyMinimumMemberRequiredException extends CustomException {

    public StudyMinimumMemberRequiredException() {
        super(ErrorCode.STUDY_MINIMUM_MEMBER_REQUIRED);
    }
}
