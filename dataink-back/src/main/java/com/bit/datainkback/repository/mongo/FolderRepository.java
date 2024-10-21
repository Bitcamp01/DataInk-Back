package com.bit.datainkback.repository.mongo;

import com.bit.datainkback.entity.mongo.Folder;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FolderRepository extends MongoRepository<Folder, String> {
    // 폴더 데이터를 처리하는 기본적인 CRUD 기능 제공
    // folderId로 폴더를 조회하는 메서드
    Optional<Folder> findById(String id);

    // folderIds 목록에 포함된 폴더들을 조회하는 커스텀 메소드
    List<Folder> findByIdIn(List<String> folderIds);
}
