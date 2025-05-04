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

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserScheduleService {

    private final UserScheduleRepository userScheduleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public void generateUserSchedule(PostUserScheduleRequest request, Long userId) {
        //유저 예외 수정
        User user = userRepository.findById(userId)
                .orElseThrow(RuntimeException::new);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(CategoryNotFoundException::new);

        LocalDateTime startDate = LocalDateTime.parse(request.getStartDate());
        LocalDateTime endDate = LocalDateTime.parse(request.getEndDate());

        UserSchedule userSchedule = new UserSchedule(
                user,
                category,
                request.getUserScheduleName(),
                request.getMemo(),
                startDate,
                request.isAllDayStart(),
                endDate,
                request.isAllDayEnd(),
                request.isDDay()
        );

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

        userSchedule.update(request);
        userSchedule.update(category);
        userScheduleRepository.save(userSchedule);
    }
}
