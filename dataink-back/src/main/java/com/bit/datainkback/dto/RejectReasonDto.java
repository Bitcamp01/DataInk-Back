package com.bit.datainkback.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class RejectReasonDto {
    private String rejectionReason; // 반려 사유
    private Map<String, Object> transformedData;
}
