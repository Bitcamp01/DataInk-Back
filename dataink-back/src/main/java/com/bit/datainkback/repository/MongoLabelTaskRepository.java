package com.bit.datainkback.repository;

import com.bit.datainkback.entity.mongo.MongoLabelTask;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoLabelTaskRepository extends MongoRepository<MongoLabelTask, String> {
}
