package com.axyl.the_atelier.data.session;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class EncryptedTokenStoreTest {
    private static final String PREFS_NAME = "secure_session_prefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_ACCESS_TOKEN_SAVED_AT = "access_token_saved_at";
    private static final long ACCESS_TOKEN_TTL_MS = TimeUnit.DAYS.toMillis(7);

    private SharedPreferences prefs;
    private EncryptedTokenStore store;

    @Before
    public void setUp() throws Exception {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        store = new EncryptedTokenStore(context);
        prefs = createEncryptedPrefs(context);
        prefs.edit().clear().commit();
    }

    @Test
    public void whenNoToken_thenReturnsNull_andClearsMetadata() {
        String token = store.getAccessToken();

        assertNull(token);
        assertFalse(prefs.contains(KEY_ACCESS_TOKEN));
        assertFalse(prefs.contains(KEY_ACCESS_TOKEN_SAVED_AT));
    }

    @Test
    public void whenBlankToken_thenReturnsNull_andClearsMetadata() {
        prefs
                .edit()
                .putString(KEY_ACCESS_TOKEN, "   ")
                .putLong(KEY_ACCESS_TOKEN_SAVED_AT, System.currentTimeMillis())
                .commit();

        String token = store.getAccessToken();

        assertNull(token);
        assertFalse(prefs.contains(KEY_ACCESS_TOKEN));
        assertFalse(prefs.contains(KEY_ACCESS_TOKEN_SAVED_AT));
    }

    @Test
    public void whenFutureSavedAt_thenExpired_andClearsMetadata() {
        long now = System.currentTimeMillis();
        long future = now + ACCESS_TOKEN_TTL_MS + 1;

        prefs
                .edit()
                .putString(KEY_ACCESS_TOKEN, "jwt_token")
                .putLong(KEY_ACCESS_TOKEN_SAVED_AT, future)
                .commit();

        String token = store.getAccessToken();

        assertNull(token);
        assertFalse(prefs.contains(KEY_ACCESS_TOKEN));
        assertFalse(prefs.contains(KEY_ACCESS_TOKEN_SAVED_AT));
    }

    @Test
    public void whenOldSavedAt_thenExpired_andClearsMetadata() {
        long now = System.currentTimeMillis();
        long old = now - ACCESS_TOKEN_TTL_MS - 1;

        prefs
                .edit()
                .putString(KEY_ACCESS_TOKEN, "jwt_token")
                .putLong(KEY_ACCESS_TOKEN_SAVED_AT, old)
                .commit();

        String token = store.getAccessToken();

        assertNull(token);
        assertFalse(prefs.contains(KEY_ACCESS_TOKEN));
        assertFalse(prefs.contains(KEY_ACCESS_TOKEN_SAVED_AT));
    }

    @Test
    public void whenValidSavedAt_thenReturnsToken() {
        long now = System.currentTimeMillis();
        long validSavedAt = now - (ACCESS_TOKEN_TTL_MS - 1000);

        prefs
                .edit()
                .putString(KEY_ACCESS_TOKEN, "jwt_token")
                .putLong(KEY_ACCESS_TOKEN_SAVED_AT, validSavedAt)
                .commit();

        String token = store.getAccessToken();

        assertEquals("jwt_token", token);
        assertTrue(prefs.contains(KEY_ACCESS_TOKEN));
        assertTrue(prefs.contains(KEY_ACCESS_TOKEN_SAVED_AT));
    }

    private static SharedPreferences createEncryptedPrefs(Context context) throws Exception {
        MasterKey masterKey =
                new MasterKey.Builder(context)
                        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                        .build();

        return EncryptedSharedPreferences.create(
                context,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
    }
}

