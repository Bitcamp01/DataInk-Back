package com.bit.datainkback.enums;

public enum TaskStatus {
    IN_PROGRESS, // 작업 진행 중
    SUBMITTED,   // 라벨러가 제출함
    PENDING,     // 검수자가 검수 대기 중
    REVIEWED,    // 검수 완료
    APPROVED,    // 최종 승인
    REJECTED     // 반려됨
}
