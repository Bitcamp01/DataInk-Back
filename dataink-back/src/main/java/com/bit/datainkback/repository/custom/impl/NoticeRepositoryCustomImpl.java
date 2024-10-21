package com.bit.datainkback.repository.custom.impl;

import com.bit.datainkback.entity.Notice;
import com.bit.datainkback.repository.custom.NoticeRepositoryCustom;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.bit.datainkback.entity.QNotice.notice;

@Repository
@RequiredArgsConstructor

public class NoticeRepositoryCustomImpl implements NoticeRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Notice> searchAll(String searchCondition, String searchKeyword, Pageable pageable) {
        List<Notice>noticeList = jpaQueryFactory.selectFrom(notice)
                .where(getSearch(searchCondition, searchKeyword))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = jpaQueryFactory.select(notice.count())
                .from(notice)
                .where(getSearch(searchCondition, searchKeyword))
                .fetchOne();

        return new PageImpl<>(noticeList, pageable, total);
    }

    public BooleanBuilder getSearch(String searchCondition, String searchKeyword) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if(searchKeyword == null || searchKeyword.isEmpty()) {
            return null;
        }

        if(searchCondition.equalsIgnoreCase("all")) {
            booleanBuilder.or(notice.title.containsIgnoreCase(searchKeyword));
            booleanBuilder.or(notice.content.containsIgnoreCase(searchKeyword));
            booleanBuilder.or(notice.user.name.containsIgnoreCase(searchKeyword));
        } else if(searchCondition.equalsIgnoreCase("title")) {
            booleanBuilder.and(notice.title.containsIgnoreCase(searchKeyword));
        } else if(searchCondition.equalsIgnoreCase("content")) {
            booleanBuilder.and(notice.content.containsIgnoreCase(searchKeyword));
        } else if(searchCondition.equalsIgnoreCase("writer")) {
            booleanBuilder.and(notice.user.name.containsIgnoreCase(searchKeyword));
        }

        return booleanBuilder;
    }
    
    
    
}



    

