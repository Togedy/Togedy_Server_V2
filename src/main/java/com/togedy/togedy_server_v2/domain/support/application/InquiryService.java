package com.togedy.togedy_server_v2.domain.support.application;

import com.togedy.togedy_server_v2.domain.support.dao.InquiryRepository;
import com.togedy.togedy_server_v2.domain.support.dto.PostInquiryRequest;
import com.togedy.togedy_server_v2.domain.support.entity.Inquiry;
import com.togedy.togedy_server_v2.domain.user.dao.UserRepository;
import com.togedy.togedy_server_v2.domain.user.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final UserRepository userRepository;
    private final InquiryRepository inquiryRepository;

    @Transactional
    public void generateInquiry(PostInquiryRequest request, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException();
        }

        Inquiry inquiry = Inquiry.builder()
                .userId(userId)
                .inquiryType(request.getInquiryType())
                .content(request.getInquiryContent())
                .replyEmail(request.getReplyEmail())
                .build();

        inquiryRepository.save(inquiry);
    }
}
