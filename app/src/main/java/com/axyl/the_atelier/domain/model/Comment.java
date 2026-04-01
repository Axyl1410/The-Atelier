package com.axyl.the_atelier.domain.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Mirrors Drizzle {@code comment} (parent reply uses {@code parentId} on same {@code postId}). For API — not persisted locally.
 */
public final class Comment {
    @NonNull public final String id;
    @NonNull public final String content;
    @NonNull public final String postId;
    @NonNull public final String authorId;
    @Nullable public final String parentId;
    public final long createdAt;
    public final long updatedAt;

    public Comment(
            @NonNull String id,
            @NonNull String content,
            @NonNull String postId,
            @NonNull String authorId,
            @Nullable String parentId,
            long createdAt,
            long updatedAt
    ) {
        this.id = id;
        this.content = content;
        this.postId = postId;
        this.authorId = authorId;
        this.parentId = parentId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
