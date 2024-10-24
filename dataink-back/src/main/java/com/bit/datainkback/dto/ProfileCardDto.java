package com.bit.datainkback.dto;

import com.bit.datainkback.enums.AuthenType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileCardDto {
    private Long userId;
    private String username; // 유저의 이름 (user 테이블)
    private AuthenType authen; // 유저 역할 (user 테이블의 authen)
    private String dep; // 유저 소속 (userDetail 테이블의 dep)
    private String profileImg; // 유저 프로필 사진 경로 (user 테이블의 profile_picture)

    public ProfileCardDto(Long userId, String username, AuthenType authen, String dep, String profileImg){
        this.userId = userId;
        this.username = username;
        this.authen = authen;
        this.dep = dep;
        this.profileImg = profileImg;
    }
}
