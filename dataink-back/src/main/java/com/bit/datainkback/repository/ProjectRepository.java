package com.bit.datainkback.repository;

import com.bit.datainkback.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
<<<<<<< HEAD


=======
    List<Project> findByUser_UserId(Long userId);
>>>>>>> 4d0bccb7f90060d32a1df2aed5dd63566830f32c
}
