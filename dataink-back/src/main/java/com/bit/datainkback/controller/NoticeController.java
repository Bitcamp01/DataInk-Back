package com.bit.datainkback.controller;


import com.bit.datainkback.dto.NoticeDto;
import com.bit.datainkback.dto.ResponseDto;
import com.bit.datainkback.entity.User;
import com.bit.datainkback.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;




@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j

public class NoticeController {
    private final NoticeService noticeService;

    @PostMapping
//    multipartFile이 추가된 데이터는 @RequestPart로 받아준다.
    public ResponseEntity<?> post(@RequestBody NoticeDto noticeDto,
                                  @AuthenticationPrincipal User user,
                                  @PageableDefault(page =0, size =15) Pageable pageable) {
        ResponseDto<NoticeDto> responseDto = new ResponseDto<>();

        try {
            // 공지사항 생성 로직 수행
            log.info("post noticeDto: {}", noticeDto);
            NoticeDto noticeDtoList = noticeService.post(noticeDto, user);

            log.info("saved notice: {}", savedNotice);
            responseDto.setPageItems(noticeDtoList);
            responseDto.setStatusCode(HttpStatus.CREATED.value());
            responseDto.setStatusMessage("created");
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("Error creating notice: {}", e.getMessage());
            responseDto.setSuccess(false);
            responseDto.setMessage("공지사항 생성 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
        }


    }
}
