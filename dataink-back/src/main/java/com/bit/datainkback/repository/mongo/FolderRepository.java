package com.bit.datainkback.repository.mongo;

import com.bit.datainkback.entity.mongo.Folder;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FolderRepository extends MongoRepository<Folder, String> {
    // 폴더 데이터를 처리하는 기본적인 CRUD 기능 제공
}
