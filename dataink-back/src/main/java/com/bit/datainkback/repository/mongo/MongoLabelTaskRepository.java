package com.bit.datainkback.repository.mongo;

import com.bit.datainkback.entity.mongo.Tasks;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MongoLabelTaskRepository extends MongoRepository<Tasks, String> {
    List<Tasks> findByParentFolderId(String parentFolderId);

    Optional<Tasks> findByParentFolderIdAndTaskName(String parentFolderId, String taskName);

}
