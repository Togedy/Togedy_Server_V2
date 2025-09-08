package com.togedy.togedy_server_v2.domain.study.api;

import com.togedy.togedy_server_v2.domain.study.application.StudyService;
import com.togedy.togedy_server_v2.domain.study.dto.GetStudyNameDuplicateResponse;
import com.togedy.togedy_server_v2.domain.study.dto.GetStudyResponse;
import com.togedy.togedy_server_v2.domain.study.dto.PatchStudyInfoRequest;
import com.togedy.togedy_server_v2.domain.study.dto.PatchStudyMemberLimitRequest;
import com.togedy.togedy_server_v2.domain.study.dto.PostStudyMemberRequest;
import com.togedy.togedy_server_v2.domain.study.dto.PostStudyRequest;
import com.togedy.togedy_server_v2.global.response.ApiResponse;
import com.togedy.togedy_server_v2.global.security.AuthUser;
import com.togedy.togedy_server_v2.global.util.ApiUtil;
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

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;

    @PostMapping(value = "/studies", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Void> createStudy(@ModelAttribute PostStudyRequest request,
                                         @AuthenticationPrincipal AuthUser user) {
        studyService.generateStudy(request, user.getId());
        return ApiUtil.successOnly();
    }

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

    @GetMapping("/studies/{studyId}")
    public ApiResponse<GetStudyResponse> readStudy(@PathVariable Long studyId) {
        GetStudyResponse response = studyService.findStudy(studyId);
        return ApiUtil.success(response);
    }

    @GetMapping("/studies/duplicate")
    public ApiResponse<GetStudyNameDuplicateResponse> readStudyNameDuplicate(
            @RequestParam("name") String name
    )
    {
        GetStudyNameDuplicateResponse response = studyService.findStudyNameDuplicate(name);
        return ApiUtil.success(response);
    }

    @PatchMapping(value = "/studies/{studyId}/information", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Void> updateStudyInfo(
            @PathVariable Long studyId,
            @ModelAttribute PatchStudyInfoRequest request,
            @AuthenticationPrincipal AuthUser user)
    {
        studyService.modifyStudyInfo(request, studyId, user.getId());
        return ApiUtil.successOnly();
    }

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

    @DeleteMapping("/studies/{studyId}")
    public ApiResponse<Void> deleteStudy(@PathVariable Long studyId,
                                         @AuthenticationPrincipal AuthUser user) {
        studyService.removeStudy(studyId, user.getId());
        return ApiUtil.successOnly();
    }

    @DeleteMapping("/studies/{studyId}/members/me")
    public ApiResponse<Void> deleteMyStudyMembership(@PathVariable Long studyId,
                                                     @AuthenticationPrincipal AuthUser user) {
        studyService.removeMyStudyMembership(studyId, user.getId());
        return ApiUtil.successOnly();
    }

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
