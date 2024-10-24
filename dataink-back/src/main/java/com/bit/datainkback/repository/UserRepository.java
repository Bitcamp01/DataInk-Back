package com.bit.datainkback.repository;

import com.bit.datainkback.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(String id);  // String 타입의 id로 검색

    Long countById(String id);
    long countByTel(String tel);
    Long findByTel(String tel);

    Optional<User> findByName(String userId);

    @Query("SELECT u.id FROM User u")
    List<Long> findAllUserIds();
}
