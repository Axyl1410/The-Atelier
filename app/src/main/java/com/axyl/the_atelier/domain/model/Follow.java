package com.axyl.the_atelier.domain.model;

import androidx.annotation.NonNull;

/**
 * Mirrors Drizzle {@code follow} composite key ({@code followerId} → {@code followingId}). For API — not persisted locally.
 */
public final class Follow {
    @NonNull public final String followerId;
    @NonNull public final String followingId;
    public final long createdAt;

    public Follow(
            @NonNull String followerId,
            @NonNull String followingId,
            long createdAt
    ) {
        this.followerId = followerId;
        this.followingId = followingId;
        this.createdAt = createdAt;
    }
}
