package com.bit.datainkback.entity.mongo;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "mongo_label_tasks")
@Getter
@Setter
public class MongoLabelTask {
    @Id
    private String id; // MongoDB에서 자동으로 생성되는 고유 ID

    private String taskName;
    private String labelerId;
    private String status;
}
