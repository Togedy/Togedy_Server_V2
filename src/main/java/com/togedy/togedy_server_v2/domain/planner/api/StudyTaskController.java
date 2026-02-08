package com.togedy.togedy_server_v2.domain.planner.api;

import com.togedy.togedy_server_v2.domain.planner.application.StudyTaskService;
import com.togedy.togedy_server_v2.domain.planner.dto.PutStudyTaskRequest;
import com.togedy.togedy_server_v2.global.response.ApiResponse;
import com.togedy.togedy_server_v2.global.security.AuthUser;
import com.togedy.togedy_server_v2.global.util.ApiUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/planners/daily/tasks")
public class StudyTaskController {

    private final StudyTaskService studyTaskService;

    @Operation(summary = "스터디 테스크 생성 및 수정", description = "스터디 테스크를 생성 또는 수정한다.(자동 저장)")
    @PutMapping("")
    public ApiResponse<Long> upsertStudyTask(@RequestBody PutStudyTaskRequest request,
                                                @AuthenticationPrincipal AuthUser user) {
        Long taskId = studyTaskService.upsertStudyTask(request, user.getId());
        return ApiUtil.success(taskId);
    }

    @Operation(summary = "스터디 테스크 삭제", description = "해당 스터디 테스크를 삭제한다.")
    @DeleteMapping("/{taskId}")
    public ApiResponse<Void> deleteTask(@PathVariable Long taskId, @AuthenticationPrincipal AuthUser user) {
        studyTaskService.deleteStudyTask(taskId, user.getId());
        return ApiUtil.successOnly();
    }


}
