package com.togedy.togedy_server_v2.domain.planner.exception;

import com.togedy.togedy_server_v2.global.error.CustomException;
import com.togedy.togedy_server_v2.global.error.ErrorCode;

public class InvalidPlannerImageException extends CustomException {

    public InvalidPlannerImageException() {
        super(ErrorCode.INVALID_PLANNER_IMAGE);
    }
}
