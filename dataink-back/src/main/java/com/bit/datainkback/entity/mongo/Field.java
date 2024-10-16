package com.bit.datainkback.entity.mongo;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "fields")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Field {
    @Id
    private String id;  // MongoDB에서 생성된 고유 ID
    private String fieldName;  // 항목 이름
    @JsonProperty("isParentField")
    private boolean isParentField;  // 상위 항목 여부 구분
    private List<Field> subFields;  // 하위 항목 리스트
}