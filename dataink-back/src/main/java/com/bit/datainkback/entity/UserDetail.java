package com.bit.datainkback.entity;

import com.bit.datainkback.dto.UserDetailDto;
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
    private User user; // User와의 관계 설정

    @Column(nullable = true)
    private String dep;

    @Column
    private String nickname;
    @Column
    private String addr;
    @Column(name = "profile_intro")
    private String profileIntro;
    @Column(name = "profile_picture_name")
    private String profilePictureName;
    @Column(name = "profile_picture_route")
    private String profilePictureRoute;
    @Column(name = "profile_picture_originname")
    private String profilePictureOriginname;
    @Column(name = "profile_picture_type")
    private String profilePictureType;
    @Column(name = "profile_picture_status")
    @Transient
    private String profilePictureStatus;
    @Column(name = "background_picture_name")
    private String backgroundPictureName;
    @Column(name = "background_picture_route")
    private String backgroundPictureRoute;
    @Column(name = "background_picture_originname")
    private String backgroundPictureOriginname;
    @Column(name = "background_picture_type")
    private String backgroundPictureType;
    @Column(name = "background_picture_status")
    @Transient
    private String backgroundPictureStatus;
    @Column(name = "last_login_time")
    private Timestamp lastLogintime;

    public UserDetailDto toDto() {
        return UserDetailDto.builder()
                .userId(this.user.getUserId())
                .dep(this.dep)
                .addr(this.addr)
                .profileIntro(this.profileIntro)
                .profilePictureName(this.profilePictureName)
                .profilePictureRoute(this.profilePictureRoute)
                .profilePictureOriginname(this.profilePictureOriginname)
                .profilePictureType(this.profilePictureType)
                .profilePictureStatus(this.profilePictureStatus)
                .backgroundPictureName(this.backgroundPictureName)
                .backgroundPictureRoute(this.backgroundPictureRoute)
                .backgroundPictureOriginname(this.backgroundPictureOriginname)
                .backgroundPictureType(this.backgroundPictureType)
                .backgroundPictureStatus(this.backgroundPictureStatus)
                .nickname(this.nickname)
                .lastLogintime(this.lastLogintime)
                .build();

    }
}