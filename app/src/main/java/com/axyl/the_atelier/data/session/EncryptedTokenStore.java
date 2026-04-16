package com.axyl.the_atelier.data.session;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.util.concurrent.TimeUnit;

public final class EncryptedTokenStore implements TokenStore {
    private static final String PREFS_NAME = "secure_session_prefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_ACCESS_TOKEN_SAVED_AT = "access_token_saved_at";
    private static final long ACCESS_TOKEN_TTL_MS = TimeUnit.DAYS.toMillis(7);

    private final SharedPreferences prefs;

    public EncryptedTokenStore(@NonNull Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            prefs = EncryptedSharedPreferences.create(
                    context,
                    PREFS_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            throw new IllegalStateException("Cannot initialize EncryptedTokenStore", e);
        }
    }

    @Override
    public void saveAccessToken(@NonNull String token) {
        prefs.edit()
                .putString(KEY_ACCESS_TOKEN, token)
                .putLong(KEY_ACCESS_TOKEN_SAVED_AT, System.currentTimeMillis())
                .apply();
    }

    @Override
    @Nullable
    public String getAccessToken() {
        String token = prefs.getString(KEY_ACCESS_TOKEN, null);
        if (token == null || token.trim().isEmpty()) {
            return null;
        }

        long savedAt = prefs.getLong(KEY_ACCESS_TOKEN_SAVED_AT, 0L);
        boolean isExpired = savedAt <= 0L
                || System.currentTimeMillis() - savedAt >= ACCESS_TOKEN_TTL_MS;
        if (isExpired) {
            clearAccessToken();
            return null;
        }

        return token;
    }

    @Override
    public void clearAccessToken() {
        prefs.edit()
                .remove(KEY_ACCESS_TOKEN)
                .remove(KEY_ACCESS_TOKEN_SAVED_AT)
                .apply();
    }
}

