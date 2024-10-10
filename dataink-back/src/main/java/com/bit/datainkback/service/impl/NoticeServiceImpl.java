package com.bit.datainkback.service.impl;


import com.bit.datainkback.common.FileUtils;
import com.bit.datainkback.dto.NoticeDto;
import com.bit.datainkback.dto.NoticeFileDto;
import com.bit.datainkback.entity.Notice;
import com.bit.datainkback.repository.NoticeRepository;
import com.bit.datainkback.service.NoticeService;
import io.lettuce.core.AbstractRedisAsyncCommands;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Slf4j

public class NoticeServieImpl implements NoticeService {
    private final NoticeRepository noticeRepository;
    private final FileUtils fileUtils;

    @Override
    public Page<NoticeDto> post(NoticeDto noticeDto, MultipartFile uploadFiles, Long userId, Pageable pageable) {
        noticeDto.setCreated(LocalDateTime.now());

        Notice notice = noticeDto.toEntity(userId);

        if (uploadFiles != null) {
            Arrays.stream(uploadFiles).forEach(multipartFile -> {
               if(multipartFile.getFileOriginName() != null &&
               !multipartFile.getFileOriginName().equalsIgnoreCase("")){

                   NoticeFileDto noticeFileDto = fileUtils.parserFileInfo(multipartFile,"notice/");

                   notice.getNoticeFileList().add(noticeFileDto.toEntity(notice));
               }
           });
       }
        AbstractRedisAsyncCommands<Object, Object> noticeRepository;
        noticeRepository.save(notice);

       return noticeRepository.findAll(pageable).map(Notice::toDto);
    }

    @Override
    public Page<NoticeDto> findAll(String searchCondition, String searchKeyword, Pageable pageale) {
        return null;
    }
}
