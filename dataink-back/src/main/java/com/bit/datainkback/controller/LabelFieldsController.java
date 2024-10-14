package com.bit.datainkback.controller;


import com.bit.datainkback.service.impl.LabelFieldsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/item")
public class LabelFieldsController {
    private final LabelFieldsService labelFieldsService;

    @PostMapping("/create")
    public ResponseEntity<?> createItem(@RequestBody Map<String, Object> body){
        System.out.println(body);
        return null;
    }
}
