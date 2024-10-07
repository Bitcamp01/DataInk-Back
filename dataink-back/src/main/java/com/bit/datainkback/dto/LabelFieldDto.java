package com.bit.datainkback.dto;

import com.bit.datainkback.entity.LabelField;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class LabelFieldDto {
    private Long fieldId; // 라벨링 항목 ID
    private String name; // 라벨링 항목 이름
    private String mongoDataId; // MongoDB에 저장된 데이터의 ID

    public LabelField toEntity() {
        return LabelField.builder()
                .fieldId(this.fieldId)
                .name(this.name)
                .mongoDataId(this.mongoDataId)
                .build();
    }
}
