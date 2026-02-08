package com.togedy.togedy_server_v2.domain.planner.exception;

import com.togedy.togedy_server_v2.global.error.CustomException;
import com.togedy.togedy_server_v2.global.error.ErrorCode;

public class StudyTaskNotFoundException extends CustomException {
    public StudyTaskNotFoundException() { super(ErrorCode.STUDY_TASK_NOT_FOUND); }
}
