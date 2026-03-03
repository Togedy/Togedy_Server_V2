package com.togedy.togedy_server_v2.domain.study.exception;

import com.togedy.togedy_server_v2.global.error.CustomException;
import com.togedy.togedy_server_v2.global.error.ErrorCode;

public class NotChallengeStudyException extends CustomException {

    public NotChallengeStudyException() {
        super(ErrorCode.NOT_CHALLENGE_STUDY);
    }

}
