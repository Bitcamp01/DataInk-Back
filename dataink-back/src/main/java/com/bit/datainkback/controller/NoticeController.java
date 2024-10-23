package com.bit.datainkback.controller;


import com.bit.datainkback.dto.NoticeDto;
import com.bit.datainkback.dto.ResponseDto;
import com.bit.datainkback.entity.CustomUserDetails;
import com.bit.datainkback.entity.Notice;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.Arrays;


@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
@Slf4j

public class NoticeController {
    private final NoticeService noticeService;

    @PostMapping
//    multipartFile이 추가된 데이터는 @RequestPart로 받아준다.
    public ResponseEntity<?> post(@RequestPart("NoticeDto") NoticeDto noticeDto,
                                  @RequestPart(value="uploadFiles", required=false) MultipartFile[] uploadFiles,
                                  @AuthenticationPrincipal CustomUserDetails customUserDetails,
                                  @PageableDefault(page =0, size =15) Pageable pageable) {
        ResponseDto<NoticeDto> responseDto = new ResponseDto<>();

        try {
            log.info("post noticeDto: {}", noticeDto);
            Page<NoticeDto> noticeDtoList = noticeService.post(noticeDto, uploadFiles,
                    customUserDetails.getUser(),pageable);

            log.info("post noticeDtoList: {}", noticeDtoList);
            responseDto.setPageItems(noticeDtoList);
            responseDto.setStatusCode(HttpStatus.CREATED.value());
            responseDto.setStatusMessage("created");

            return ResponseEntity.created(new URI("/notice")).body(responseDto);
        } catch (Exception e) {
            log.error("post error: {}", e.getMessage());
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDto);
        }

    }


    @GetMapping
    public ResponseEntity<?> getBoards(@RequestParam("searchCondition") String searchCondition,
                                       @RequestParam("searchKeyword") String searchKeyword,
                                       @PageableDefault(page=0, size=15) Pageable pageale){
        ResponseDto<NoticeDto> responseDto = new ResponseDto<>();

        try{
            Page<NoticeDto> noticeDtoList = noticeService.findAll(searchCondition, searchKeyword, pageale);

            responseDto.setPageItems(noticeDtoList);
            responseDto.setItem(NoticeDto.builder()
                            .searchCondition(searchCondition)
                            .searchKeyword(searchKeyword)
                            .build());
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("ok");

            return ResponseEntity.ok(responseDto);
        }catch(Exception e){
            log.error("getBoards error: {}", e.getMessage());
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDto);


        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable("id") Long id) {
        ResponseDto<NoticeDto> responseDto = new ResponseDto<>();

        try {
            log.info("deleteById id: {}", id);

            noticeService.deleteById(id);

            responseDto.setStatusCode(HttpStatus.NO_CONTENT.value());
            responseDto.setStatusMessage("no content");

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("deleteById error: {}", e.getMessage());
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDto);
        }
    }

    @PatchMapping
    public ResponseEntity<?> modify(@RequestPart("noticeDto" )NoticeDto noticeDto,
                                    @RequestPart(value="uploadFiles", required = false) MultipartFile[] uploadFiles,
                                    @RequestPart(value="changeFiles", required = false) MultipartFile[] changeFiles,
                                    @RequestPart(value="originFiles", required = false) String originFiles,
                                    @AuthenticationPrincipal User user){
        ResponseDto<NoticeDto> responseDto = new ResponseDto<>();

        try {
            log.info("modify noticeDto: {}", noticeDto);

            if (uploadFiles != null && uploadFiles.length > 0)
                Arrays.stream(uploadFiles).forEach(file ->
                        log.info("modify uploadFile: {}", file.getOriginalFilename()));

            if (changeFiles != null && changeFiles.length > 0)
                Arrays.stream(uploadFiles).forEach(file ->
                        log.info("modify changeFile: {}", file.getOriginalFilename()));

            log.info("modify originFile: {}", originFiles);

            NoticeDto modifiedNoticeDto = noticeService.modify(noticeDto, uploadFiles,
                    changeFiles, originFiles, user.getUserId());

            responseDto.setItem(modifiedNoticeDto);
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("ok");

            return ResponseEntity.ok(responseDto);
            }catch(Exception e){
            log.error("modify error: {}", e.getMessage());
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDto);
        }
    }


}
