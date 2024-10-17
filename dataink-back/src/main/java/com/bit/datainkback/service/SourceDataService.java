package com.bit.datainkback.service;

import com.bit.datainkback.dto.SourceDataDto;
import com.bit.datainkback.entity.SourceData;
import com.bit.datainkback.repository.SourceDataRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

public interface SourceDataService {

//    private final SourceDataRepository sourceDataRepository; // 원천 데이터 리포지토리 의존성 주입
//
//    // 특정 작업 ID로 원천 데이터 조회
//    public SourceDataDto getSourceDataByTaskId(Long taskId) {
//        // 작업 ID에 해당하는 원천 데이터 조회 로직 (작업 ID와의 연관 관계에 따라 구현)
//        Optional<SourceData> sourceDataOptional = sourceDataRepository.findByProjectId(taskId);
//
//        return sourceDataOptional.map(SourceData::toDto).orElse(null); // DTO로 변환 후 반환
//    }
//
//    public <SourceDataDTO> SourceDataDTO getSourceDataBySourceId(Long sourceId) {
//    }
}