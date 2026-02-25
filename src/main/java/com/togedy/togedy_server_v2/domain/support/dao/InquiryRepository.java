package com.togedy.togedy_server_v2.domain.support.dao;

import com.togedy.togedy_server_v2.domain.support.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
}
