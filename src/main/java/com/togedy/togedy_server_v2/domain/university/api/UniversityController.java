package com.togedy.togedy_server_v2.domain.university.api;

import com.togedy.togedy_server_v2.domain.university.application.UniversityService;
import com.togedy.togedy_server_v2.domain.university.dto.GetUniversityScheduleResponse;
import com.togedy.togedy_server_v2.domain.university.dto.GetUniversityResponse;
import com.togedy.togedy_server_v2.domain.university.dto.PostUniversityAdmissionMethodRequest;
import com.togedy.togedy_server_v2.global.response.ApiResponse;
import com.togedy.togedy_server_v2.global.security.AuthUser;
import com.togedy.togedy_server_v2.global.util.ApiUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @Operation(summary = "대학 조회", description = "대학 정보를 조회한다.")
    @GetMapping("")
    public ApiResponse<List<GetUniversityResponse>> readUniversityList(
            @RequestParam(name = "name", defaultValue = "대학") String name,
            @RequestParam(name = "admission-type", defaultValue = "전체") String admissionType,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @AuthenticationPrincipal AuthUser user)
    {
        List<GetUniversityResponse> response =
                universityService.findUniversityList(name, admissionType, user.getId(), page, size);
        return ApiUtil.success(response);
    }

    @Operation(summary = "대학 전형별 일정 상세 조회", description = "대학 전형별 일정을 조회한다.")
    @GetMapping("/{universityId}/schedule")
    public ApiResponse<GetUniversityScheduleResponse> readUniversitySchedule(
            @PathVariable Long universityId,
            @AuthenticationPrincipal AuthUser user)
    {
        GetUniversityScheduleResponse response = universityService.findUniversitySchedule(universityId, user.getId());
        return ApiUtil.success(response);
    }

    @Operation(summary = "대학 일정 추가", description = "대학 전형을 추가한다.")
    @PostMapping("")
    public ApiResponse<Void> createUserUniversitySchedule(
            @RequestBody PostUniversityAdmissionMethodRequest request,
            @AuthenticationPrincipal AuthUser user)
    {
        universityService.generateUserUniversityAdmissionMethod(request, user.getId());
        return ApiUtil.successOnly();
    }

    @Operation(summary = "대학 전형 제거", description = "유저가 추가한 대학 전형을 제거한다.")
    @DeleteMapping("")
    public ApiResponse<Void> deleteUserUniversityMethod(
            @RequestParam Long universityAdmissionMethodId,
            @AuthenticationPrincipal AuthUser user)
    {
        universityService.removeUserUniversityMethod(universityAdmissionMethodId, user.getId());
        return ApiUtil.successOnly();
    }
}
