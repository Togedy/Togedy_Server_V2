package com.togedy.togedy_server_v2.domain.planner.exception;

import com.togedy.togedy_server_v2.global.error.CustomException;
import com.togedy.togedy_server_v2.global.error.ErrorCode;

public class StudyCategoryNotOwnedException extends CustomException {

    public StudyCategoryNotOwnedException() { super(ErrorCode.STUDY_CATEGORY_NOT_OWNED); }
}
