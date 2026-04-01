package com.axyl.the_atelier.domain.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Mirrors Drizzle {@code post}. For API responses — not persisted locally.
 */
public final class Post {
    @NonNull public final String id;
    @NonNull public final String slug;
    @NonNull public final String title;
    @NonNull public final String content;
    @Nullable public final String summary;
    @Nullable public final String coverImage;
    public final boolean isPublished;
    @NonNull public final String authorId;
    public final long createdAt;
    public final long updatedAt;

    public Post(
            @NonNull String id,
            @NonNull String slug,
            @NonNull String title,
            @NonNull String content,
            @Nullable String summary,
            @Nullable String coverImage,
            boolean isPublished,
            @NonNull String authorId,
            long createdAt,
            long updatedAt
    ) {
        this.id = id;
        this.slug = slug;
        this.title = title;
        this.content = content;
        this.summary = summary;
        this.coverImage = coverImage;
        this.isPublished = isPublished;
        this.authorId = authorId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
