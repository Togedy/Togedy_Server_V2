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
