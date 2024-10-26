package com.bit.datainkback.entity;

import com.bit.datainkback.dto.UserDetailDto;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@SequenceGenerator(
        name = "userDetailSeqGenerator",
        sequenceName = "USERDETAIL_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetail {
    @Id
    private Long userId; // 기본 키로 사용할 필드 추가
    @OneToOne
    @MapsId // User의 기본 키를 이 엔티티의 기본 키로 사용
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @JsonBackReference // 순환 참조 해결
    private User user; // User와의 관계 설정

    private String dep;
    private String nickname;
    private String addr;
    private Timestamp lastLogintime;
    private String profileImageUrl;
    private String backgroundImageUrl;
    private String profileIntro;
    private String profileImgName;
    private String profileImgOriginname;
    private String profileImgType;
    private String backgroundImgName;
    private String backgroundImgOriginname;
    private String backgroundImgType;

    @Transient
    private String profileImgStatus;
    @Transient
    private String backgroundImgStatus;

    public UserDetailDto toDto() {
        return UserDetailDto.builder()
                .userId(this.user.getUserId())
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