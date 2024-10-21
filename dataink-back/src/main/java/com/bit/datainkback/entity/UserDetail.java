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
    @JsonBackReference
    private User user; // User와의 관계 설정

    @Column(nullable = true)
    private String dep;
    @Column
    private String addr;
    @Column(name = "profile_intro")
    private String profileIntro;
    @Column(name = "profile_picture")
    private String profilePicture;
    @Column(name = "profile_picture_route")
    private String profilePictureRoute;
    @Column(name = "background_picture")
    private String backgroundPicture;
    @Column(name = "background_picture_route")
    private String backgroundPictureRoute;
    @Column
    private String nickname;
    @Column(name = "last_login_time")
    private Timestamp lastLogintime;

    public UserDetailDto toDto() {
        return UserDetailDto.builder()
                .userId(this.user.getUserId())
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