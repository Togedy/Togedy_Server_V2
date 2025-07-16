package com.togedy.togedy_server_v2.domain.university.api;

import com.togedy.togedy_server_v2.domain.university.application.UniversityService;
import com.togedy.togedy_server_v2.domain.university.dto.GetUniversityResponse;
import com.togedy.togedy_server_v2.global.response.ApiResponse;
import com.togedy.togedy_server_v2.global.security.AuthUser;
import com.togedy.togedy_server_v2.global.util.ApiUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v2/calendars/universities")
@RequiredArgsConstructor
@Tag(name = "University Schedule", description = "대학 일정 API")
public class UniversityController {

    private final UniversityService universityService;

    @Operation(summary = "대학 일정 조회", description = "대학 일정 정보를 조회한다.")
    @GetMapping("")
    public ApiResponse<List<GetUniversityResponse>> readUniversityScheduleList(
            @RequestParam(name = "name", defaultValue = "대학교") String name,
            @RequestParam(name = "admission-type", required = false) String admissionType,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @AuthenticationPrincipal AuthUser user){
        List<GetUniversityResponse> response =
                universityService.findUniversityList(name, admissionType, user.getId(), page, size).getContent();
        return ApiUtil.success(response);
    }

//    @Operation(summary = "대학 일정 추가", description = "해당 대학 일정들을 유저의 일정으로 추가한다.")
//    @PostMapping("")
//    public ApiResponse<Void> createUserUniversitySchedule(
//            @RequestBody PostUniversityScheduleRequest request,
//            @AuthenticationPrincipal AuthUser user) {
//        universityService.generateUserUniversitySchedule(request, user.getId());
//        return ApiUtil.successOnly();
//    }
//
//    @Operation(summary = "대학 일정 제거", description = "해당 대학 일정들을 유저 일정에서 제거한다.")
//    @DeleteMapping("")
//    public ApiResponse<Void> deleteUserUniversitySchedule(
//            @RequestParam List<Long> universityScheduleIdList,
//            @AuthenticationPrincipal AuthUser user) {
//        universityService.removeUserUniversitySchedule(universityScheduleIdList, user.getId());
//        return ApiUtil.successOnly();
//    }
}
