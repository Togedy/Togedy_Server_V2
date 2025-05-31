package com.togedy.togedy_server_v2.domain.university.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostUniversityScheduleRequest {

    List<Long> universityScheduleIdList;
}
