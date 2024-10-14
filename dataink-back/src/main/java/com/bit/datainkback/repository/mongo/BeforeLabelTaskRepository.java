package com.bit.datainkback.repository.mongo;

import com.bit.datainkback.entity.mongo.BeforeLabelTask;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BeforeLabelTaskRepository extends MongoRepository<BeforeLabelTask, String> {
}
