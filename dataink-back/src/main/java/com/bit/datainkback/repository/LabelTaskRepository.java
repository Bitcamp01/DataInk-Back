package com.bit.datainkback.repository;

import com.bit.datainkback.entity.LabelTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LabelTaskRepository extends JpaRepository<LabelTask, Long> {
    // 필요 시 사용자 정의 메서드 추가 가능
}
