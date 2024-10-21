package com.bit.datainkback.repository.mongo;

import com.bit.datainkback.entity.mongo.MongoProjectData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MongoProjectDataRepository extends MongoRepository<MongoProjectData, String> {
    Optional<MongoProjectData> findByProjectId(Long projectId);

    @Query("{ 'folders._id': ?0 }")
    Optional<MongoProjectData> findFolderById(String folderId);
}
