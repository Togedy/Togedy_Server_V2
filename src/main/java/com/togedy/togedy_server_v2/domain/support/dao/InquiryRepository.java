package com.togedy.togedy_server_v2.domain.support.dao;

import com.togedy.togedy_server_v2.domain.support.entity.Inquiry;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    List<Inquiry> findAllByOrderByCreatedAtDesc();
}
