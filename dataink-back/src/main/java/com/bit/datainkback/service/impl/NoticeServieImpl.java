package com.bit.datainkback.service.impl;


import com.bit.datainkback.dto.NoticeDto;
import com.bit.datainkback.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j

public class NoticeServieImpl implements NoticeService {

    @Override
    public Page<NoticeDto> post(NoticeDto noticeDto, MultipartFile uploadFiles, Long userId, Pageable pageable) {
        return null;
    }
}
