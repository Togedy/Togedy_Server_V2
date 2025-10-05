package com.togedy.togedy_server_v2.domain.study.exception;

import com.togedy.togedy_server_v2.global.error.CustomException;
import com.togedy.togedy_server_v2.global.error.ErrorCode;

public class StudyLeaderRequiredException extends CustomException {

    public StudyLeaderRequiredException() {
        super(ErrorCode.STUDY_LEADER_REQUIRED);
    }
}
