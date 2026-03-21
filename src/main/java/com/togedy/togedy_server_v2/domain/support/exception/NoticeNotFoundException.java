package com.togedy.togedy_server_v2.domain.support.exception;

import com.togedy.togedy_server_v2.global.error.CustomException;
import com.togedy.togedy_server_v2.global.error.ErrorCode;

public class NoticeNotFoundException extends CustomException {

    public NoticeNotFoundException() {
        super(ErrorCode.NOTICE_NOT_FOUND);
    }
}
