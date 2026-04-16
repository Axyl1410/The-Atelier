package com.axyl.the_atelier.data.session;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface TokenStore {
    /** Fixed token lifetime: 7 days in milliseconds. */
    long TOKEN_TTL_MS = 7L * 24 * 60 * 60 * 1000;

    void saveAccessToken(@NonNull String token);
    @Nullable String getAccessToken();
    void clearAccessToken();
}

