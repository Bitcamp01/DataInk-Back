package com.bit.datainkback.entity;

import com.bit.datainkback.dto.UserDto;
import com.bit.datainkback.enums.AuthenType;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.sql.Date;

@Entity
@SequenceGenerator(
        name = "userSeqGenerator",
        sequenceName = "USER_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "userSeqGenerator"
    )
    @Column(name = "user_id")
    private Long userId;
    @Column(unique = true, nullable = false)
    private String id;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String name;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(unique = true, nullable = false)
    private String tel;
    @Column(nullable = false)
    private Date birth;
    @Column(nullable = true)
    private String dep;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthenType authen;
    @Column(nullable = false)
    private Timestamp regdate;
    @Column(nullable = false)
    private String status;
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserDetail userDetail;

    public UserDto toDto() {
        return UserDto.builder()
                .userId(this.userId)
                .id(this.id)
                .password(this.password)
                .name(this.name)
                .email(this.email)
                .tel(this.tel)
                .birth(this.birth)
                .dep(this.dep)
                .authen(this.authen)
                .regdate(this.regdate)
                .status(this.status)
                .userDetailDto(
                    userDetail != null
                            ? userDetail.toDto()
                            : null
                )
                .build();
    }


}
