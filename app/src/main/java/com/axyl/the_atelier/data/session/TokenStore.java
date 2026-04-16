package com.axyl.the_atelier.data.session;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface TokenStore {
    void saveAccessToken(@NonNull String token);
    @Nullable String getAccessToken();
    void clearAccessToken();
}

