package com.connectoBackend.user.specification;

import com.connectoBackend.user.entity.User;
import com.connectoBackend.user.enums.AccountStatus;
import com.connectoBackend.user.enums.Gender;
import com.connectoBackend.user.enums.UserRole;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specifications for dynamic User queries.
 */
public final class UserSpecification {

    private UserSpecification() {
    }

    // -> Filter by username.
    public static Specification<User> hasUsername(String username) {

        return (root, query, cb) ->
                username == null || username.isBlank()
                        ? null
                        : cb.equal(root.get("username"), username);
    }

    // -> Filter by email.
    public static Specification<User> hasEmail(String email) {

        return (root, query, cb) ->
                email == null || email.isBlank()
                        ? null
                        : cb.equal(root.get("email"), email);
    }

    // -> Filter by role.
    public static Specification<User> hasRole(UserRole role) {

        return (root, query, cb) ->
                role == null
                        ? null
                        : cb.equal(root.get("role"), role);
    }

    // -> Filter by gender.
    public static Specification<User> hasGender(Gender gender) {

        return (root, query, cb) ->
                gender == null
                        ? null
                        : cb.equal(root.get("gender"), gender);
    }

    // -> Filter by account status.
    public static Specification<User> hasAccountStatus(AccountStatus status) {

        return (root, query, cb) ->
                status == null
                        ? null
                        : cb.equal(root.get("accountStatus"), status);
    }

}