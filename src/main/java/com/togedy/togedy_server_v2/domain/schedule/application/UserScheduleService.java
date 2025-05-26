package com.togedy.togedy_server_v2.domain.schedule.application;

import com.togedy.togedy_server_v2.domain.schedule.Exception.CategoryNotFoundException;
import com.togedy.togedy_server_v2.domain.schedule.Exception.UserScheduleNotFoundException;
import com.togedy.togedy_server_v2.domain.schedule.Exception.UserScheduleNotOwnedException;
import com.togedy.togedy_server_v2.domain.schedule.dto.GetUserScheduleResponse;
import com.togedy.togedy_server_v2.domain.schedule.dao.CategoryRepository;
import com.togedy.togedy_server_v2.domain.schedule.dao.UserScheduleRepository;
import com.togedy.togedy_server_v2.domain.schedule.dto.PatchUserScheduleRequest;
import com.togedy.togedy_server_v2.domain.schedule.dto.PostUserScheduleRequest;
import com.togedy.togedy_server_v2.domain.schedule.entity.Category;
import com.togedy.togedy_server_v2.domain.schedule.entity.UserSchedule;
import com.togedy.togedy_server_v2.domain.user.dao.UserRepository;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import com.togedy.togedy_server_v2.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserScheduleService {

    private final UserScheduleRepository userScheduleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    /**
     * 개인 일정을 생성한다. 해당 일정을 D-Day 설정하고자 한다면 기존의 D-Day 설정된 일정 상태를 변경한다.
     *
     * @param request   개인 일정 생성 DTO
     * @param userId    유저ID
     */
    @Transactional
    public void generateUserSchedule(PostUserScheduleRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(CategoryNotFoundException::new);

        if (request.isDDay()) {
            clearDdaySchedule(userId);
        }

        UserSchedule userSchedule = UserSchedule.builder()
                .user(user)
                .category(category)
                .name(request.getUserScheduleName())
                .memo(request.getMemo())
                .startDate(request.getStartDate())
                .startTime(request.getStartTime())
                .endDate(request.getEndDate())
                .endTime(request.getEndTime())
                .dDay(request.isDDay())
                .build();

        userScheduleRepository.save(userSchedule);
    }

    /**
     * 유저가 보유 중인 개인 일정을 단일 조회한다.
     *
     * @param userScheduleId    조회할 개인 일정ID
     * @param userId            유저ID
     * @return                  개인 일정 정보 DTO
     */
    @Transactional(readOnly = true)
    public GetUserScheduleResponse findUserSchedule(Long userScheduleId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        UserSchedule userSchedule = userScheduleRepository.findById(userScheduleId)
                .orElseThrow(UserScheduleNotFoundException::new);

        if (!userSchedule.getUser().getId().equals(userId)) {
            throw new UserScheduleNotOwnedException();
        }

        return GetUserScheduleResponse.from(userSchedule);
    }

    /**
     * 유저가 보유 중인 개인 일정 정보를 수정한다.
     *
     * @param request           개인 일정 수정 DTO
     * @param userScheduleId    수정할 개인 일정ID
     * @param userId            유저ID
     */
    @Transactional
    public void modifyUserSchedule(PatchUserScheduleRequest request, Long userScheduleId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        UserSchedule userSchedule = userScheduleRepository.findById(userScheduleId)
                .orElseThrow(UserScheduleNotFoundException::new);
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(CategoryNotFoundException::new);

        if (!userSchedule.getUser().getId().equals(userId)) {
            throw new UserScheduleNotOwnedException();
        }

        if (request.getDDay()) {
            clearDdaySchedule(userId);
        }

        userSchedule.update(request);
        userSchedule.update(category);
        userScheduleRepository.save(userSchedule);
    }

    /**
     * 유저가 보유 중인 개인 일정을 제거한다.
     *
     * @param userScheduleId    제거할 개인 일정 ID
     * @param userId            유저ID
     */
    @Transactional
    public void removeUserSchedule(Long userScheduleId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        UserSchedule userSchedule = userScheduleRepository.findById(userScheduleId)
                .orElseThrow(UserScheduleNotFoundException::new);

        if (!userSchedule.getUser().getId().equals(userId)) {
            throw new UserScheduleNotOwnedException();
        }

        userScheduleRepository.delete(userSchedule);
    }

    /**
     * D-Day 설정이 되어 있는 개인 일정 상태를 변경한다.
     *
     * @param userId    유저ID
     */
    private void clearDdaySchedule(Long userId) {
        Optional<UserSchedule> userSchedule = userScheduleRepository.findByUserIdAndDDayTrue(userId);
        userSchedule.ifPresent(UserSchedule::cancelDday);
    }
}
