package com.togedy.togedy_server_v2.domain.schedule.Exception;

import com.togedy.togedy_server_v2.global.error.CustomException;
import com.togedy.togedy_server_v2.global.error.ErrorCode;

public class DuplicateCategoryException extends CustomException {

    public DuplicateCategoryException() {
        super(ErrorCode.DUPLICATE_CATEGORY);
    }
}
