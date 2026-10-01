package com.togedy.togedy_server_v2.domain.study.exception;

import com.togedy.togedy_server_v2.global.enums.ErrorCode;
import com.togedy.togedy_server_v2.global.exception.CustomException;

public class StudyMemberCountExceededException extends CustomException {

    public StudyMemberCountExceededException() {
        super(ErrorCode.STUDY_MEMBER_COUNT_EXCEEDED);
    }

}
