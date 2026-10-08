package com.togedy.togedy_server_v2.global.exception;

import com.togedy.togedy_server_v2.global.enums.ErrorCode;

public class StorageUploadFailedException extends CustomException {

    public StorageUploadFailedException() {
        super(ErrorCode.STORAGE_UPLOAD_FAILED);
    }
}
