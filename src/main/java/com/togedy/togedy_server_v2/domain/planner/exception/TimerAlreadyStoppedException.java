package com.togedy.togedy_server_v2.domain.planner.exception;

import com.togedy.togedy_server_v2.global.error.CustomException;
import com.togedy.togedy_server_v2.global.error.ErrorCode;

public class TimerAlreadyStoppedException extends CustomException {

    public TimerAlreadyStoppedException() {
        super(ErrorCode.TIMER_ALREADY_STOPPED);
    }
}
