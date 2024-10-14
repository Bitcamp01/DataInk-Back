package com.bit.datainkback.repository.mongo;

import com.bit.datainkback.entity.mongo.TaskDetails;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskDetailsRepository extends MongoRepository<TaskDetails, String> {
    // TaskDetails 데이터를 처리하는 기본적인 CRUD 기능 제공
}
