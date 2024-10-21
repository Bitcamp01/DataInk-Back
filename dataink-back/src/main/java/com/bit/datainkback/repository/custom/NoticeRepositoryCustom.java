package com.bit.datainkback.repository.custom;

import com.bit.datainkback.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NoticeRepositoryCustom {
    Page<Notice> searchAll(String searchCondition, String searchKeyword, Pageable pageable);
}
