package com.bit.datainkback.service;

import com.bit.datainkback.dto.NoticeDto;
import com.bit.datainkback.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface NoticeService {

   Page<NoticeDto> post(NoticeDto noticeDto, MultipartFile[] uploadFiles, User user, Pageable pageable);

//   Page<NoticeDto> findAll(String searchCondition, String searchKeyword, Pageable pageale);

   NoticeDto modify(NoticeDto noticeDto, MultipartFile[] uploadFiles, MultipartFile[] changeFiles, String originFiles, Long userId);

   void deleteById(Long noticeId);
}
