package com.bit.datainkback.entity.mongo;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "fields")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Field {
    @Id
    private String id;  // MongoDB에서 생성된 고유 ID
    private Long userId;
    private String fieldName;  // 항목 이름
    @JsonProperty("isParentField")
    private boolean isParentField;  // 상위 항목 여부 구분
    private List<Field> subFields;  // 하위 항목 리스트

    // MongoDB에 삽입할 때 String으로 변환된 ObjectId를 id에 설정하는 메서드
    public void generateId() {
        this.id = new ObjectId().toString();  // 자동 생성된 ObjectId를 String으로 변환
    }
}
