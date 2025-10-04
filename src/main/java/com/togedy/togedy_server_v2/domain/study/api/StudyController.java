package com.togedy.togedy_server_v2.domain.study.api;

import com.togedy.togedy_server_v2.domain.study.application.StudyService;
import com.togedy.togedy_server_v2.domain.study.dto.GetMyStudyInfoResponse;
import com.togedy.togedy_server_v2.domain.study.dto.GetStudyAttendanceResponse;
import com.togedy.togedy_server_v2.domain.study.dto.GetStudyMemberManagementResponse;
import com.togedy.togedy_server_v2.domain.study.dto.GetStudyMemberPlannerResponse;
import com.togedy.togedy_server_v2.domain.study.dto.GetStudyMemberProfileResponse;
import com.togedy.togedy_server_v2.domain.study.dto.GetStudyMemberResponse;
import com.togedy.togedy_server_v2.domain.study.dto.GetStudyMemberStudyTimeResponse;
import com.togedy.togedy_server_v2.domain.study.dto.GetStudyNameDuplicateResponse;
import com.togedy.togedy_server_v2.domain.study.dto.GetStudyResponse;
import com.togedy.togedy_server_v2.domain.study.dto.GetStudySearchResponse;
import com.togedy.togedy_server_v2.domain.study.dto.PatchPlannerVisibilityRequest;
import com.togedy.togedy_server_v2.domain.study.dto.PatchStudyInfoRequest;
import com.togedy.togedy_server_v2.domain.study.dto.PatchStudyMemberLimitRequest;
import com.togedy.togedy_server_v2.domain.study.dto.PostStudyMemberRequest;
import com.togedy.togedy_server_v2.domain.study.dto.PostStudyRequest;
import com.togedy.togedy_server_v2.global.response.ApiResponse;
import com.togedy.togedy_server_v2.global.security.AuthUser;
import com.togedy.togedy_server_v2.global.util.ApiUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2")
@Tag(name = "Study", description = "스터디 API")
public class StudyController {

    private final StudyService studyService;

