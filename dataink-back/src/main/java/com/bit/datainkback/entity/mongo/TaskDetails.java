package com.bit.datainkback.entity.mongo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "task_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskDetails {
    @Id
    private String id;  // 고유 Task ID
    private String category1;
    private String category2;
    private String category3;
    private String workname;
    private String workstatus;
}
