package com.axyl.the_atelier.domain.model;

import androidx.annotation.NonNull;

/**
 * Mirrors Drizzle {@code bookmark} composite key. For API responses — not persisted locally.
 */
public final class Bookmark {
    @NonNull public final String userId;
    @NonNull public final String postId;
    public final long createdAt;

    public Bookmark(@NonNull String userId, @NonNull String postId, long createdAt) {
        this.userId = userId;
        this.postId = postId;
        this.createdAt = createdAt;
    }
}
