package com.bit.datainkback.entity.mongo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MongoLabelField {
    private String fieldName;
    private String fieldValue = "";  // 초기값을 빈 값으로 설정
    private MongoLabelField parentField;  // 상위 항목 필드
    private List<MongoLabelField> subFields;  // 하위 항목 필드 리스트
}
