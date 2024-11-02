package com.bit.datainkback.repository.custom.impl;

import com.bit.datainkback.entity.Notification;
import com.bit.datainkback.entity.Project;
import com.bit.datainkback.repository.custom.NotificationRepositoryCustom;
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

import static com.bit.datainkback.entity.QNotification.notification;

@Slf4j
@Repository
@RequiredArgsConstructor
public class NotificationRepositoryCustomImpl implements NotificationRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Notification> searchAll(String searchCondition, String searchKeyword, Pageable pageable, LocalDateTime startDate, LocalDateTime endDate, List<Long> notificationIds) {
        BooleanBuilder booleanBuilder = getSearch(searchCondition, searchKeyword);

        // notificationIds 리스트에 포함된 알림만 조회하고, notificationIds가 비어 있으면 빈 결과 반환
        if (notificationIds == null || notificationIds.isEmpty()) {
            booleanBuilder.and(notification.notificationId.isNull()); // 항상 거짓인 조건 추가
        } else {
            booleanBuilder.and(notification.notificationId.in(notificationIds));
        }

        // 시작일과 종료일이 모두 존재할 때만 조건 추가
        if (startDate != null && endDate != null) {
            booleanBuilder.and(notification.createdAt.goe(startDate))  // 생성일 조건 추가
                    .and(notification.createdAt.loe(endDate));   // 종료일 조건 추가
        }

        List<Notification> notificationList = jpaQueryFactory.selectFrom(notification)
                .where(booleanBuilder)
                .orderBy(notification.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = jpaQueryFactory.select(notification.count())
                .from(notification)
                .where(booleanBuilder)
                .fetchOne();

        return new PageImpl<>(notificationList, pageable, total);
    }

    public BooleanBuilder getSearch(String searchCondition, String searchKeyword) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if ("all".equalsIgnoreCase(searchCondition) && (searchKeyword == null || searchKeyword.isEmpty())) {
            // 조건 없이 모든 프로젝트를 조회
            return booleanBuilder;  // 빈 조건으로 반환
        }

        if (searchCondition.equalsIgnoreCase("all")) {
            booleanBuilder.or(notification.notificationType.stringValue().equalsIgnoreCase(searchKeyword));
            booleanBuilder.or(notification.content.containsIgnoreCase(searchKeyword));
        } else if (searchCondition.equalsIgnoreCase("type")) {
            booleanBuilder.and(notification.notificationType.stringValue().equalsIgnoreCase(searchKeyword));
        } else if (searchCondition.equalsIgnoreCase("content")) {
            booleanBuilder.and(notification.content.containsIgnoreCase(searchKeyword));
        }

        return booleanBuilder;
    }
}
