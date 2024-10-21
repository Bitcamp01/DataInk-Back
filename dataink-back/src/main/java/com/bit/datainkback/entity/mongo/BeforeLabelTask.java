package com.bit.datainkback.entity.mongo;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "beforeLabelTasks")  // MongoDB 컬렉션 이름을 지정
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BeforeLabelTask {
    @Id
    private String id;  // MongoDB의 _id 필드가 자동으로 매핑됩니다

    private String category1;
    private String category2;
    private String category3;
    private String workname;
    private String workstatus;
    private LocalDateTime endDate;  // MySQL에서 가져올 마감일
}
