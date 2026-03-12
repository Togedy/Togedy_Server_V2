package com.togedy.togedy_server_v2.domain.planner.exception;

import com.togedy.togedy_server_v2.global.error.CustomException;
import com.togedy.togedy_server_v2.global.error.ErrorCode;

public class TimerNotOwnedException extends CustomException {

    public TimerNotOwnedException() {
        super(ErrorCode.TIMER_NOT_OWNED);
    }
}
