package com.togedy.togedy_server_v2.domain.university.api;

import com.togedy.togedy_server_v2.domain.university.application.UniversityService;
import com.togedy.togedy_server_v2.domain.university.dto.GetUniversityScheduleResponse;
import com.togedy.togedy_server_v2.domain.university.dto.PostUniversityScheduleRequest;
import com.togedy.togedy_server_v2.global.response.ApiResponse;
import com.togedy.togedy_server_v2.global.security.AuthUser;
import com.togedy.togedy_server_v2.global.util.ApiUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v2/calendars/universities")
@RequiredArgsConstructor
public class UniversityController {

    private final UniversityService universityService;

    @GetMapping("")
    public ApiResponse<List<GetUniversityScheduleResponse>> readUniversityScheduleList(
            @RequestParam(name = "name") String name,
            @AuthenticationPrincipal AuthUser user){
        List<GetUniversityScheduleResponse> response = universityService.findUniversityScheduleList(name, user.getId());
        return ApiUtil.success(response);
    }

    @PostMapping("")
    public ApiResponse<Void> createUserUniversitySchedule(
            @RequestBody PostUniversityScheduleRequest request,
            @AuthenticationPrincipal AuthUser user) {
        universityService.generateUserUniversitySchedule(request, user.getId());
        return ApiUtil.successOnly();
    }

    @DeleteMapping("")
    public ApiResponse<Void> deleteUserUniversitySchedule(
            @RequestParam List<Long> universityScheduleIdList,
            @AuthenticationPrincipal AuthUser user) {
        universityService.removeUserUniversitySchedule(universityScheduleIdList, user.getId());
        return ApiUtil.successOnly();
    }
}
