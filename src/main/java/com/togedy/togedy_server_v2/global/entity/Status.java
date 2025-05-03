package com.togedy.togedy_server_v2.global.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Status {
    ACTIVE("CREATED"),
    INACTIVE("INACTIVE");

    private final String status;

}
