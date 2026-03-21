package com.togedy.togedy_server_v2.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ImageCategory {

    PROFILE("profile"),
    STUDY("study"),
    PLANNER("planner");

    private final String prefix;
}
