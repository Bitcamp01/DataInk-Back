package com.bit.datainkback.entity.mongo;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "folders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Folder {
    @Id
    private String id;
    private String label; // 폴더/파일 이름
    private String itemId;  // 폴더일 경우 null, 파일(Task)일 경우 항목 ID
    private String lastModifiedUserId;
    private String lastModifiedDate;
    private boolean isFolder;  // 폴더 또는 파일(Task) 여부
    private boolean finished; // 하위 작업 완료 여부
    private List<Folder> children;  // 하위 폴더 및 파일(Task) 통합
    private String workstatus;

    // MongoDB에 삽입할 때 String으로 변환된 ObjectId를 id에 설정하는 메서드
    public void generateId() {
        this.id = new ObjectId().toString();  // 자동 생성된 ObjectId를 String으로 변환
    }
}
