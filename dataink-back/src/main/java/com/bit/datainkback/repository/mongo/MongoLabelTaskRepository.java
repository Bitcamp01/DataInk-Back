package com.bit.datainkback.repository.mongo;

import com.bit.datainkback.entity.mongo.Tasks;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoLabelTaskRepository extends MongoRepository<Tasks, String> {
    void deleteByParentFolderId(String folderId);
}
