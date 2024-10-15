package com.bit.datainkback.controller.mongo;

import com.bit.datainkback.entity.mongo.Field;
import com.bit.datainkback.service.mongo.FieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/mongo/fields")
public class FieldController {

    @Autowired
    private FieldService fieldService;

    // 폴더에 필드 추가
    @PostMapping("/{folderId}/add")
    public ResponseEntity<Void> addFieldsToFolder(@PathVariable String folderId, @RequestBody List<Field> fields) {
        fieldService.addFieldsToTask(folderId, fields);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 특정 필드 조회
    @GetMapping("/{id}")
    public Field getFieldById(@PathVariable Long id) {
        return fieldService.getFieldById(id);
    }
}
