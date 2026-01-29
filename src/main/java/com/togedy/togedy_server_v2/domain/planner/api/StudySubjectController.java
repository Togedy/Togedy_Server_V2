package com.togedy.togedy_server_v2.domain.planner.api;

import com.togedy.togedy_server_v2.domain.planner.application.StudySubjectService;
import com.togedy.togedy_server_v2.domain.planner.dto.GetStudySubjectResponse;
import com.togedy.togedy_server_v2.domain.planner.dto.PatchReorderRequest;
import com.togedy.togedy_server_v2.domain.planner.dto.PatchStudySubjectRequest;
import com.togedy.togedy_server_v2.domain.planner.dto.PostStudySubjectRequest;
import com.togedy.togedy_server_v2.global.response.ApiResponse;
import com.togedy.togedy_server_v2.global.security.AuthUser;
import com.togedy.togedy_server_v2.global.util.ApiUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/planners/subjects")
public class StudySubjectController {

    private final StudySubjectService studySubjectService;

    @Operation(summary = "스터디 과목 생성", description = "스터디 과목을 생성한다.")
    @PostMapping("")
    public ApiResponse<Void> createStudySubject(@RequestBody PostStudySubjectRequest request,
                                                 @AuthenticationPrincipal AuthUser user) {
        studySubjectService.generateStudySubject(request, user.getId());
        return ApiUtil.successOnly();
    }

    @Operation(summary = "스터디 과목 조회", description = "해당 유저가 보유한 스터디 과목을 조회한다.")
    @GetMapping("")
    public ApiResponse<List<GetStudySubjectResponse>> readAllStudySubjects(@AuthenticationPrincipal AuthUser user) {
        List<GetStudySubjectResponse> response = studySubjectService.findAllStudySubjectsByUserId(user.getId());
        return ApiUtil.success(response);
    }

    @Operation(summary = "스터디 과목 수정", description = "해당 스터디 과목의 정보를 수정한다.")
    @PatchMapping("/{subjectId}")
    public ApiResponse<Void> updateStudySubject(@RequestBody PatchStudySubjectRequest request,
                                                 @PathVariable Long subjectId,
                                                 @AuthenticationPrincipal AuthUser user) {
        studySubjectService.modifyStudySubject(request, subjectId, user.getId());
        return ApiUtil.successOnly();
    }

    @Operation(summary = "스터디 과목 삭제", description = "해당 스터디 과목을 삭제한다.")
    @DeleteMapping("/{subjectId}")
    public ApiResponse<Void> deleteStudySubject(@PathVariable Long subjectId,
                                            @AuthenticationPrincipal AuthUser user) {
        studySubjectService.removeStudySubject(subjectId, user.getId());
        return ApiUtil.successOnly();
    }

    @Operation(summary = "스터디 과목 순서 이동", description = "해당 스터디 과목의 정렬 순서를 변경한다.")
    @PatchMapping("/{subjectId}/move")
    public ApiResponse<Void> moveStudySubject(@RequestBody PatchReorderRequest request,
                                               @PathVariable Long subjectId,
                                               @AuthenticationPrincipal AuthUser user) {
        studySubjectService.reorderStudySubject(request, subjectId, user.getId());
        return ApiUtil.successOnly();
    }

}
