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
import com.togedy.togedy_server_v2.domain.user.dao.UserRepository;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import com.togedy.togedy_server_v2.domain.user.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserScheduleService {

    private final UserScheduleRepository userScheduleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    /**
     * 개인 일정을 생성한다. 해당 일정을 D-Day 설정하고자 한다면 기존의 D-Day 설정된 일정 상태를 변경한다.
     *
     * @param request 개인 일정 생성 DTO
     * @param userId  유저ID
     * @throws UserNotFoundException     해당 유저가 존재하지 않는 경우
     * @throws CategoryNotFoundException 요청한 카테고리가 존재하지 않는 경우
     * @throws CategoryNotOwnedException 요청한 카테고리가 해당 유저의 카테고리가 아닌 경우
     */
    @Transactional
    public void generateUserSchedule(PostUserScheduleRequest request, Long userId) {
        User user = findUserById(userId);
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
                .dDay(Boolean.TRUE.equals(request.getDDay()))
                .build();

        userScheduleRepository.save(userSchedule);
    }

    /**
     * 유저가 보유 중인 개인 일정을 단일 조회한다.
     *
     * @param userScheduleId 조회할 개인 일정ID
     * @param userId         유저ID
     * @return 개인 일정 정보 DTO
     * @throws UserScheduleNotFoundException 해당 개인 일정이 존재하지 않는 경우
     * @throws UserScheduleNotOwnedException 해당 개인 일정 소유자가 아닌 경우
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
     * @throws UserScheduleNotFoundException 해당 개인 일정이 존재하지 않는 경우
     * @throws UserScheduleNotOwnedException 해당 개인 일정 소유자가 아닌 경우
     * @throws CategoryNotFoundException     요청에 포함된 카테고리ID로 카테고리를 변경하려 했으나 해당 카테고리가 존재하지 않는 경우
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
     * @throws UserScheduleNotFoundException 해당 개인 일정이 존재하지 않는 경우
     * @throws UserScheduleNotOwnedException 해당 개인 일정 소유자가 아닌 경우
     */
    @Transactional
    public void removeUserSchedule(Long userScheduleId, Long userId) {
        UserSchedule userSchedule = findUserScheduleById(userScheduleId);
        validateUserScheduleOwnership(userId, userSchedule);
        userScheduleRepository.delete(userSchedule);
    }

    /**
     * 카테고리 하나를 조회한다.
     *
     * @param categoryId 카테고리ID
     * @return 카테고리
     * @throws CategoryNotFoundException 해당 카테고리가 존재하지 않는 경우
     */
    private Category findCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(CategoryNotFoundException::new);
    }

    /**
     * 새로 생성/수정되는 일정을 D-Day로 설정하려는 경우, 기존에 D-Day로 설정되어 있던 유저의 개인 일정을 D-Day 해제 상태로 변경한다. {@code isDday}가 {@code true}가
     * 아니면 아무 동작도 하지 않는다.
     *
     * @param isDday 신규/수정 일정의 D-Day 설정 여부
     * @param userId 유저ID
     */
    private void clearDdaySchedule(Boolean isDday, Long userId) {
        if (Boolean.TRUE.equals(isDday)) {
            userScheduleRepository.findByUserIdAndDDayTrue(userId)
                    .ifPresent(UserSchedule::cancelDday);
        }
    }

    /**
     * 해당 유저의 카테고리인지 검증한다.
     *
     * @param category 검증할 카테고리
     * @param user     유저
     * @throws CategoryNotOwnedException 해당 카테고리가 유저의 소유가 아닌 경우
     */
    private void validateCategoryOwnership(Category category, User user) {
        if (!category.getUser().getId().equals(user.getId())) {
            throw new CategoryNotOwnedException();
        }
    }

    /**
     * 개인 일정 소유를 검증한다.
     *
     * @param userId       유저ID
     * @param userSchedule 개인 일정
     * @throws UserScheduleNotOwnedException 해당 개인 일정 소유자가 아닌 경우
     */
    private void validateUserScheduleOwnership(Long userId, UserSchedule userSchedule) {
        if (!userSchedule.getUser().getId().equals(userId)) {
            throw new UserScheduleNotOwnedException();
        }
    }

    /**
     * 요청에 카테고리ID가 포함된 경우에만 개인 일정의 카테고리를 변경한다.
     *
     * @param request      개인 일정 수정 DTO
     * @param userSchedule 수정할 개인 일정
     * @throws CategoryNotFoundException 요청한 카테고리가 존재하지 않는 경우
     */
    private void modifyCategory(PatchUserScheduleRequest request, UserSchedule userSchedule) {
        if (request.getCategoryId() != null) {
            Category category = findCategoryById(request.getCategoryId());
            userSchedule.updateCategory(category);
        }
    }

    /**
     * 개인 일정 하나를 조회한다.
     *
     * @param userScheduleId 개인 일정ID
     * @return 개인 일정
     * @throws UserScheduleNotFoundException 해당 개인 일정이 존재하지 않는 경우
     */
    private UserSchedule findUserScheduleById(Long userScheduleId) {
        return userScheduleRepository.findById(userScheduleId)
                .orElseThrow(UserScheduleNotFoundException::new);
    }

    /**
     * 유저 하나를 조회한다.
     *
     * @param userId 유저ID
     * @return 유저
     * @throws UserNotFoundException 해당 유저가 존재하지 않는 경우
     */
    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }
}
