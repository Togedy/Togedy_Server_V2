package com.togedy.togedy_server_v2.domain.university.application;

import com.togedy.togedy_server_v2.domain.university.dao.UniversityAdmissionMethodRepository;
import com.togedy.togedy_server_v2.domain.university.dao.UniversityRepository;
import com.togedy.togedy_server_v2.domain.university.dao.UserUniversityMethodRepository;
import com.togedy.togedy_server_v2.domain.university.dto.GetUniversityScheduleResponse;
import com.togedy.togedy_server_v2.domain.university.dto.GetUniversityResponse;
import com.togedy.togedy_server_v2.domain.university.dao.UniversityScheduleRepository;
import com.togedy.togedy_server_v2.domain.university.dto.PostUniversityAdmissionMethodRequest;
import com.togedy.togedy_server_v2.domain.university.dto.UniversityAdmissionMethodDto;
import com.togedy.togedy_server_v2.domain.university.dto.UniversityScheduleDto;
import com.togedy.togedy_server_v2.domain.university.entity.UniversityAdmissionMethod;
import com.togedy.togedy_server_v2.domain.university.entity.University;
import com.togedy.togedy_server_v2.domain.university.entity.UserUniversityMethod;
import com.togedy.togedy_server_v2.domain.university.exception.UniversityNotFoundException;
import com.togedy.togedy_server_v2.domain.university.exception.UniversityScheduleNotFoundException;
import com.togedy.togedy_server_v2.domain.user.dao.UserRepository;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import com.togedy.togedy_server_v2.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UniversityService {

    private final UniversityScheduleRepository universityScheduleRepository;
    private final UniversityAdmissionMethodRepository universityAdmissionMethodRepository;
    private final UserRepository userRepository;
    private final UniversityRepository universityRepository;
    private final UserUniversityMethodRepository userUniversityMethodRepository;

    private static final int ACADEMIC_YEAR = 2025;

    /***
     * 대학명, 입시 전형에 해당하는 대학 정보를 조회한다.
     *
     * @param name              대학명
     * @param admissionType     입시 전형(수시, 정시)
     * @param userId            유저ID
     * @param page              페이지
     * @param size              크기
     * @return                  대학별 정보
     */
    @Transactional(readOnly = true)
    public Page<GetUniversityResponse> findUniversityList(
            String name,
            String admissionType,
            Long userId,
            int page,
            int size
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        Page<University> universities = universityRepository.findByNameAndType(name, admissionType, pageRequest);

        return universities.map(university -> {
            int count = universityAdmissionMethodRepository.countByUniversity(university);

            List<UniversityAdmissionMethod> addedUniversityAdmissionMethodList
                    = universityAdmissionMethodRepository.findAllByUniversityAndUser(university, user);

            return GetUniversityResponse.of(
                    university,
                    count,
                    addedUniversityAdmissionMethodList
            );
        });
    }

    /***
     * 해당 대학의 전형별 일정을 조회한다.
     *
     * @param universityId  대학ID
     * @param userId        유저ID
     * @return              해당 대학의 전형별 일정
     */
    @Transactional(readOnly = true)
    public GetUniversityScheduleResponse findUniversitySchedule(Long universityId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        University university = universityRepository.findById(universityId)
                .orElseThrow(UniversityNotFoundException::new);

        List<UniversityAdmissionMethod> addedUniversityAdmissionMethodList =
                universityAdmissionMethodRepository.findAllByUniversityAndUser(university, user);

        List<UniversityAdmissionMethod> universityAdmissionMethodList
                = universityAdmissionMethodRepository.findAllByUniversity(university);

        List<UniversityAdmissionMethodDto> universityAdmissionMethodDtoList = new ArrayList<>();

        for (UniversityAdmissionMethod universityAdmissionMethod : universityAdmissionMethodList) {
            List<UniversityScheduleDto> universityScheduleDtoList =
                    universityAdmissionMethod.getUniversityAdmissionScheduleList().stream()
                            .map(universityAdmissionSchedule ->
                                    UniversityScheduleDto.from(universityAdmissionSchedule.getUniversitySchedule()))
                            .toList();

            universityAdmissionMethodDtoList.add(UniversityAdmissionMethodDto.of(universityAdmissionMethod, universityScheduleDtoList));
        }

        return GetUniversityScheduleResponse.of(university, addedUniversityAdmissionMethodList, universityAdmissionMethodDtoList);
    }

    /***
     * 유저가 대학 전형들을 추가한다.
     *
     * @param request   대학 전형ID 리스트
     * @param userId    유저ID
     */
    @Transactional
    public void generateUserUniversityAdmissionMethod(PostUniversityAdmissionMethodRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        List<Long> universityAdmissionMethodIdList = request.getUniversityAdmissionMethodIdList();

        List<UniversityAdmissionMethod> universityScheduleList
                = universityAdmissionMethodRepository.findAllById(universityAdmissionMethodIdList);

        if (universityScheduleList.size() != universityAdmissionMethodIdList.size()) {
            throw new UniversityScheduleNotFoundException();
        }

        List<UserUniversityMethod> userUniversityScheduleList = universityScheduleList.stream()
                .map(us -> UserUniversityMethod.builder()
                        .user(user)
                        .universityAdmissionMethod(us)
                        .build())
                .toList();

        userUniversityMethodRepository.saveAll(userUniversityScheduleList);
    }

    /***
     * 유저가 보유한 대학 전형을 제거한다.
     * @param universityScheduleIdList  대학 전형ID 리스트
     * @param userId                    유저ID
     */
    @Transactional
    public void removeUserUniversityMethod(List<Long> universityScheduleIdList, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        List<UserUniversityMethod> userUniversityMethodList =
                userUniversityMethodRepository
                        .findByUserAndUniversityAdmissionMethodIdIn(user, universityScheduleIdList);

        if (userUniversityMethodList.isEmpty()) {
            throw new UniversityScheduleNotFoundException();
        }

        userUniversityMethodRepository.deleteAll(userUniversityMethodList);
    }

}