    @Operation(summary = "스터디 생성", description = "스터디를 생성한다.")
    @PostMapping(value = "/studies", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Void> createStudy(@ModelAttribute PostStudyRequest request,
                                         @AuthenticationPrincipal AuthUser user) {
        studyService.generateStudy(request, user.getId());
        return ApiUtil.successOnly();
    }

    @Operation(summary = "스터디 입장", description = "스터디에 입장한다.")
    @PostMapping("/studies/{studyId}/members")
    public ApiResponse<Void> createStudyMember(
            @RequestBody(required = false) PostStudyMemberRequest request,
            @PathVariable Long studyId,
            @AuthenticationPrincipal AuthUser user
    )
    {
        studyService.registerStudyMember(request, studyId, user.getId());
        return ApiUtil.successOnly();
    }

    @Operation(summary = "스터디 정보 조회", description = "스터디 정보를 단일 조회한다.")
    @GetMapping("/studies/{studyId}")
    public ApiResponse<GetStudyResponse> readStudyInfo(@PathVariable Long studyId,
                                                       @AuthenticationPrincipal AuthUser user) {
        GetStudyResponse response = studyService.findStudyInfo(studyId, user.getId());
        return ApiUtil.success(response);
    }

    @Operation(summary = "스터디 이름 중복 검사", description = "스터디 생성 시 이름을 중복 검사한다.")
    @GetMapping("/studies/duplicate")
    public ApiResponse<GetStudyNameDuplicateResponse> readStudyNameDuplicate(
            @RequestParam("name") String name
    )
    {
        GetStudyNameDuplicateResponse response = studyService.findStudyNameDuplicate(name);
        return ApiUtil.success(response);
    }

    @Operation(summary = "스터디 그룹원 조회", description = "스터디 그룹원을 조회한다.")
    @GetMapping("/studies/{studyId}/members")
    public ApiResponse<List<GetStudyMemberResponse>> readStudyMember(
            @PathVariable Long studyId,
            @AuthenticationPrincipal AuthUser user
    ) {
        List<GetStudyMemberResponse> response = studyService.findStudyMember(studyId, user.getId());
        return ApiUtil.success(response);
    }

    @Operation(summary = "본인 스터디 조회", description = "본인의 스터디 관련 정보를 조회한다.")
    @GetMapping("/users/me/studies")
    public ApiResponse<GetMyStudyInfoResponse> readMyStudyInfo(@AuthenticationPrincipal AuthUser user) {
        GetMyStudyInfoResponse response = studyService.findMyStudyInfo(user.getId());
        return ApiUtil.success(response);
    }

    @Operation(summary = "스터디 탐색", description = "스터디를 탐색한다.")
    @GetMapping("/studies")
    public ApiResponse<GetStudySearchResponse> readStudySearch(
            @RequestParam(name = "tag", required = false) String tag,
            @RequestParam(name = "filter", required = false, defaultValue = "latest") String filter,
            @RequestParam(name = "joinable", required = false, defaultValue = "false") boolean joinable,
            @RequestParam(name = "challenge", required = false, defaultValue = "false") boolean challenge,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @AuthenticationPrincipal AuthUser user
    )
    {
        GetStudySearchResponse response =
                studyService.findStudySearch(tag, filter, joinable, challenge, page, size, user.getId());
        return ApiUtil.success(response);
    }

    @Operation(summary = "스터디 그룹원 프로필 조회", description = "스터디 그룹원의 프로필을 조회한다.")
    @GetMapping("/studies/{studyId}/members/{userId}/profiles")
    public ApiResponse<GetStudyMemberProfileResponse> readStudyMemberProfile(
            @PathVariable Long studyId,
            @PathVariable Long userId,
            @AuthenticationPrincipal AuthUser user
    )
    {
        GetStudyMemberProfileResponse response =
                studyService.findStudyMemberProfile(studyId, userId, user.getId());
        return ApiUtil.success(response);
    }

    @Operation(summary = "스터디 그룹원 공부시간 조회", description = "스터디 그룹원의 공부시간 통계를 조회한다.")
    @GetMapping("/studies/{studyId}/members/{userId}/study-times")
    public ApiResponse<GetStudyMemberStudyTimeResponse> readStudyMemberStudyTime(
            @PathVariable Long studyId,
            @PathVariable Long userId,
            @AuthenticationPrincipal AuthUser user
    )
    {
        GetStudyMemberStudyTimeResponse response =
                studyService.findStudyMemberStudyTime(studyId, userId, user.getId());
        return ApiUtil.success(response);
    }

    @Operation(summary = "스터디 그룹원 플래너 조회", description = "스터디 그룹원의 플래너를 조회한다.")
    @GetMapping("/studyies/{studyId}/members/{userId}/planners")
    public ApiResponse<GetStudyMemberPlannerResponse> readStudyMemberPlanner(
            @PathVariable Long studyId,
            @PathVariable Long userId,
            @AuthenticationPrincipal AuthUser user
    )
    {
        GetStudyMemberPlannerResponse response =
                studyService.findStudyMemberPlanner(studyId, userId, user.getId());
        return ApiUtil.success(response);
    }

    @Operation(summary = "스터디 멤버 관리 조회", description = "스터디 멤버 관리를 조회한다.")
    @GetMapping("/studies/{studyId}/members/management")
    public ApiResponse<List<GetStudyMemberManagementResponse>> readStudyMemberManagement(
            @PathVariable Long studyId,
            @AuthenticationPrincipal AuthUser user
    )
    {
        List<GetStudyMemberManagementResponse> response =
                studyService.findStudyMemberManagement(studyId, user.getId());
        return ApiUtil.success(response);
    }

    @Operation(summary = "스터디 출석부 조회", description = "스터디 출석부를 조회한다.")
    @GetMapping("/studies/{studyId}/members/attendance")
    public ApiResponse<List<GetStudyAttendanceResponse>> readStudyAttendance(
            @RequestParam(name = "startDate") LocalDate startDate,
            @RequestParam(name = "endDate") LocalDate endDate,
            @PathVariable Long studyId
    )
    {
        List<GetStudyAttendanceResponse> response = studyService.findStudyAttendance(startDate, endDate, studyId);
        return ApiUtil.success(response);
    }

    @Operation(summary = "스터디 정보 수정", description = "스터디 정보를 수정한다.")
    @PatchMapping(value = "/studies/{studyId}/information", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Void> updateStudyInfo(
            @PathVariable Long studyId,
            @ModelAttribute PatchStudyInfoRequest request,
            @AuthenticationPrincipal AuthUser user)
    {
        studyService.modifyStudyInfo(request, studyId, user.getId());
        return ApiUtil.successOnly();
    }

    @Operation(summary = "스터디 최대 인원 수정", description = "스터디의 최대 인원을 수정한다.")
    @PatchMapping("/studies/{studyId}/members/limit")
    public ApiResponse<Void> updateStudyMemberLimit(
            @PathVariable Long studyId,
            @RequestBody PatchStudyMemberLimitRequest request,
            @AuthenticationPrincipal AuthUser user
    )
    {
        studyService.modifyStudyMemberLimit(request, studyId, user.getId());
        return ApiUtil.successOnly();
    }

    @Operation(summary = "스터디 리더 변경", description = "스터디 리더를 변경한다.")
    @PatchMapping("/studies/{studyId}/members/{userId}/leader")
    public ApiResponse<Void> updateStudyLeader(
            @PathVariable Long studyId,
            @PathVariable Long userId,
            @AuthenticationPrincipal AuthUser user
    )
    {
        studyService.modifyStudyLeader(studyId, userId, user.getId());
        return ApiUtil.successOnly();
    }

    @Operation(summary = "플래너 공개 수정", description = "본인의 플래너 공개 여부를 수정한다.")
    @PatchMapping("/studies/{studyId}/members/{userId}/planners/visibility")
    public ApiResponse<Void> updatePlannerVisibility(
            @PathVariable Long studyId,
            @PathVariable Long userId,
            @RequestBody PatchPlannerVisibilityRequest request,
            @AuthenticationPrincipal AuthUser user
    )
    {
        studyService.modifyPlannerVisibility(request, studyId, userId, user.getId());
        return ApiUtil.successOnly();
    }

    @Operation(summary = "스터디 제거", description = "해당 스터디를 제거한다.")
    @DeleteMapping("/studies/{studyId}")
    public ApiResponse<Void> deleteStudy(@PathVariable Long studyId,
                                         @AuthenticationPrincipal AuthUser user) {
        studyService.removeStudy(studyId, user.getId());
        return ApiUtil.successOnly();
    }

    @Operation(summary = "스터디 퇴장", description = "해당 스터디에서 퇴장한다.")
    @DeleteMapping("/studies/{studyId}/members/me")
    public ApiResponse<Void> deleteMyStudyMembership(@PathVariable Long studyId,
                                                     @AuthenticationPrincipal AuthUser user) {
        studyService.removeMyStudyMembership(studyId, user.getId());
        return ApiUtil.successOnly();
    }

    @Operation(summary = "스터디 추방", description = "스터디에서 해당 유저를 추방한다.")
    @DeleteMapping("/studies/{studyId}/members/{userId}")
    public ApiResponse<Void> deleteStudyMember(
            @PathVariable Long studyId,
            @PathVariable Long userId,
            @AuthenticationPrincipal AuthUser user)
    {
        studyService.removeStudyMember(studyId, userId, user.getId());
        return ApiUtil.successOnly();
    }
}
