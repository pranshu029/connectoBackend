package com.connectoBackend.user.repository;

import com.connectoBackend.user.entity.Connection;
import com.connectoBackend.user.entity.User;
import com.connectoBackend.user.enums.ConnectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConnectionRepository extends JpaRepository<Connection, UUID> {

    Optional<Connection> findByUserAndTargetUser(User user, User targetUser);

    List<Connection> findAllByUserAndStatus(User user, ConnectionStatus status);

    List<Connection> findAllByTargetUserAndStatus(User targetUser, ConnectionStatus status);

    List<Connection> findAllByUser(User user);

    List<Connection> findAllByTargetUser(User targetUser);

    boolean existsByUserAndTargetUser(User user, User targetUser);

    @Query("select count(c) > 0 from Connection c where ((c.user = :user and c.targetUser = :targetUser) or (c.user = :targetUser and c.targetUser = :user)) and c.status = :status")
    boolean existsConnectionBetweenUsers(User user, User targetUser, ConnectionStatus status);

    @Query("select case when count(c) > 0 then true else false end from Connection c where ((c.user = :first and c.targetUser = :second) or (c.user = :second and c.targetUser = :first))")
    boolean existsConnectionBetweenUsers(User first, User second);

    void deleteByUserAndTargetUser(User user, User targetUser);
}
