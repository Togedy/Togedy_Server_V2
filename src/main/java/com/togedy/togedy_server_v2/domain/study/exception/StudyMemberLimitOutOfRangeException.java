package com.togedy.togedy_server_v2.domain.study.exception;

import com.togedy.togedy_server_v2.global.error.CustomException;
import com.togedy.togedy_server_v2.global.error.ErrorCode;

public class StudyMemberLimitOutOfRangeException extends CustomException {

    public StudyMemberLimitOutOfRangeException() {
        super(ErrorCode.STUDY_MEMBER_LIMIT_OUT_OF_RANGE);
    }

}
