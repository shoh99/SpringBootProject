package com.example.springprojectfrombook.user.repository;

import com.example.springprojectfrombook.user.entity.UserEntity;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    @Query(
            "" +
                    "SELECT CASE WHEN COUNT(u) > 0 THEN " +
                    "TRUE ELSE FALSE END " +
                    "FROM UserEntity u " +
                    "WHERE u.email = ?1"
    )
    Boolean selectExistsEmail (String email);

    UserEntity findByEmail(String email);
}
