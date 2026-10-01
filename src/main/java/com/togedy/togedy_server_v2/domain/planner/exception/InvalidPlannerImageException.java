package com.togedy.togedy_server_v2.domain.planner.exception;

import com.togedy.togedy_server_v2.global.enums.ErrorCode;
import com.togedy.togedy_server_v2.global.exception.CustomException;

public class InvalidPlannerImageException extends CustomException {

    public InvalidPlannerImageException() {
        super(ErrorCode.INVALID_PLANNER_IMAGE);
    }
}
