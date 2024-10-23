package com.bit.datainkback.dto.mongo;

import com.bit.datainkback.entity.mongo.Folder;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class FolderDto {
    private String id;
    private String label;
    private List<String> itemIds;
    private String lastModifiedUserId;
    private String lastModifiedDate;
    private boolean isFolder;
    private boolean finished;
    private List<FolderDto> children;  // Folder의 하위 폴더도 Dto로 변환
    private String category1;
    private String category2;
    private String category3;
    private String workStatus;

    // FolderDto -> Folder 엔티티로 변환
    public Folder toEntity() {
        return Folder.builder()
                .id(this.id)
                .label(this.label)
                .itemIds(this.itemIds)
                .lastModifiedUserId(this.lastModifiedUserId)
                .lastModifiedDate(this.lastModifiedDate)
                .isFolder(this.isFolder)
                .finished(this.finished)
                .children(this.children != null ? this.children.stream().map(FolderDto::toEntity).toList() : null)
                .category1(this.category1)
                .category2(this.category2)
                .category3(this.category3)
                .workStatus(this.workStatus)
                .build();
    }
}
