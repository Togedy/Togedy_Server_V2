package com.togedy.togedy_server_v2.domain.planner.api;

import com.togedy.togedy_server_v2.domain.planner.application.StudyTaskService;
import com.togedy.togedy_server_v2.domain.planner.dto.GetDailyPlannerTaskResponse;
import com.togedy.togedy_server_v2.domain.planner.dto.PatchStudyTaskCheckRequest;
import com.togedy.togedy_server_v2.domain.planner.dto.PutStudyTaskRequest;
import com.togedy.togedy_server_v2.global.response.ApiResponse;
import com.togedy.togedy_server_v2.global.security.AuthUser;
import com.togedy.togedy_server_v2.global.util.ApiUtil;
import io.swagger.v3.oas.annotations.Operation;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/planners/daily/tasks")
public class StudyTaskController {

    private final StudyTaskService studyTaskService;

    @Operation(summary = "일간 플래너 테스크 조회", description = "날짜 기준 과목별 일간 플래너 테스크를 조회한다.")
    @GetMapping("")
    public ApiResponse<GetDailyPlannerTaskResponse> readDailyPlannerTasks(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @AuthenticationPrincipal AuthUser user
    ) {
        GetDailyPlannerTaskResponse response = studyTaskService.findDailyPlannerTasks(date, user.getId());
        return ApiUtil.success(response);
    }

    @Operation(summary = "스터디 테스크 생성 및 수정", description = "스터디 테스크를 생성 또는 수정한다.(자동 저장)")
    @PutMapping("")
    public ApiResponse<Long> upsertStudyTask(@RequestBody PutStudyTaskRequest request,
                                             @AuthenticationPrincipal AuthUser user) {
        Long taskId = studyTaskService.upsertStudyTask(request, user.getId());
        return ApiUtil.success(taskId);
    }

    @Operation(summary = "스터디 테스크 삭제", description = "해당 스터디 테스크를 삭제한다.")
    @DeleteMapping("/{taskId}")
    public ApiResponse<Void> deleteStudyTask(@PathVariable Long taskId,
                                             @AuthenticationPrincipal AuthUser user) {
        studyTaskService.deleteStudyTask(taskId, user.getId());
        return ApiUtil.successOnly();
    }

    @Operation(summary = "스터디 테스크 체크 및 미체크", description = "해당 스터디 테스크를 체크 또는 미체크 상태로 변경한다.")
    @PatchMapping("/{taskId}/check")
    public ApiResponse<Void> checkStudyTask(@PathVariable Long taskId,
                                            @RequestBody PatchStudyTaskCheckRequest request,
                                            @AuthenticationPrincipal AuthUser user) {
        studyTaskService.checkStudyTask(taskId, request.isChecked(), user.getId());
        return ApiUtil.successOnly();
    }

}
