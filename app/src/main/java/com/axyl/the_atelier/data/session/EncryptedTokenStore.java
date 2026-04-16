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
        prefs.edit().putString(KEY_ACCESS_TOKEN, token).apply();
    }

    @Override
    @Nullable
    public String getAccessToken() {
        return prefs.getString(KEY_ACCESS_TOKEN, null);
    }

    @Override
    public void clearAccessToken() {
        prefs.edit().remove(KEY_ACCESS_TOKEN).apply();
    }
}

