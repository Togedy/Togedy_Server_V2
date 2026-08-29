package com.togedy.togedy_server_v2.domain.schedule.application;

import com.togedy.togedy_server_v2.domain.schedule.dao.CategoryRepository;
import com.togedy.togedy_server_v2.domain.schedule.dao.UserScheduleRepository;
import com.togedy.togedy_server_v2.domain.schedule.dto.request.PatchUserScheduleRequest;
import com.togedy.togedy_server_v2.domain.schedule.dto.request.PostUserScheduleRequest;
import com.togedy.togedy_server_v2.domain.schedule.dto.response.GetUserScheduleResponse;
import com.togedy.togedy_server_v2.domain.schedule.entity.Category;
import com.togedy.togedy_server_v2.domain.schedule.entity.UserSchedule;
import com.togedy.togedy_server_v2.domain.schedule.exception.CategoryNotFoundException;
import com.togedy.togedy_server_v2.domain.schedule.exception.CategoryNotOwnedException;
import com.togedy.togedy_server_v2.domain.schedule.exception.UserScheduleNotFoundException;
import com.togedy.togedy_server_v2.domain.schedule.exception.UserScheduleNotOwnedException;
import com.togedy.togedy_server_v2.domain.user.application.UserService;
import com.togedy.togedy_server_v2.domain.user.dao.UserRepository;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserScheduleService {

    private final UserScheduleRepository userScheduleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final UserService userService;

    /**
     * 개인 일정을 생성한다. 해당 일정을 D-Day 설정하고자 한다면 기존의 D-Day 설정된 일정 상태를 변경한다.
     *
     * @param request 개인 일정 생성 DTO
     * @param userId  유저ID
     */
    @Transactional
    public void generateUserSchedule(PostUserScheduleRequest request, Long userId) {
        User user = userService.loadUserById(userId);
        Category category = findCategoryById(request.getCategoryId());

        validateCategoryOwnership(category, user);
        clearDdaySchedule(request.getDDay(), userId);

        UserSchedule userSchedule = UserSchedule.builder()
                .user(user)
                .category(category)
                .name(request.getUserScheduleName())
                .memo(request.getMemo())
                .startDate(request.getStartDate())
                .startTime(request.getStartTime())
                .endDate(request.getEndDate())
                .endTime(request.getEndTime())
                .dDay(request.getDDay())
                .build();

        userScheduleRepository.save(userSchedule);
    }

    /**
     * 유저가 보유 중인 개인 일정을 단일 조회한다.
     *
     * @param userScheduleId 조회할 개인 일정ID
     * @param userId         유저ID
     * @return 개인 일정 정보 DTO
     */
    public GetUserScheduleResponse findUserSchedule(Long userScheduleId, Long userId) {
        UserSchedule userSchedule = findUserScheduleById(userScheduleId);
        validateUserScheduleOwnership(userId, userSchedule);
        return GetUserScheduleResponse.from(userSchedule);
    }

    /**
     * 유저가 보유 중인 개인 일정 정보를 수정한다.
     *
     * @param request        개인 일정 수정 DTO
     * @param userScheduleId 수정할 개인 일정ID
     * @param userId         유저ID
     */
    @Transactional
    public void modifyUserSchedule(PatchUserScheduleRequest request, Long userScheduleId, Long userId) {
        UserSchedule userSchedule = findUserScheduleById(userScheduleId);
        validateUserScheduleOwnership(userId, userSchedule);
        modifyCategory(request, userSchedule);
        clearDdaySchedule(request.getDDay(), userId);
        userSchedule.update(request);
        userScheduleRepository.save(userSchedule);
    }

    /**
     * 유저가 보유 중인 개인 일정을 제거한다.
     *
     * @param userScheduleId 제거할 개인 일정 ID
     * @param userId         유저ID
     */
    @Transactional
    public void removeUserSchedule(Long userScheduleId, Long userId) {
        UserSchedule userSchedule = findUserScheduleById(userScheduleId);
        validateUserScheduleOwnership(userId, userSchedule);
        userScheduleRepository.delete(userSchedule);
    }

    private Category findCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(CategoryNotFoundException::new);
    }

    /**
     * D-Day 설정이 되어 있는 개인 일정 상태를 변경한다.
     *
     * @param userId 유저ID
     */
    private void clearDdaySchedule(Boolean isDday, Long userId) {
        if (Boolean.TRUE.equals(isDday)) {
            userScheduleRepository.findByUserIdAndDDayTrue(userId)
                    .ifPresent(UserSchedule::cancelDday);
        }
    }

    private void validateCategoryOwnership(Category category, User user) {
        if (!category.getUser().equals(user)) {
            throw new CategoryNotOwnedException();
        }
    }

    private void validateUserScheduleOwnership(Long userId, UserSchedule userSchedule) {
        if (!userSchedule.getUser().getId().equals(userId)) {
            throw new UserScheduleNotOwnedException();
        }
    }

    private void modifyCategory(PatchUserScheduleRequest request, UserSchedule userSchedule) {
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(CategoryNotFoundException::new);
            userSchedule.updateCategory(category);
        }
    }

    private UserSchedule findUserScheduleById(Long userScheduleId) {
        return userScheduleRepository.findById(userScheduleId)
                .orElseThrow(UserScheduleNotFoundException::new);
    }
}
