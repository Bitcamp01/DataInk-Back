package com.bit.datainkback.dto.mongo;

import com.bit.datainkback.entity.mongo.Folder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskSearchCriteria {
    private String category1;  // 대분류
    private String category2;  // 중분류
    private String category3;  // 소분류
    private String searchKeyword;  // taskName에 대한 검색어
    private String workStatus;  // 작업 상태 (in_progress, submitted 등)
    private List<Folder> folderItems;  // 프론트에서 전달된 폴더 구조
}

