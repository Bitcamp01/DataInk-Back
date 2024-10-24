package com.bit.datainkback.dto;

import com.bit.datainkback.entity.User;
import com.bit.datainkback.entity.UserDetail;
import jakarta.persistence.Column;
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
    private String profileImageUrl;
    private String backgroundImageUrl;
    private String profileIntro; // 프로필 소개
    private String profileImgName; // 프로필 사진
    private String profileImgOriginname;
    private String profileImgType;
    private String profileImgStatus;
    private String backgroundImgName; // 배경 사진
    private String backgroundImgOriginname;
    private String backgroundImgType;
    private String backgroundImgStatus;
    private String nickname; // 닉네임
    private Timestamp lastLogintime; // 마지막 로그인 시간

    // UserDetailDto를 UserDetail 엔티티로 변환하는 메서드
    public UserDetail toEntity(User user) {
        return UserDetail.builder()
                .user(user)
                .dep(this.dep)
                .addr(this.addr)
                .profileIntro(this.profileIntro)
                .profileImgName(this.profileImgName)
                .profileImageUrl(this.profileImageUrl)
                .profileImgOriginname(this.profileImgOriginname)
                .profileImgType(this.profileImgType)
                .profileImgStatus(this.profileImgStatus)
                .backgroundImgName(this.backgroundImgName)
                .backgroundImageUrl(this.backgroundImageUrl)
                .backgroundImgOriginname(this.backgroundImgOriginname)
                .backgroundImgType(this.backgroundImgType)
                .backgroundImgStatus(this.backgroundImgStatus)
                .nickname(this.nickname)
                .lastLogintime(this.lastLogintime)
                .build();
    }
}
