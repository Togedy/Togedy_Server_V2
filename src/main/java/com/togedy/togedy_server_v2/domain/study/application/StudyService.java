package com.togedy.togedy_server_v2.domain.study.application;

import com.togedy.togedy_server_v2.domain.study.dao.StudyRepository;
import com.togedy.togedy_server_v2.domain.study.dao.UserStudyRepository;
import com.togedy.togedy_server_v2.domain.study.dto.GetStudyResponse;
import com.togedy.togedy_server_v2.domain.study.dto.PatchStudyInfoRequest;
import com.togedy.togedy_server_v2.domain.study.dto.PostStudyRequest;
import com.togedy.togedy_server_v2.domain.study.entity.Study;
import com.togedy.togedy_server_v2.domain.study.entity.UserStudy;
import com.togedy.togedy_server_v2.domain.study.enums.StudyRole;
import com.togedy.togedy_server_v2.domain.study.exception.DuplicateStudyNameException;
import com.togedy.togedy_server_v2.domain.study.exception.StudyLeaderRequiredException;
import com.togedy.togedy_server_v2.domain.study.exception.StudyNotFoundException;
import com.togedy.togedy_server_v2.domain.study.exception.UserStudyNotFoundException;
import com.togedy.togedy_server_v2.domain.user.dao.UserRepository;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import com.togedy.togedy_server_v2.global.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository studyRepository;
    private final UserStudyRepository userStudyRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    @Transactional
    public void generateStudy(PostStudyRequest request, Long userId) {
        if (request.getIsDuplicate()) {
            throw new DuplicateStudyNameException();
        }

        String studyImageUrl = null;

        if (!request.getStudyImage().isEmpty()) {
            studyImageUrl = s3Service.uploadFile(request.getStudyImage());
        }

        Study study = Study.builder()
                .name(request.getStudyName())
                .description(request.getStudyDescription())
                .memberLimit(request.getMemberLimit())
                .tag(request.getStudyTag())
                .imageUrl(studyImageUrl)
                .password(request.getPassword())
                .tier("tier")
                .build();

        Study savedStudy = studyRepository.save(study);

        UserStudy userStudy = UserStudy.builder()
                .userId(userId)
                .studyId(savedStudy.getId())
                .role(StudyRole.LEADER.name())
                .build();

        userStudyRepository.save(userStudy);
    }

    public GetStudyResponse findStudy(Long studyId) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(StudyNotFoundException::new);

        User leader = userRepository.findByStudyIdAndRole(study.getId(), StudyRole.LEADER.name());

        return GetStudyResponse.of(study, leader);
    }

    public void removeStudy(Long studyId, Long userId) {
        UserStudy userStudy = userStudyRepository.findByStudyIdAndUserId(studyId, userId)
                .orElseThrow(UserStudyNotFoundException::new);

        if (!userStudy.getRole().equals(StudyRole.LEADER.name())) {
            throw new StudyLeaderRequiredException();
        }

        Study study = studyRepository.findById(studyId)
                .orElseThrow(StudyNotFoundException::new);

        studyRepository.delete(study);

        List<UserStudy> userStudyList = userStudyRepository.findAllByStudyId(studyId);

        userStudyRepository.deleteAll(userStudyList);
    }

    public void modifyStudyInfo(PatchStudyInfoRequest request, Long studyId, Long userId) {
        UserStudy userStudy = userStudyRepository.findByStudyIdAndUserId(studyId, userId)
                .orElseThrow(UserStudyNotFoundException::new);

        if (!userStudy.getRole().equals(StudyRole.LEADER.name())) {
            throw new StudyLeaderRequiredException();
        }

        Study study = studyRepository.findById(studyId)
                .orElseThrow(StudyNotFoundException::new);

        String studyImageUrl = null;

        if (!request.getStudyImage().isEmpty()) {
            studyImageUrl = s3Service.uploadFile(request.getStudyImage());
            s3Service.deleteFile(study.getImageUrl());
        }

        if (!request.getStudyName().isEmpty() && request.getIsDuplicate()) {
            throw new DuplicateStudyNameException();
        }

        study.updateInfo(request, studyImageUrl);
        studyRepository.save(study);
    }
}
