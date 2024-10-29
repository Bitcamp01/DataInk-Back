package com.bit.datainkback.repository.custom.impl;

import com.bit.datainkback.entity.Project;
import com.bit.datainkback.repository.custom.ProjectRepositoryCustom;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.bit.datainkback.entity.QProject.project;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ProjectRepositoryCustomImpl implements ProjectRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Project> searchAll(String searchCondition, String searchKeyword, Pageable pageable, LocalDateTime startDate, LocalDateTime endDate, List<Long> projectIds) {
        BooleanBuilder booleanBuilder = getSearch(searchCondition, searchKeyword);
        // projectIds 리스트에 포함된 프로젝트만 조회
        if (projectIds != null && !projectIds.isEmpty()) {
            booleanBuilder.and(project.projectId.in(projectIds));
        }

        // 시작일과 종료일이 모두 존재할 때만 조건 추가
        if (startDate != null && endDate != null) {
            booleanBuilder.and(project.startDate.goe(startDate))  // 시작일 조건 추가
                    .and(project.endDate.loe(endDate));   // 종료일 조건 추가
        }

        List<Project> projectList = jpaQueryFactory.selectFrom(project)
                .where(booleanBuilder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = jpaQueryFactory.select(project.count())
                .from(project)
                .where(booleanBuilder)
                .fetchOne();

        return new PageImpl<>(projectList, pageable, total);
    }

    public BooleanBuilder getSearch(String searchCondition, String searchKeyword) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if ("all".equalsIgnoreCase(searchCondition) && (searchKeyword == null || searchKeyword.isEmpty())) {
            // 조건 없이 모든 프로젝트를 조회
            return booleanBuilder;  // 빈 조건으로 반환
        }

        if (searchCondition.equalsIgnoreCase("all")) {
            booleanBuilder.or(project.name.containsIgnoreCase(searchKeyword));
            booleanBuilder.or(project.description.containsIgnoreCase(searchKeyword));
        } else if (searchCondition.equalsIgnoreCase("projectName")) {
            booleanBuilder.and(project.name.containsIgnoreCase(searchKeyword));
        } else if (searchCondition.equalsIgnoreCase("workName")) {
            booleanBuilder.and(project.description.containsIgnoreCase(searchKeyword));
        }

        return booleanBuilder;
    }

}
