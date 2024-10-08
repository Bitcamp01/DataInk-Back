package com.bit.datainkback.dto;

import com.bit.datainkback.entity.User;
import com.bit.datainkback.entity.UserDetail;
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
    private String dep;
    private Enum authen;
    private Timestamp regdate;
    private String status;
    private UserDetailDto userDetailDto;

    public User toEntity(User user) {
        return User.builder()
                .userId(this.userId)
                .id(this.id)
                .password(this.password)
                .name(this.name)
                .email(this.email)
                .tel(this.tel)
                .birth(this.birth)
                .dep(this.dep)
                .authen(this.authen) // Enum 타입 그대로 사용
                .regdate(this.regdate)
                .status(this.status)
                .userDetail(
                        userDetailDto != null
                            ? userDetailDto.toEntity(user)
                            : null
                )
                .build();


    }
}
