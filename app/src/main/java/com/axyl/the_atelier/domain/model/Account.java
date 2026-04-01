package com.axyl.the_atelier.domain.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Mirrors auth account table shape (e.g. Drizzle {@code account}). For API responses — not persisted locally.
 */
public final class Account {
    @NonNull public final String id;
    @NonNull public final String accountId;
    @NonNull public final String providerId;
    @NonNull public final String userId;
    @Nullable public final String accessToken;
    @Nullable public final String refreshToken;
    @Nullable public final String idToken;
    @Nullable public final Long accessTokenExpiresAt;
    @Nullable public final Long refreshTokenExpiresAt;
    @Nullable public final String scope;
    @Nullable public final String password;
    public final long createdAt;
    public final long updatedAt;

    public Account(
            @NonNull String id,
            @NonNull String accountId,
            @NonNull String providerId,
            @NonNull String userId,
            @Nullable String accessToken,
            @Nullable String refreshToken,
            @Nullable String idToken,
            @Nullable Long accessTokenExpiresAt,
            @Nullable Long refreshTokenExpiresAt,
            @Nullable String scope,
            @Nullable String password,
            long createdAt,
            long updatedAt
    ) {
        this.id = id;
        this.accountId = accountId;
        this.providerId = providerId;
        this.userId = userId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.idToken = idToken;
        this.accessTokenExpiresAt = accessTokenExpiresAt;
        this.refreshTokenExpiresAt = refreshTokenExpiresAt;
        this.scope = scope;
        this.password = password;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
