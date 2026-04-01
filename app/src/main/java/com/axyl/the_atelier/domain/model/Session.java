package com.axyl.the_atelier.domain.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Mirrors auth session table shape (e.g. Drizzle {@code session}). For API responses — not persisted locally.
 */
public final class Session {
    @NonNull public final String id;
    public final long expiresAt;
    @NonNull public final String token;
    public final long createdAt;
    public final long updatedAt;
    @Nullable public final String ipAddress;
    @Nullable public final String userAgent;
    @NonNull public final String userId;
    @Nullable public final String impersonatedBy;

    public Session(
            @NonNull String id,
            long expiresAt,
            @NonNull String token,
            long createdAt,
            long updatedAt,
            @Nullable String ipAddress,
            @Nullable String userAgent,
            @NonNull String userId,
            @Nullable String impersonatedBy
    ) {
        this.id = id;
        this.expiresAt = expiresAt;
        this.token = token;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.userId = userId;
        this.impersonatedBy = impersonatedBy;
    }
}
