package com.bit.datainkback.controller.mongo;

import com.bit.datainkback.entity.mongo.Field;
import com.bit.datainkback.service.mongo.FieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/mongo/fields")
public class FieldController {

    @Autowired
    private FieldService fieldService;

    // 상위 필드와 하위 필드를 한 번에 생성
    @PostMapping
    public Field createFieldWithSubFields(@RequestBody Field field) {
        return fieldService.createFieldWithSubFields(field);
    }

    // 특정 필드 조회
    @GetMapping("/{id}")
    public Field getFieldById(@PathVariable Long id) {
        return fieldService.getFieldById(id);
    }
}
