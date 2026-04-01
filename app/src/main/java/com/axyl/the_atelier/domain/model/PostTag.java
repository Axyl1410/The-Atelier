package com.axyl.the_atelier.domain.model;

import androidx.annotation.NonNull;

/**
 * Mirrors Drizzle {@code post_tag} composite key (post ↔ tag). For API responses — not persisted locally.
 */
public final class PostTag {
    @NonNull public final String postId;
    @NonNull public final String tagId;

    public PostTag(@NonNull String postId, @NonNull String tagId) {
        this.postId = postId;
        this.tagId = tagId;
    }
}
