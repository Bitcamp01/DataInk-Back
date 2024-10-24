package com.bit.datainkback.repository;

import com.bit.datainkback.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(String id);  // String 타입의 id로 검색

    Long countById(String id);
    long countByTel(String tel);
    Long findByTel(String tel);

    Optional<User> findByName(String userId);

    //무한스크롤 lastItemId이후 데이터 가져오는 메소드
    List<User> findByUserIdGreaterThan(Long lastItemId, Pageable pageable);
}
