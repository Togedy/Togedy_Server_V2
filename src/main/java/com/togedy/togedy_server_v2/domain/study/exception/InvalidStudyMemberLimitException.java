package com.togedy.togedy_server_v2.domain.study.exception;

import com.togedy.togedy_server_v2.global.error.CustomException;
import com.togedy.togedy_server_v2.global.error.ErrorCode;

public class InvalidStudyMemberLimitException extends CustomException {

    public InvalidStudyMemberLimitException() {
        super(ErrorCode.INVALID_STUDY_MEMBER_LIMIT);
    }
}
