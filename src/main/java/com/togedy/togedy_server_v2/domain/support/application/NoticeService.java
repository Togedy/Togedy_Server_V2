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

    public List<GetNoticesResponse> findNotices() {
        List<Notice> notices = noticeRepository.findAll(Sort.by(Direction.DESC, "createdAt"));

        return notices.stream()
                .map(GetNoticesResponse::from)
                .toList();
    }

    public GetNoticeResponse findNotice(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(NoticeNotFoundException::new);

        return GetNoticeResponse.from(notice);
    }

    @Transactional
    public void generateNotice(PostNoticeRequest request, Long userId) {
        Notice notice = Notice.builder()
                .userId(userId)
                .title(request.getNoticeTitle())
                .content(request.getNoticeContent())
                .build();

        noticeRepository.save(notice);
    }

    @Transactional
    public void modifyNotice(PatchNoticeRequest request, Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(NoticeNotFoundException::new);

        notice.update(request.getNoticeTitle(), request.getNoticeContent());
    }

    @Transactional
    public void removeNotice(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(NoticeNotFoundException::new);

        noticeRepository.delete(notice);
    }
}
