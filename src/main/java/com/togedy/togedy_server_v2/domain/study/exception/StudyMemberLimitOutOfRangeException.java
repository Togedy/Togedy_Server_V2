package com.togedy.togedy_server_v2.domain.study.exception;

import com.togedy.togedy_server_v2.global.enums.ErrorCode;
import com.togedy.togedy_server_v2.global.exception.CustomException;

public class StudyMemberLimitOutOfRangeException extends CustomException {

    public StudyMemberLimitOutOfRangeException() {
        super(ErrorCode.STUDY_MEMBER_LIMIT_OUT_OF_RANGE);
    }

}
