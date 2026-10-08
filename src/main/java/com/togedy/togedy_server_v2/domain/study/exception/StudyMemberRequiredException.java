package com.togedy.togedy_server_v2.domain.study.exception;

import com.togedy.togedy_server_v2.global.enums.ErrorCode;
import com.togedy.togedy_server_v2.global.exception.CustomException;

public class StudyMemberRequiredException extends CustomException {

    public StudyMemberRequiredException() {
        super(ErrorCode.STUDY_MEMBER_REQUIRED);
    }
}
