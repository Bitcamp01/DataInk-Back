package com.bit.datainkback.service.impl;


import com.bit.datainkback.common.FileUtils;
import com.bit.datainkback.dto.NoticeDto;
import com.bit.datainkback.dto.NoticeFileDto;
import com.bit.datainkback.dto.NotificationDto;
import com.bit.datainkback.entity.Notice;
import com.bit.datainkback.entity.Notification;
import com.bit.datainkback.entity.User;
import com.bit.datainkback.enums.NotificationType;
import com.bit.datainkback.repository.NoticeFileRepository;
import com.bit.datainkback.repository.NoticeRepository;
import com.bit.datainkback.repository.NotificationRepository;
import com.bit.datainkback.service.NoticeService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.AbstractRedisAsyncCommands;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
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
    private final NotificationRepository notificationRepository;

    @Autowired
    private RedisTemplate<String, NotificationDto> redisTemplate;

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

        // 2. 알림 생성 및 저장
        Notification notification = Notification.builder()
                .user(user)  // 알림 수신자 - 필요에 따라 다른 사용자로 변경 가능
                .content("새로운 공지가 등록되었습니다: " + notice.getTitle())  // 알림 내용
                .notificationType(NotificationType.NOTICE)  // 알림 유형 설정
                .isRead(false)  // 읽음 여부 기본값
                .createdAt(LocalDateTime.now())  // 알림 생성일시
                .relatedId(notice.getNoticeId())  // 관련 공지사항 ID
                .build();

        notificationRepository.save(notification);  // 알림 저장

        // Redis에 알림 추가 및 확인
        ListOperations<String, NotificationDto> listOps = redisTemplate.opsForList();
        String redisKey = "notifications:" + user.getUserId();

        try {
            listOps.rightPush(redisKey, notification.toDto());
        } catch (Exception e) {
            System.err.println("Redis에 알림 저장 실패: " + e.getMessage());
        }
        Long notificationCount = listOps.size(redisKey); // Redis에 저장된 알림 개수 확인

        // 최대 100개 알림만 유지
        if (notificationCount != null && notificationCount > 100) {
            listOps.leftPop(redisKey); // 오래된 알림 제거
        }

        // Redis에 저장된 최신 알림 확인
        NotificationDto latestNotification = listOps.index(redisKey, -1);
        if (latestNotification != null) {
            System.out.println("Redis에 저장된 최신 알림: " + latestNotification.getContent());
        }

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
    public NoticeDto modify(NoticeDto noticeDto, Long userId) {
        return null;
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

        if (noticeDto.getContent() == null) {
            throw new RuntimeException("Content cannot be null");
        }

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
