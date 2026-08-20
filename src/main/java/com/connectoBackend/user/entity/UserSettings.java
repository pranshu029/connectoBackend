package com.connectoBackend.user.entity;

import com.connectoBackend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Stores user preferences.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_settings")
public class UserSettings extends BaseEntity {

    // -> Associated user
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true
    )
    private User user;

    // -> Notification preference
    @Builder.Default
    @Column(nullable = false)
    private Boolean notificationsEnabled = true;

    // -> Read receipt preference
    @Builder.Default
    @Column(nullable = false)
    private Boolean readReceiptsEnabled = true;

    // -> Online visibility preference
    @Builder.Default
    @Column(nullable = false)
    private Boolean onlineStatusVisible = true;

    @Builder.Default
    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean publicProfileDetails = false;

    @Builder.Default
    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean firstNamePublic = false;

    @Builder.Default
    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean lastNamePublic = false;

    @Builder.Default
    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean emailPublic = false;

    @Builder.Default
    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean phoneNumberPublic = false;

    @Builder.Default
    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean dateOfBirthPublic = false;

    @Builder.Default
    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean genderPublic = false;

    @Builder.Default
    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean profilePicturePublic = false;
}