package com.bit.datainkback.repository.mongo;

import com.bit.datainkback.entity.mongo.Field;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FieldRepository extends MongoRepository<Field, String> {
    // itemId로 필드를 저장하는 커스텀 메서드
    @Query("{ 'itemId': ?0 }")
    void saveFieldsForItem(String itemId, List<Field> fieldData);
}
