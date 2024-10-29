package com.bit.datainkback.repository;

import com.bit.datainkback.entity.Project;
import com.bit.datainkback.entity.UserProject;
import com.bit.datainkback.entity.UserProjectId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserProjectRepository extends JpaRepository<UserProject, UserProjectId> {
    List<UserProject> findByUserUserId(Long userId);

    List<UserProject> findByProjectProjectId(Long projectId);
    Optional<UserProject> findByProject(Project project);
}
