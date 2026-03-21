package com.togedy.togedy_server_v2.domain.support.application;

import com.togedy.togedy_server_v2.domain.support.dao.InquiryRepository;
import com.togedy.togedy_server_v2.domain.support.dto.GetInquiryResponse;
import com.togedy.togedy_server_v2.domain.support.dto.InquiryDto;
import com.togedy.togedy_server_v2.domain.support.dto.PostInquiryRequest;
import com.togedy.togedy_server_v2.domain.support.entity.Inquiry;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;

    /**
     * 사용자의 문의를 생성한다.
     * <p>
     * 요청된 문의 유형, 내용, 회신 이메일 정보를 기반으로 문의 엔티티를 생성하여 저장한다. 생성된 문의는 관리자가 확인 및 답변할 수 있도록 보관된다.
     * </p>
     *
     * @param request 문의 생성 요청 DTO
     * @param userId  문의를 작성한 사용자 ID
     */
    @Transactional
    public void generateInquiry(PostInquiryRequest request, Long userId) {
        Inquiry inquiry = Inquiry.builder()
                .userId(userId)
                .inquiryType(request.getInquiryType())
                .content(request.getInquiryContent())
                .replyEmail(request.getReplyEmail())
                .build();

        inquiryRepository.save(inquiry);
    }

    /**
     * 문의 목록을 페이징하여 조회한다.
     * <p>
     * 요청한 페이지와 크기 정보를 기반으로 문의를 최신순(createdAt 내림차순)으로 조회한다. Slice 기반 조회를 사용하여 다음 페이지 존재 여부를 함께 반환한다.
     * </p>
     *
     * @param page 조회할 페이지 번호 (1부터 시작)
     * @param size 페이지당 조회 개수
     * @return 문의 목록 조회 응답 DTO
     */
    public GetInquiryResponse findInquiries(int page, int size) {
        PageRequest pageRequest = PageRequest.of(
                Math.max(page - 1, 0),
                size,
                Sort.by(Sort.Direction.DESC, "createdAt") // 최신순
        );

        Slice<Inquiry> inquirySlice = inquiryRepository.findAll(pageRequest);

        List<InquiryDto> inquiries = inquirySlice.getContent()
                .stream()
                .map(InquiryDto::from)
                .toList();

        return GetInquiryResponse.of(inquirySlice.hasNext(), inquiries);
    }
}
