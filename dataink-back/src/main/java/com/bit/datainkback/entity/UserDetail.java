package com.bit.datainkback.entity;

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
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "userDetailSeqGenerator"
    )
    @OneToOne
    @MapsId
    @Column(name = "user_id")
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    private User user;

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
    private String nickname;
    @Column(name = "last_login_time")
    private Timestamp lastLogintime;

    public UserDetailDto toDto() {
        return UserDetailDto.builder()
                .userId(this.getUser())
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