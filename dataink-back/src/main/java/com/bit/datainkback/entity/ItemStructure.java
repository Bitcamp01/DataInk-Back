package com.bit.datainkback.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "item_structure")
public class ItemStructure {
    @Id
    private String id;
    @DBRef
    private Object ItemStructure;
}