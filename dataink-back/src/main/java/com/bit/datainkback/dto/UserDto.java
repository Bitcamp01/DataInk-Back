package com.bit.datainkback.dto;

import com.bit.datainkback.entity.User;
import com.bit.datainkback.entity.UserDetail;
import com.bit.datainkback.enums.AuthenType;
import lombok.*;

import java.sql.Date;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserDto {
    private Long userId;
    private String id;
    private String password;
    private String name;
    private String email;
    private String tel;
    private Date birth;
    private AuthenType authen;
    private Timestamp regdate;
    private String status;
    private UserDetailDto userDetailDto;
    private String token;

    public User toEntity() {
        User user = User.builder()
                .userId(this.userId)
                .id(this.id)
                .password(this.password)
                .name(this.name)
                .email(this.email)
                .tel(this.tel)
                .birth(this.birth)
                .authen(this.authen)
                .regdate(this.regdate)
                .status(this.status)
                .build();

        if (userDetailDto != null) {
            user.setUserDetail(userDetailDto.toEntity(user)); // User 객체 전달
        }

        return user;
    }
}
