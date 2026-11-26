package com.togedy.togedy_server_v2.domain.badge.exception;

import com.togedy.togedy_server_v2.global.enums.ErrorCode;
import com.togedy.togedy_server_v2.global.exception.CustomException;

public class RuleHolidayNotModifiableException extends CustomException {

    public RuleHolidayNotModifiableException() {
        super(ErrorCode.RULE_HOLIDAY_NOT_MODIFIABLE);
    }
}
