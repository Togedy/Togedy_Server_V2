package com.togedy.togedy_server_v2.domain.study.application;

import com.togedy.togedy_server_v2.global.error.CustomException;
import com.togedy.togedy_server_v2.global.error.ErrorCode;

public class StudyLeaderNotFoundException extends CustomException {

    public StudyLeaderNotFoundException() {
        super(ErrorCode.STUDY_LEADER_NOT_FOUND);
    }
}
