package com.axyl.the_atelier.data.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * JVM unit tests for the 7-day token expiry policy.
 *
 * <p>{@link EncryptedTokenStore} depends on {@link androidx.security.crypto.EncryptedSharedPreferences}
 * which requires the Android framework and cannot run on the host JVM.  To keep tests fast and
 * dependency-free, this test uses {@link FakeTokenStore} — a pure-Java implementation that mirrors
 * the exact same TTL logic as {@link EncryptedTokenStore} and accepts an injectable {@link Clock}.
 *
 * <p>The TTL constant is sourced from {@link TokenStore#TOKEN_TTL_MS} (a pure-Java interface),
 * ensuring both the production implementation and this test always agree on the expiry window.
 */
public class TokenExpiryPolicyTest {

    /**
     * Minimal in-memory {@link TokenStore} that replicates the TTL policy of
     * {@link EncryptedTokenStore}, backed by a plain {@link HashMap}.
     */
    static final class FakeTokenStore implements TokenStore {
        private static final String KEY_TOKEN = "token";
        private static final String KEY_SAVED_AT = "savedAt";

        final Map<String, Object> store = new HashMap<>();
        private final Clock clock;

        FakeTokenStore(Clock clock) {
            this.clock = clock;
        }

        @Override
        public void saveAccessToken(String token) {
            store.put(KEY_TOKEN, token);
            store.put(KEY_SAVED_AT, clock.currentTimeMillis());
        }

        @Override
        public String getAccessToken() {
            String token = (String) store.get(KEY_TOKEN);
            if (token == null) {
                return null;
            }
            Object savedAtObj = store.get(KEY_SAVED_AT);
            long savedAt = savedAtObj instanceof Long ? (Long) savedAtObj : -1L;
            if (savedAt < 0 || clock.currentTimeMillis() - savedAt > TOKEN_TTL_MS) {
                clearAccessToken();
                return null;
            }
            return token;
        }

        @Override
        public void clearAccessToken() {
            store.remove(KEY_TOKEN);
            store.remove(KEY_SAVED_AT);
        }
    }

    /** Mutable clock whose time can be advanced for testing. */
    static final class FakeClock implements Clock {
        private long now;

        FakeClock(long initialNow) {
            this.now = initialNow;
        }

        void advanceMs(long ms) {
            now += ms;
        }

        @Override
        public long currentTimeMillis() {
            return now;
        }
    }

    private FakeClock clock;
    private FakeTokenStore store;

    @Before
    public void setUp() {
        clock = new FakeClock(1_000_000L);
        store = new FakeTokenStore(clock);
    }

    @Test
    public void getAccessToken_returnsNullWhenNoTokenSaved() {
        assertNull(store.getAccessToken());
    }

    @Test
    public void getAccessToken_returnsFreshToken() {
        store.saveAccessToken("my_token");
        assertEquals("my_token", store.getAccessToken());
    }

    @Test
    public void getAccessToken_returnsTokenJustBeforeExpiry() {
        store.saveAccessToken("my_token");
        clock.advanceMs(TokenStore.TOKEN_TTL_MS - 1);
        assertEquals("my_token", store.getAccessToken());
    }

    @Test
    public void getAccessToken_returnsNullWhenTokenExpired() {
        store.saveAccessToken("my_token");
        clock.advanceMs(TokenStore.TOKEN_TTL_MS + 1);
        assertNull(store.getAccessToken());
    }

    @Test
    public void getAccessToken_clearsStorageWhenExpired() {
        store.saveAccessToken("my_token");
        clock.advanceMs(TokenStore.TOKEN_TTL_MS + 1);

        store.getAccessToken(); // triggers expiry cleanup

        // Both keys must be gone after expiry cleanup
        assertNull(store.store.get("token"));
        assertNull(store.store.get("savedAt"));
    }

    @Test
    public void getAccessToken_treatsLegacyTokenWithoutSavedAtAsExpired() {
        // Simulate a token saved before TTL policy was introduced (no savedAt key).
        store.store.put("token", "legacy_token");

        assertNull(store.getAccessToken());
    }

    @Test
    public void clearAccessToken_removesTokenAndSavedAt() {
        store.saveAccessToken("my_token");
        store.clearAccessToken();

        assertNull(store.getAccessToken());
        assertNull(store.store.get("savedAt"));
    }

    @Test
    public void saveAccessToken_overwritesPreviousToken() {
        store.saveAccessToken("token_v1");
        clock.advanceMs(1000L);
        store.saveAccessToken("token_v2");

        assertEquals("token_v2", store.getAccessToken());
    }
}
