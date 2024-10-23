package com.bit.datainkback.service.impl;


import com.bit.datainkback.common.FileUtils;
import com.bit.datainkback.dto.NoticeDto;
import com.bit.datainkback.dto.NoticeFileDto;
import com.bit.datainkback.entity.Notice;
import com.bit.datainkback.entity.User;
import com.bit.datainkback.repository.NoticeFileRepository;
import com.bit.datainkback.repository.NoticeRepository;
import com.bit.datainkback.service.NoticeService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.AbstractRedisAsyncCommands;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j

public class NoticeServiceImpl implements NoticeService {
    private final NoticeRepository noticeRepository;
    private final FileUtils fileUtils;
    private final NoticeFileRepository noticeFileRepository;

    @Override
    public Page<NoticeDto> post(NoticeDto noticeDto, MultipartFile[] uploadFiles, User user, Pageable pageable) {
        noticeDto.setCreated(new Timestamp(System.currentTimeMillis()));

        Notice notice = noticeDto.toEntity(user);

        if (uploadFiles != null) {
            Arrays.stream(uploadFiles).forEach(multipartFile -> {
               if(multipartFile.getOriginalFilename() != null &&
               !multipartFile.getOriginalFilename().equalsIgnoreCase("")){

                   NoticeFileDto noticeFileDto = fileUtils.parserFileInfo(multipartFile,"notice/");

                   notice.getNoticeFileList().add(noticeFileDto.toEntity(notice));
               }
           });
       }

        noticeRepository.save(notice);

       return noticeRepository.findAll(pageable).map(Notice::toDto);
    }



    @Override
    public Page<NoticeDto> findAll(String searchCondition, String searchKeyword, Pageable pageable) {
        return noticeRepository
                .searchAll(searchCondition, searchKeyword, pageable)
                .map(Notice::toDto);
    }

    @Override
    public void deleteById(Long id) {
        noticeRepository.deleteById(id);
    }

    @Override
    public NoticeDto modify(NoticeDto noticeDto, MultipartFile[] uploadFiles, MultipartFile[] changeFiles, String originFiles, Long userId) {
        List<NoticeFileDto> originFileList = new ArrayList<>();

        try {
            originFileList = new ObjectMapper().readValue(
                    originFiles,
                    new TypeReference<List<NoticeFileDto>>() {
                    }
            );
        } catch (IOException ie) {
            log.error("noticeService modify readvalue error : {}", ie.getMessage());
        }

        List<NoticeFileDto> uFileList = new ArrayList<>();

        if (originFileList.size() > 0) {
            originFileList.forEach(noticeFileDto -> {
                if (noticeFileDto.getFileStatus().equalsIgnoreCase("U")
                        && changeFiles != null) {
                    Arrays.stream(changeFiles).forEach(file -> {
                        if (noticeFileDto.getFileNewName().equalsIgnoreCase(file.getOriginalFilename())) {
                            NoticeFileDto updateNoticeFileDto = fileUtils.parserFileInfo(file, "/notice");

                            updateNoticeFileDto.setFileId(noticeFileDto.getFileId());
                            updateNoticeFileDto.setNoticeId(noticeDto.getNoticeId());
                            updateNoticeFileDto.setFileStatus("U");

                            uFileList.add(updateNoticeFileDto);

                        }
                    });
                } else if (noticeFileDto.getFileStatus().equalsIgnoreCase("D")) {
                    NoticeFileDto deleteNoticeFileDto = new NoticeFileDto();

                    deleteNoticeFileDto.setFileId(noticeFileDto.getFileId());
                    deleteNoticeFileDto.setNoticeId(noticeDto.getNoticeId());
                    deleteNoticeFileDto.setFileStatus("D");

                    fileUtils.deleteFile("notice/", noticeFileDto.getFileName());

                    uFileList.add(deleteNoticeFileDto);
                }

            });
        }

        if (uploadFiles != null && uploadFiles.length > 0) {
            Arrays.stream(uploadFiles).forEach(file -> {
                if (!file.getOriginalFilename().equalsIgnoreCase("")
                        && file.getOriginalFilename() != null) {
                    NoticeFileDto addNoticeFileDto = fileUtils.parserFileInfo(file, "/notice");

                    addNoticeFileDto.setNoticeId(noticeDto.getNoticeId());
                    addNoticeFileDto.setFileStatus("I");

                    uFileList.add(addNoticeFileDto);

                }
            });
        }

        Notice notice = noticeRepository.findById(noticeDto.getNoticeId()).orElseThrow(
                () -> new RuntimeException("notice not exist")
        );

        notice.setTitle(noticeDto.getTitle());
        notice.setContent(noticeDto.getContent());
        notice.setModdate(new Timestamp(System.currentTimeMillis()));

        uFileList.forEach(
                noticeFileDto -> {
                    if (noticeFileDto.getFileStatus().equalsIgnoreCase("U")
                            || noticeFileDto.getFileStatus().equalsIgnoreCase("I")) {
                        notice.getNoticeFileList().add(noticeFileDto.toEntity(notice));
                    } else if (noticeFileDto.getFileStatus().equalsIgnoreCase("D")) {
                        noticeFileRepository.delete(noticeFileDto.toEntity(notice));
                    }
                }
        );

        noticeRepository.save(notice);

        noticeRepository.flush();

        return noticeRepository.findById(noticeDto.getUserId()).orElseThrow(
                () -> new RuntimeException("notice not exist")
        ).toDto();
    }


}
