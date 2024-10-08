package com.bit.datainkback.entity;

import com.bit.datainkback.dto.UserDto;
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
    @Column(name = "user_id", unique = true, nullable = false)
    private int userId;
    @Column(unique = true, nullable = false)
    private String id;
    private String password;
    private String name;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(unique = true, nullable = false)
    private String tel;
    private Date birth;
    private String dep;
    private Enum authen;
    private Timestamp regdate;
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
                .build();
    }


}
