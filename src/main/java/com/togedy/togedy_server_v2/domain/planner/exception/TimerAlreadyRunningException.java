package com.togedy.togedy_server_v2.domain.planner.exception;

import com.togedy.togedy_server_v2.global.enums.ErrorCode;
import com.togedy.togedy_server_v2.global.exception.CustomException;

public class TimerAlreadyRunningException extends CustomException {

    public TimerAlreadyRunningException() {
        super(ErrorCode.TIMER_ALREADY_RUNNING);
    }
}
