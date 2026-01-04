package com.togedy.togedy_server_v2.domain.planner.exception;

import com.togedy.togedy_server_v2.global.error.CustomException;
import com.togedy.togedy_server_v2.global.error.ErrorCode;

public class InvalidStudyCategoryReorderException extends CustomException {
    public InvalidStudyCategoryReorderException() { super(ErrorCode.INVALID_STUDY_CATEGORY_REORDER); }
}
