package com.bit.datainkback.repository;

import com.bit.datainkback.entity.Notice;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    <T> Optional<T> searchAll(String searchCondition, String searchKeyword, Pageable pageable);
}
