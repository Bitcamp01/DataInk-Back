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
@RequestMapping("/api")
@CrossOrigin(origins = "https://dataink.site")
public class ProfileCardController {

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private UserDetailRepository userDetailRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/notices")
    public List<NoticeDto> getNotices() {
        List<Notice> notices = noticeRepository.findAll();
        return notices.stream()
                .map(notice -> new NoticeDto(notice.getTitle(), notice.getContent(), notice.getNoticeId(), notice.getCreated()))
                .collect(Collectors.toList());
    }

    @GetMapping("/profile")
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
                userDetail.getProfilePicture()
        );
    }
}
