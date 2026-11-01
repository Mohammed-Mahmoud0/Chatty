package org.example.chatty.user.repository;

import org.example.chatty.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUserName(String userName);

    Optional<User> findByEmail(String email);

    boolean existsByUserName(String userName);

    boolean existsByEmail(String email);

    @Modifying
    @Transactional
    @Query("""
            UPDATE User u
            SET u.status = true
            WHERE u.userName = :username
            """)
    int markOnline(@Param("username") String username);

    @Modifying
    @Transactional
    @Query("""
            UPDATE User u
            SET u.status = false,
                u.lastSeen = :lastSeen
            WHERE u.userName = :username
            """)
    int markOffline(
            @Param("username") String username,
            @Param("lastSeen") LocalDateTime lastSeen
    );

    @Modifying
    @Transactional
    @Query("""
            UPDATE User u
            SET u.status = false
            WHERE u.status = true
            """)
    int resetOnlineStatuses();

}
