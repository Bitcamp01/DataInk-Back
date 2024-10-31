package com.bit.datainkback.repository;

import com.bit.datainkback.entity.LabelTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LabelTaskRepository extends JpaRepository<LabelTask, Long> {
    Optional<LabelTask> findByRefTaskId(String refTaskId); // fieldId로 조회

    void deleteByRefTaskId(String id);
}
