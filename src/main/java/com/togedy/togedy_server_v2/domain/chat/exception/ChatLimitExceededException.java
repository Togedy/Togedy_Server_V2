package com.togedy.togedy_server_v2.domain.chat.exception;

import com.togedy.togedy_server_v2.global.enums.ErrorCode;
import com.togedy.togedy_server_v2.global.exception.CustomException;

public class ChatLimitExceededException extends CustomException {

    public ChatLimitExceededException() {
        super(ErrorCode.CHAT_LIMIT_EXCEEDED);
    }
}
