package com.bit.datainkback.entity.mongo;

import com.bit.datainkback.enums.TaskStatus;
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
public class MongoLabelTaskOne {
    private String taskName;
    private TaskStatus status;  // 작업 상태 (진행중, 완료됨 등) -> enum 사용
    private List<Field> fields;  // 작업에 연결된 라벨링 항목 리스트
}
