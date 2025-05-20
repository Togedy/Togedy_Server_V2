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

    @Transactional
    public void generateUserSchedule(PostUserScheduleRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(RuntimeException::new);

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

    @Transactional(readOnly = true)
    public GetUserScheduleResponse findUserSchedule(Long userScheduleId, Long userId) {
        UserSchedule userSchedule = userScheduleRepository.findById(userScheduleId)
                .orElseThrow(UserScheduleNotFoundException::new);

        if (!userSchedule.getUser().getId().equals(userId)) {
            throw new UserScheduleNotOwnedException();
        }

        return GetUserScheduleResponse.from(userSchedule);
    }

    @Transactional
    public void modifyUserSchedule(PatchUserScheduleRequest request, Long userScheduleId, Long userId) {
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

    @Transactional
    public void removeUserSchedule(Long userScheduleId, Long userId) {
        UserSchedule userSchedule = userScheduleRepository.findById(userScheduleId)
                .orElseThrow(UserScheduleNotFoundException::new);

        if (!userSchedule.getUser().getId().equals(userId)) {
            throw new UserScheduleNotOwnedException();
        }

        userScheduleRepository.delete(userSchedule);
    }

    private void clearDdaySchedule(Long userId) {
        Optional<UserSchedule> userSchedule = userScheduleRepository.findByUserIdAndDDayTrue(userId);
        userSchedule.ifPresent(UserSchedule::cancleDday);
    }
}
