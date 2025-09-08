package com.togedy.togedy_server_v2.domain.study.application;

import com.togedy.togedy_server_v2.domain.study.dao.StudyRepository;
import com.togedy.togedy_server_v2.domain.study.dao.UserStudyRepository;
import com.togedy.togedy_server_v2.domain.study.dto.GetStudyNameDuplicateResponse;
import com.togedy.togedy_server_v2.domain.study.dto.GetStudyResponse;
import com.togedy.togedy_server_v2.domain.study.dto.PatchStudyInfoRequest;
import com.togedy.togedy_server_v2.domain.study.dto.PatchStudyMemberLimitRequest;
import com.togedy.togedy_server_v2.domain.study.dto.PostStudyMemberRequest;
import com.togedy.togedy_server_v2.domain.study.dto.PostStudyRequest;
import com.togedy.togedy_server_v2.domain.study.entity.Study;
import com.togedy.togedy_server_v2.domain.study.entity.UserStudy;
import com.togedy.togedy_server_v2.domain.study.enums.StudyRole;
import com.togedy.togedy_server_v2.domain.study.enums.StudyType;
import com.togedy.togedy_server_v2.domain.study.exception.DuplicateStudyNameException;
import com.togedy.togedy_server_v2.domain.study.exception.StudyLeaderRequiredException;
import com.togedy.togedy_server_v2.domain.study.exception.StudyMemberRequiredException;
import com.togedy.togedy_server_v2.domain.study.exception.StudyNotFoundException;
import com.togedy.togedy_server_v2.domain.study.exception.StudyPasswordMismatchException;
import com.togedy.togedy_server_v2.domain.study.exception.StudyPasswordRequiredException;
import com.togedy.togedy_server_v2.domain.study.exception.UserStudyNotFoundException;
import com.togedy.togedy_server_v2.domain.study.util.InvitationCodeUtil;
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
        if (Boolean.TRUE.equals(request.getDuplicate())) {
            throw new DuplicateStudyNameException();
        }

        String imageUrl = null;
        String invitationCode;
        String type = StudyType.NORMAL.name();

        if (request.getStudyImage() != null) {
            imageUrl = s3Service.uploadFile(request.getStudyImage());
        }

        if (request.getGoalTime() != null) {
            type = StudyType.CHALLENGE.name();
        }

        do {
            invitationCode = InvitationCodeUtil.generateInvitationCode();
        } while (studyRepository.existsByInvitationCode(invitationCode));

        Study study = Study.builder()
                .name(request.getStudyName())
                .description(request.getStudyDescription())
                .memberLimit(request.getStudyMemberLimit())
                .tag(request.getStudyTag())
                .imageUrl(imageUrl)
                .type(type)
                .goalTime(request.getGoalTime())
                .invitationCode(invitationCode)
                .password(request.getStudyPassword())
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

        if (request.getStudyImage() != null) {
            studyImageUrl = s3Service.uploadFile(request.getStudyImage());
            s3Service.deleteFile(study.getImageUrl());
        }

        if (!request.getStudyName().isEmpty() && Boolean.TRUE.equals(request.getDuplicate())) {
            throw new DuplicateStudyNameException();
        }

        study.updateInfo(request, studyImageUrl);
        studyRepository.save(study);
    }

    @Transactional
    public void modifyStudyMemberLimit(PatchStudyMemberLimitRequest request, Long studyId, Long userId) {
        UserStudy userStudy = userStudyRepository.findByStudyIdAndUserId(studyId, userId)
                .orElseThrow(UserStudyNotFoundException::new);

        if (!userStudy.getRole().equals(StudyRole.LEADER.name())) {
            throw new StudyLeaderRequiredException();
        }

        Study study = studyRepository.findById(studyId)
                .orElseThrow(StudyNotFoundException::new);

        study.updateMemberLimit(request);
        studyRepository.save(study);
    }

    public GetStudyNameDuplicateResponse findStudyNameDuplicate(String studyName) {
        boolean isDuplicate = studyRepository.existsByName(studyName);

        return GetStudyNameDuplicateResponse.from(isDuplicate);
    }

    @Transactional
    public void registerStudyMember(PostStudyMemberRequest request, Long studyId, Long userId) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(StudyNotFoundException::new);

        if (study.getPassword() != null) {
            if (request.getStudyPassword() == null) {
                throw new StudyPasswordRequiredException();
            }
            if (!study.getPassword().equals(request.getStudyPassword())) {
                throw new StudyPasswordMismatchException();
            }
        }

        UserStudy userStudy = UserStudy.builder()
                .userId(userId)
                .studyId(studyId)
                .role(StudyRole.MEMBER.name())
                .build();

        userStudyRepository.save(userStudy);

        study.increaseMemberCount();
        studyRepository.save(study);
    }

    @Transactional
    public void removeMyStudyMembership(Long studyId, Long userId) {
        UserStudy userStudy = userStudyRepository.findByStudyIdAndUserId(studyId, userId)
                .orElseThrow(UserStudyNotFoundException::new);

        Study study = studyRepository.findById(studyId)
                .orElseThrow(StudyNotFoundException::new);

        if (userStudy.getRole().equals(StudyRole.MEMBER.name())) {
            throw new StudyMemberRequiredException();
        }

        study.decreaseMemberCount();
        studyRepository.save(study);
        userStudyRepository.delete(userStudy);
    }
}
