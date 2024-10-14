package com.bit.datainkback.repository;

import com.bit.datainkback.entity.mongo.MongoLabelTasks;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoLabelTaskRepository extends MongoRepository<MongoLabelTasks, String> {
}
