package com.axyl.the_atelier.domain.model;

import androidx.annotation.NonNull;

/**
 * Mirrors Drizzle {@code tag}. For API responses — not persisted locally.
 */
public final class Tag {
    @NonNull public final String id;
    @NonNull public final String name;
    @NonNull public final String slug;

    public Tag(@NonNull String id, @NonNull String name, @NonNull String slug) {
        this.id = id;
        this.name = name;
        this.slug = slug;
    }
}
