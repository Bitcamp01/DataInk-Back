package com.bit.datainkback.repository;

import com.bit.datainkback.entity.Project;
import com.bit.datainkback.repository.custom.ProjectRepositoryCustom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>, ProjectRepositoryCustom {
    List<Project> findByUser_UserId(Long userId);
}
