package com.bit.datainkback.service.mongo;

import com.bit.datainkback.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class MySqlService {

    @Autowired
    private ProjectRepository projectRepository;  // MySQL용 리포지토리

    public LocalDateTime getEndDateByProjectId(Long projectId) {
        return projectRepository.findById(projectId)
                .map(project -> project.getEndDate())
                        .orElseThrow(() -> new RuntimeException("프로젝트를 찾을 수 없습니다."));
    }
}

