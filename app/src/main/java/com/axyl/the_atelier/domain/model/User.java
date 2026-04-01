package com.axyl.the_atelier.domain.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Mirrors auth/user table shape (e.g. Drizzle {@code user}). For API responses — not persisted locally.
 */
public final class User {
    @NonNull public final String id;
    @NonNull public final String name;
    @NonNull public final String email;
    public final boolean emailVerified;
    @Nullable public final String image;
    public final long createdAt;
    public final long updatedAt;
    @Nullable public final String username;
    @Nullable public final String displayUsername;
    @Nullable public final String role;
    @Nullable public final Boolean banned;
    @Nullable public final String banReason;
    @Nullable public final Long banExpires;

    public User(
            @NonNull String id,
            @NonNull String name,
            @NonNull String email,
            boolean emailVerified,
            @Nullable String image,
            long createdAt,
            long updatedAt,
            @Nullable String username,
            @Nullable String displayUsername,
            @Nullable String role,
            @Nullable Boolean banned,
            @Nullable String banReason,
            @Nullable Long banExpires
    ) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.emailVerified = emailVerified;
        this.image = image;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.username = username;
        this.displayUsername = displayUsername;
        this.role = role;
        this.banned = banned;
        this.banReason = banReason;
        this.banExpires = banExpires;
    }
}
