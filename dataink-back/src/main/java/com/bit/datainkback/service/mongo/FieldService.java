package com.bit.datainkback.service.mongo;

import com.bit.datainkback.entity.mongo.Field;
import com.bit.datainkback.repository.mongo.FieldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FieldService {

    @Autowired
    private FieldRepository fieldRepository;

    // 상위 필드와 하위 필드를 함께 생성
    public Field createFieldWithSubFields(Field field) {
        if (field.isParentField()) {
            field.setFieldValue(null);  // 상위 필드는 값을 가지지 않음
        } else {
            // 하위 필드이면서 마지막 필드인 경우, 받은 fieldValue를 그대로 저장
            if (field.getSubFields() == null || field.getSubFields().isEmpty()) {
                // 받은 fieldValue가 있으면 그 값을 그대로 저장, 없으면 빈값으로 설정
                field.setFieldValue(field.getFieldValue() != null ? field.getFieldValue() : "");
            } else {
                // 상위 필드인 경우에는 fieldValue를 null로 설정
                field.setFieldValue(null);
            }
        }

        return fieldRepository.save(field);
    }

    // 특정 필드 조회
    public Field getFieldById(Long id) {
        return fieldRepository.findById(id).orElse(null);
    }
}



