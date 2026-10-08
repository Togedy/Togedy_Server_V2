package com.togedy.togedy_server_v2.domain.planner.exception;

import com.togedy.togedy_server_v2.global.enums.ErrorCode;
import com.togedy.togedy_server_v2.global.exception.CustomException;

public class StudyTaskNotOwnedException extends CustomException {
    public StudyTaskNotOwnedException() {
        super(ErrorCode.STUDY_TASK_NOT_OWNED);
    }
}
