package com.connectoBackend.user.repository;

import com.connectoBackend.user.entity.FollowRequest;
import com.connectoBackend.user.entity.User;
import com.connectoBackend.user.enums.FollowRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FollowRequestRepository extends JpaRepository<FollowRequest, UUID> {

    Optional<FollowRequest> findByRequesterAndTargetUser(User requester, User targetUser);

    List<FollowRequest> findAllByTargetUser(User targetUser);

    List<FollowRequest> findAllByTargetUserAndStatus(User targetUser, FollowRequestStatus status);

    List<FollowRequest> findAllByRequester(User requester);

    boolean existsByRequesterAndTargetUser(User requester, User targetUser);

    void deleteByRequesterAndTargetUser(User requester, User targetUser);
}
