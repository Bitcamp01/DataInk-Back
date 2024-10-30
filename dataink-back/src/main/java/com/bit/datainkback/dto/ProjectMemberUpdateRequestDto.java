package com.bit.datainkback.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ProjectMemberUpdateRequestDto {

    private Long projectId;
    private List<MemberActionDto> members;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    public static class MemberActionDto {
        private Long userId;
        private String name;
        private String department;
        private String role;
        private String action;
    }
}
