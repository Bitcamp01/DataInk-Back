package com.bit.datainkback.repository;

import com.bit.datainkback.entity.Notice;
import com.bit.datainkback.repository.custom.NoticeRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice, Long>, NoticeRepositoryCustom {


}
