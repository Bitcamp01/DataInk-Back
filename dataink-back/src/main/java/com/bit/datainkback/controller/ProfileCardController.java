
package com.bit.datainkback.controller;

import com.bit.datainkback.dto.NoticeDto;
import com.bit.datainkback.dto.ProfileCardDto;
import com.bit.datainkback.entity.CustomUserDetails;
import com.bit.datainkback.entity.Notice;
import com.bit.datainkback.entity.User;
import com.bit.datainkback.entity.UserDetail;
import com.bit.datainkback.repository.NoticeRepository;
import com.bit.datainkback.repository.UserDetailRepository;
import com.bit.datainkback.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/profile")
public class ProfileCardController {

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private UserDetailRepository userDetailRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/notices")
    public List<NoticeDto> getNotices(@AuthenticationPrincipal CustomUserDetails userDetails) {

        List<Notice> notices = noticeRepository.findAll();
        return notices.stream()
                .map(notice -> {
                    // 공지사항을 작성한 유저의 ID를 사용하여 User 정보 조회
                    UserDetail userDetail = userRepository.findById(notice.getUser().getUserId()).orElse(null).getUserDetail();
                    String dep = (userDetail != null) ? userDetail.getDep() : "배정되지 않음";

                    return new NoticeDto(
                            notice.getContent(),
                            notice.getNoticeId(),
                            notice.getCreated(),
                            dep
                    );
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/profiles")
    public ProfileCardDto getProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        // CustomUserDetails에서 userId 가져오기
        Long userId = userDetails.getUser().getUserId();

        // 유저 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 유저 상세 정보 조회
        UserDetail userDetail = userDetailRepository.findById(user.getUserId())
                .orElseThrow(() -> new RuntimeException("UserDetail not found"));

        // ProfileCardDto 반환
        return new ProfileCardDto(
                user.getUserId(),
                user.getName(),
                user.getAuthen(),
                userDetail.getDep(),
                userDetail.getProfileImageUrl()
        );
    }
}

