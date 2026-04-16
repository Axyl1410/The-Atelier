package com.axyl.the_atelier.domain.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class SignInResult {
    public final boolean redirect;
    @NonNull public final String token;
    @Nullable public final String url;
    @Nullable public final User user;

    public SignInResult(
            boolean redirect,
            @NonNull String token,
            @Nullable String url,
            @Nullable User user
    ) {
        this.redirect = redirect;
        this.token = token;
        this.url = url;
        this.user = user;
    }
}

