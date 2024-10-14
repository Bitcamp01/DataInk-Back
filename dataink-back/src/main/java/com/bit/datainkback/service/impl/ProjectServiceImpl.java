package com.bit.datainkback.service.impl;

import com.bit.datainkback.dto.mongo.ProjectStructureDto;
import com.bit.datainkback.repository.ProjectRepository;
import com.bit.datainkback.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    @Override
    public ProjectStructureDto getInitFolderData() {
        return null;
    }
}
