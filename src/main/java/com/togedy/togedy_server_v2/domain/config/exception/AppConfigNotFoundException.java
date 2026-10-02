package com.togedy.togedy_server_v2.domain.config.exception;

import com.togedy.togedy_server_v2.global.enums.ErrorCode;
import com.togedy.togedy_server_v2.global.exception.CustomException;

public class AppConfigNotFoundException extends CustomException {

    public AppConfigNotFoundException() {
        super(ErrorCode.APP_CONFIG_NOT_FOUND);
    }
}
