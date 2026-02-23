package com.togedy.togedy_server_v2.domain.planner.exception;

import com.togedy.togedy_server_v2.global.error.CustomException;
import com.togedy.togedy_server_v2.global.error.ErrorCode;

public class InvalidStudyTaskNameException extends CustomException {
    public InvalidStudyTaskNameException() { super(ErrorCode.INVALID_STUDY_TASK_NAME); }
}
