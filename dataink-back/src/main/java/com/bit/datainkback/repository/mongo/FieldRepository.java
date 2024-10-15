package com.bit.datainkback.repository.mongo;

import com.bit.datainkback.entity.mongo.Field;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FieldRepository extends MongoRepository<Field, Long> {
}
