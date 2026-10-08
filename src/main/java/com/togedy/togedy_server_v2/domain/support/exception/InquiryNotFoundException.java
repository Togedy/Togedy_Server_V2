package com.togedy.togedy_server_v2.domain.support.exception;

import com.togedy.togedy_server_v2.global.enums.ErrorCode;
import com.togedy.togedy_server_v2.global.exception.CustomException;

public class InquiryNotFoundException extends CustomException {

    public InquiryNotFoundException() {
        super(ErrorCode.INQUIRY_NOT_FOUND);
    }
}
