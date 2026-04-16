package com.axyl.the_atelier.data.session;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

public final class EncryptedTokenStore implements TokenStore {
    private static final String PREFS_NAME = "secure_session_prefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_ACCESS_TOKEN_SAVED_AT = "access_token_saved_at";

    private final SharedPreferences prefs;
    private final Clock clock;

    public EncryptedTokenStore(@NonNull Context context) {
        this(context, new SystemClock());
    }

    /** Package-private constructor used by tests to inject a custom {@link Clock}. */
    EncryptedTokenStore(@NonNull Context context, @NonNull Clock clock) {
        this.clock = clock;
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
                .putLong(KEY_ACCESS_TOKEN_SAVED_AT, clock.currentTimeMillis())
                .apply();
    }

    /**
     * Returns the stored access token, or {@code null} if the token is absent or has
     * exceeded the 7-day TTL.  Expired tokens are automatically cleared from storage
     * so that subsequent calls also return {@code null}.
     *
     * <p>Backward-compatibility: tokens saved before this TTL policy was introduced
     * will not have a {@code savedAt} value and are treated as expired.
     */
    @Override
    @Nullable
    public String getAccessToken() {
        String token = prefs.getString(KEY_ACCESS_TOKEN, null);
        if (token == null) {
            return null;
        }
        long savedAt = prefs.getLong(KEY_ACCESS_TOKEN_SAVED_AT, -1L);
        if (savedAt < 0 || clock.currentTimeMillis() - savedAt > TokenStore.TOKEN_TTL_MS) {
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

