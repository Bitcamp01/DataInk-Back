package com.bit.datainkback.repository;

import com.bit.datainkback.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(String id);  // String 타입의 id로 검색

    Long countById(String id);

    Long findByTel(String tel);
}
