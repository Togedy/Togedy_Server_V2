package com.togedy.togedy_server_v2.domain.study.exception;

import com.togedy.togedy_server_v2.global.enums.ErrorCode;
import com.togedy.togedy_server_v2.global.exception.CustomException;

public class StudyLeaderNotFoundException extends CustomException {

    public StudyLeaderNotFoundException() {
        super(ErrorCode.STUDY_LEADER_NOT_FOUND);
    }
}
