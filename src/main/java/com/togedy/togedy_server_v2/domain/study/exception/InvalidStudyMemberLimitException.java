package com.togedy.togedy_server_v2.domain.study.exception;

import com.togedy.togedy_server_v2.global.enums.ErrorCode;
import com.togedy.togedy_server_v2.global.exception.CustomException;

public class InvalidStudyMemberLimitException extends CustomException {

    public InvalidStudyMemberLimitException() {
        super(ErrorCode.INVALID_STUDY_MEMBER_LIMIT);
    }
}
