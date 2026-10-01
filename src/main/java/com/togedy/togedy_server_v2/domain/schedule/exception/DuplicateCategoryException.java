package com.togedy.togedy_server_v2.domain.schedule.exception;

import com.togedy.togedy_server_v2.global.enums.ErrorCode;
import com.togedy.togedy_server_v2.global.exception.CustomException;

public class DuplicateCategoryException extends CustomException {

    public DuplicateCategoryException() {
        super(ErrorCode.DUPLICATE_CATEGORY);
    }
}
