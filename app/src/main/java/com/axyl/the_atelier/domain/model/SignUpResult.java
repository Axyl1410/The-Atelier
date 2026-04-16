package com.axyl.the_atelier.domain.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class SignUpResult {
    @NonNull public final String token;
    @Nullable public final User user;

    public SignUpResult(@NonNull String token, @Nullable User user) {
        this.token = token;
        this.user = user;
    }
}

