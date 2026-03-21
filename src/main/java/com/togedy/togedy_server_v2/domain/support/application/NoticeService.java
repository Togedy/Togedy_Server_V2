package com.togedy.togedy_server_v2.domain.support.application;

import com.togedy.togedy_server_v2.domain.support.dao.NoticeRepository;
import com.togedy.togedy_server_v2.domain.support.dto.GetNoticeResponse;
import com.togedy.togedy_server_v2.domain.support.dto.GetNoticesResponse;
import com.togedy.togedy_server_v2.domain.support.dto.PatchNoticeRequest;
import com.togedy.togedy_server_v2.domain.support.dto.PostNoticeRequest;
import com.togedy.togedy_server_v2.domain.support.entity.Notice;
import com.togedy.togedy_server_v2.domain.support.exception.NoticeNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    /**
     * 공지사항 목록을 조회한다.
     * <p>
     * 등록일(createdAt) 기준 내림차순으로 정렬하여 최신 공지사항부터 반환한다.
     * </p>
     *
     * @return 공지사항 목록 응답 DTO 리스트
     */
    public List<GetNoticesResponse> findNotices() {
        List<Notice> notices = noticeRepository.findAll(Sort.by(Direction.DESC, "createdAt"));

        return notices.stream()
                .map(GetNoticesResponse::from)
                .toList();
    }

    /**
     * 단일 공지사항을 조회한다.
     * <p>
     * 공지사항 ID에 해당하는 데이터를 조회하며, 존재하지 않는 경우 예외를 발생시킨다.
     * </p>
     *
     * @param noticeId 조회 대상 공지사항 ID
     * @return 공지사항 상세 조회 응답 DTO
     * @throws NoticeNotFoundException 해당 공지사항이 존재하지 않는 경우
     */
    public GetNoticeResponse findNotice(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(NoticeNotFoundException::new);

        return GetNoticeResponse.from(notice);
    }

    /**
     * 새로운 공지사항을 생성한다.
     * <p>
     * 요청된 제목과 내용을 기반으로 공지사항을 생성하여 저장한다. 작성자 정보는 요청한 사용자 ID로 기록된다.
     * </p>
     *
     * @param request 공지사항 생성 요청 DTO
     * @param userId  공지사항을 작성한 사용자 ID
     */
    @Transactional
    public void generateNotice(PostNoticeRequest request, Long userId) {
        Notice notice = Notice.builder()
                .userId(userId)
                .title(request.getNoticeTitle())
                .content(request.getNoticeContent())
                .build();

        noticeRepository.save(notice);
    }

    /**
     * 기존 공지사항을 수정한다.
     * <p>
     * 공지사항 ID에 해당하는 엔티티를 조회한 뒤, 요청된 제목과 내용으로 수정한다.
     * </p>
     *
     * @param request  공지사항 수정 요청 DTO
     * @param noticeId 수정 대상 공지사항 ID
     * @throws NoticeNotFoundException 해당 공지사항이 존재하지 않는 경우
     */
    @Transactional
    public void modifyNotice(PatchNoticeRequest request, Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(NoticeNotFoundException::new);

        notice.update(request.getNoticeTitle(), request.getNoticeContent());
    }

    /**
     * 공지사항을 삭제한다.
     * <p>
     * 공지사항 ID에 해당하는 엔티티를 조회한 뒤 삭제한다. 존재하지 않는 경우 예외를 발생시킨다.
     * </p>
     *
     * @param noticeId 삭제 대상 공지사항 ID
     * @throws NoticeNotFoundException 해당 공지사항이 존재하지 않는 경우
     */
    @Transactional
    public void removeNotice(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(NoticeNotFoundException::new);

        noticeRepository.delete(notice);
    }
}
