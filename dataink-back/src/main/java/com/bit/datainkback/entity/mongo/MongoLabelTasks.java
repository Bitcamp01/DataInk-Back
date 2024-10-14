package com.bit.datainkback.entity.mongo;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "mongo_label_tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MongoLabelTasks {
    @Id
    private String id; // MongoDB에서 자동으로 생성되는 고유 ID

    private String taskName;
    private String labelerId;
    private String status;
}
