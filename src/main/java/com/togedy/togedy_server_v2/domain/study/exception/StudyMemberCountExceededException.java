package com.togedy.togedy_server_v2.domain.study.exception;

import com.togedy.togedy_server_v2.global.error.CustomException;
import com.togedy.togedy_server_v2.global.error.ErrorCode;

public class StudyMemberCountExceededException extends CustomException {

    public StudyMemberCountExceededException() {
        super(ErrorCode.STUDY_MEMBER_COUNT_EXCEEDED);
    }

}
