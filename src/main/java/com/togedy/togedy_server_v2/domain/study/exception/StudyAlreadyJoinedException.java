package com.togedy.togedy_server_v2.domain.study.exception;

import com.togedy.togedy_server_v2.global.error.CustomException;
import com.togedy.togedy_server_v2.global.error.ErrorCode;

public class StudyAlreadyJoinedException extends CustomException {

    public StudyAlreadyJoinedException() {
        super(ErrorCode.STUDY_ALREADY_JOINED);
    }
}
