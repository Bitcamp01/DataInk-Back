package com.bit.datainkback.repository;

import com.bit.datainkback.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
