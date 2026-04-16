package com.axyl.the_atelier;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.axyl.the_atelier.data.session.EncryptedTokenStore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class AuthRedirectTest {
    private static final String PREFS_NAME = "secure_session_prefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_ACCESS_TOKEN_SAVED_AT = "access_token_saved_at";
    private static final long ACCESS_TOKEN_TTL_MS = TimeUnit.DAYS.toMillis(7);

    private Context context;
    private SharedPreferences prefs;
    private android.app.Instrumentation instrumentation;

    @Before
    public void setUp() throws Exception {
        instrumentation = InstrumentationRegistry.getInstrumentation();
        context = instrumentation.getTargetContext();
        prefs = createEncryptedPrefs(context);
        prefs.edit().clear().commit();
    }

    @After
    public void tearDown() {
        // Keep each test isolated.
        prefs.edit().clear().commit();
    }

    @Test
    public void landingActivity_whenNoToken_thenStartsSignInActivity() {
        startLandingAndAssertSignIn();
    }

    @Test
    public void landingActivity_whenExpiredToken_thenStartsSignInActivity() {
        seedExpiredToken();
        startLandingAndAssertSignIn();
    }

    @Test
    public void homeActivity_whenExpiredToken_thenStartsSignInActivity() {
        seedExpiredToken();

        launchHomeAndWaitForSignIn();
    }

    private void startLandingAndAssertSignIn() {
        android.app.ActivityMonitor monitor =
                instrumentation.addMonitor(SignInActivity.class.getName(), null, false);
        Activity signIn = null;
        try {
            Intent intent = new Intent(context, LandingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            instrumentation.startActivitySync(intent);

            signIn = instrumentation.waitForMonitorWithTimeout(monitor, 5000);
            assertNotNull(signIn);
        } finally {
            if (signIn != null) {
                signIn.finish();
            }
            instrumentation.removeMonitor(monitor);
        }
    }

    private void launchHomeAndWaitForSignIn() {
        android.app.ActivityMonitor monitor =
                instrumentation.addMonitor(SignInActivity.class.getName(), null, false);
        Activity signIn = null;
        try {
            Intent intent = new Intent(context, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            instrumentation.startActivitySync(intent);

            signIn = instrumentation.waitForMonitorWithTimeout(monitor, 5000);
            assertNotNull(signIn);
        } finally {
            if (signIn != null) {
                signIn.finish();
            }
            instrumentation.removeMonitor(monitor);
        }
    }

    private void seedExpiredToken() {
        long now = System.currentTimeMillis();
        long expiredAt = now - ACCESS_TOKEN_TTL_MS - 1;

        prefs
                .edit()
                .putString(KEY_ACCESS_TOKEN, "jwt_token")
                .putLong(KEY_ACCESS_TOKEN_SAVED_AT, expiredAt)
                .commit();

        // Ensure any existing cached instance doesn't keep stale values.
        new EncryptedTokenStore(context).clearAccessToken();
        prefs
                .edit()
                .putString(KEY_ACCESS_TOKEN, "jwt_token")
                .putLong(KEY_ACCESS_TOKEN_SAVED_AT, expiredAt)
                .commit();
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

