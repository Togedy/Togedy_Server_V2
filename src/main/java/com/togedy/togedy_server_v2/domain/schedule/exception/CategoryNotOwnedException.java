package com.togedy.togedy_server_v2.domain.schedule.exception;

import com.togedy.togedy_server_v2.global.error.CustomException;
import com.togedy.togedy_server_v2.global.error.ErrorCode;

public class CategoryNotOwnedException extends CustomException {

    public CategoryNotOwnedException() {
        super(ErrorCode.CATEGORY_NOT_OWNED);
    }

}
