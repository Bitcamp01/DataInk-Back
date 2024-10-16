package com.bit.datainkback.entity.mongo;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    private String id;  // MongoDB의 ObjectId를 사용하여 ID 생성
    private String label; // 폴더/파일 이름
    private String itemId;  // 항목 ID (파일일 경우)
    private String lastModifiedUserId;
    private String lastModifiedDate;
    @JsonProperty("isFolder")  // JSON에서 받는 속성과 매핑
    private boolean isFolder;  // 폴더 또는 파일(Task) 여부
    private boolean finished; // 하위 작업 완료 여부
    private List<Folder> children;  // 하위 폴더 및 파일(Task) 통합

    // MongoDB에 삽입할 때 String으로 변환된 ObjectId를 id에 설정하는 메서드
    public void generateId() {
        this.id = new ObjectId().toString();  // 자동 생성된 ObjectId를 String으로 변환
    }
}
