package com.bit.datainkback.repository;

import com.bit.datainkback.entity.UserProject;
import com.bit.datainkback.entity.UserProjectId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserProjectRepository extends JpaRepository<UserProject, UserProjectId> {
    List<UserProject> findByUserUserId(Long userId);
}
