package com.togedy.togedy_server_v2.domain.support.application;

import com.togedy.togedy_server_v2.domain.support.dao.NoticeRepository;
import com.togedy.togedy_server_v2.domain.support.dto.GetNoticeResponse;
import com.togedy.togedy_server_v2.domain.support.dto.GetNoticesResponse;
import com.togedy.togedy_server_v2.domain.support.entity.Notice;
import com.togedy.togedy_server_v2.domain.support.exception.NoticeNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public List<GetNoticesResponse> findNotices() {
        List<Notice> notices = noticeRepository.findAll();

        return notices.stream()
                .map(GetNoticesResponse::from)
                .toList();
    }

    public GetNoticeResponse findNotice(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(NoticeNotFoundException::new);

        return GetNoticeResponse.from(notice);
    }
}
