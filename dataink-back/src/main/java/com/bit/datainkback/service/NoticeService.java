package com.bit.datainkback.service;

import com.bit.datainkback.dto.NoticeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface NoticeService {

   Page<NoticeDto> post(NoticeDto noticeDto, MultipartFile uploadFiles, Long userId, Pageable pageable);

   Page<NoticeDto> findAll(String searchCondition, String searchKeyword, Pageable pageale);

}
