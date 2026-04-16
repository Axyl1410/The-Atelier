package com.axyl.the_atelier.data.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.axyl.the_atelier.data.remote.ApiError;
import com.axyl.the_atelier.data.remote.ApiResultCallback;
import com.axyl.the_atelier.data.remote.JsonMappers;
import com.axyl.the_atelier.data.remote.VolleyClient;
import com.axyl.the_atelier.data.session.TokenStore;
import com.axyl.the_atelier.domain.model.SignInResult;
import com.axyl.the_atelier.domain.model.SignUpResult;
import com.axyl.the_atelier.domain.model.User;

import org.json.JSONException;
import org.json.JSONObject;

public final class AuthRepository {
    private final VolleyClient client;
    private final TokenStore tokenStore;

    public AuthRepository(@NonNull VolleyClient client, @NonNull TokenStore tokenStore) {
        this.client = client;
        this.tokenStore = tokenStore;
    }

    public void signInEmail(
            @NonNull String email,
            @NonNull String password,
            boolean rememberMe,
            @Nullable String callbackUrl,
            @NonNull ApiResultCallback<SignInResult> callback
    ) {
        JSONObject body = new JSONObject();
        try {
            body.put("email", email);
            body.put("password", password);
            if (callbackUrl != null && !callbackUrl.trim().isEmpty()) {
                body.put("callbackURL", callbackUrl);
            }
            body.put("rememberMe", rememberMe);
        } catch (JSONException e) {
            callback.onError(new ApiError(-1, null, e.getMessage()));
            return;
        }

        client.postJsonNoAuth(
                "/api/auth/sign-in/email",
                body,
                null,
                new ApiResultCallback<JSONObject>() {
                    @Override
                    public void onSuccess(@NonNull JSONObject data) {
                        try {
                            String token = data.getString("token");
                            boolean redirect = data.optBoolean("redirect", false);
                            String url = data.isNull("url") ? null : data.optString("url", null);

                            User user = null;
                            if (data.has("user") && !data.isNull("user")) {
                                // Keep sign-in robust even if user payload shape changes.
                                try {
                                    user = JsonMappers.toUser(data.getJSONObject("user"));
                                } catch (JSONException ignored) {
                                    user = null;
                                }
                            }

                            tokenStore.saveAccessToken(token);
                            callback.onSuccess(new SignInResult(redirect, token, url, user));
                        } catch (JSONException e) {
                            callback.onError(new ApiError(-1, data.toString(), e.getMessage()));
                        }
                    }

                    @Override
                    public void onError(@NonNull ApiError error) {
                        callback.onError(error);
                    }
                }
        );
    }

    public void signUpEmail(
            @NonNull String name,
            @NonNull String email,
            @NonNull String password,
            @Nullable String image,
            @Nullable String callbackUrl,
            boolean rememberMe,
            @NonNull ApiResultCallback<SignUpResult> callback
    ) {
        JSONObject body = new JSONObject();
        try {
            body.put("name", name);
            body.put("email", email);
            body.put("password", password);
            if (image != null && !image.trim().isEmpty()) {
                body.put("image", image);
            }
            if (callbackUrl != null && !callbackUrl.trim().isEmpty()) {
                body.put("callbackURL", callbackUrl);
            }
            body.put("rememberMe", rememberMe);
        } catch (JSONException e) {
            callback.onError(new ApiError(-1, null, e.getMessage()));
            return;
        }

        client.postJsonNoAuth(
                "/api/auth/sign-up/email",
                body,
                null,
                new ApiResultCallback<JSONObject>() {
                    @Override
                    public void onSuccess(@NonNull JSONObject data) {
                        try {
                            String token = data.getString("token");
                            User user = null;
                            if (data.has("user") && !data.isNull("user")) {
                                try {
                                    user = JsonMappers.toUser(data.getJSONObject("user"));
                                } catch (JSONException ignored) {
                                    user = null;
                                }
                            }
                            // Sign-up flow requires email verification before sign-in.
                            // Do not establish an authenticated session at this step.
                            tokenStore.clearAccessToken();
                            callback.onSuccess(new SignUpResult(token, user));
                        } catch (JSONException e) {
                            callback.onError(new ApiError(-1, data.toString(), e.getMessage()));
                        }
                    }

                    @Override
                    public void onError(@NonNull ApiError error) {
                        callback.onError(error);
                    }
                }
        );
    }
}

