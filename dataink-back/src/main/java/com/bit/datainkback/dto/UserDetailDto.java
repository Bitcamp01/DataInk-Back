package com.bit.datainkback.dto;

import com.bit.datainkback.entity.User;
import com.bit.datainkback.entity.UserDetail;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserDetailDto {
    private Long userId; // 사용자 ID
    private String dep;
    private String addr; // 주소
    private String profileIntro; // 프로필 소개
    private String profilePicture; // 프로필 사진
    private String profilePictureRoute; // 프로필 사진 경로
    private String backgroundPicture; // 배경 사진
    private String backgroundPictureRoute; // 배경 사진 경로
    private String nickname; // 닉네임
    private Timestamp lastLogintime; // 마지막 로그인 시간

    // UserDetailDto를 UserDetail 엔티티로 변환하는 메서드
    public UserDetail toEntity(User user) {
        return UserDetail.builder()
                .user(user)
                .dep(this.dep)
                .addr(this.addr)
                .profileIntro(this.profileIntro)
                .profilePicture(this.profilePicture)
                .profilePictureRoute(this.profilePictureRoute)
                .backgroundPicture(this.backgroundPicture)
                .backgroundPictureRoute(this.backgroundPictureRoute)
                .nickname(this.nickname)
                .lastLogintime(this.lastLogintime)
                .build();
    }
}
