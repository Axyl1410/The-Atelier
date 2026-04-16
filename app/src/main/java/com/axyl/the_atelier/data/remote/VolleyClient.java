package com.axyl.the_atelier.data.remote;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.axyl.the_atelier.data.session.TokenStore;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class VolleyClient {
    private final RequestQueue queue;
    private final String baseUrl;
    @Nullable private final TokenStore tokenStore;

    public VolleyClient(@NonNull Context context, @NonNull String baseUrl) {
        this(context, baseUrl, null);
    }

    public VolleyClient(
            @NonNull Context context,
            @NonNull String baseUrl,
            @Nullable TokenStore tokenStore
    ) {
        this.queue = Volley.newRequestQueue(context.getApplicationContext());
        this.baseUrl = baseUrl;
        this.tokenStore = tokenStore;
    }

    public void postJson(
            @NonNull String path,
            @Nullable JSONObject body,
            @Nullable Map<String, String> headers,
            @NonNull ApiResultCallback<JSONObject> callback
    ) {
        requestJson(Request.Method.POST, path, body, headers, true, callback);
    }

    public void postJsonNoAuth(
            @NonNull String path,
            @Nullable JSONObject body,
            @Nullable Map<String, String> headers,
            @NonNull ApiResultCallback<JSONObject> callback
    ) {
        requestJson(Request.Method.POST, path, body, headers, false, callback);
    }

    public void getJson(
            @NonNull String path,
            @Nullable Map<String, String> headers,
            @NonNull ApiResultCallback<JSONObject> callback
    ) {
        requestJson(Request.Method.GET, path, null, headers, true, callback);
    }

    public void putJson(
            @NonNull String path,
            @Nullable JSONObject body,
            @Nullable Map<String, String> headers,
            @NonNull ApiResultCallback<JSONObject> callback
    ) {
        requestJson(Request.Method.PUT, path, body, headers, true, callback);
    }

    public void patchJson(
            @NonNull String path,
            @Nullable JSONObject body,
            @Nullable Map<String, String> headers,
            @NonNull ApiResultCallback<JSONObject> callback
    ) {
        requestJson(Request.Method.PATCH, path, body, headers, true, callback);
    }

    public void deleteJson(
            @NonNull String path,
            @Nullable JSONObject body,
            @Nullable Map<String, String> headers,
            @NonNull ApiResultCallback<JSONObject> callback
    ) {
        requestJson(Request.Method.DELETE, path, body, headers, true, callback);
    }

    private void requestJson(
            int method,
            @NonNull String path,
            @Nullable JSONObject body,
            @Nullable Map<String, String> headers,
            boolean withAuth,
            @NonNull ApiResultCallback<JSONObject> callback
    ) {
        String url = baseUrl + path;

        JsonObjectRequest request = new JsonObjectRequest(
                method,
                url,
                body,
                callback::onSuccess,
                error -> callback.onError(toApiError(error))
        ) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> merged = new HashMap<>();
                merged.put("Accept", "application/json");
                if (withAuth) {
                    String token = tokenStore == null ? null : tokenStore.getAccessToken();
                    if (token != null && !token.trim().isEmpty()) {
                        merged.put("Authorization", "Bearer " + token);
                    }
                }
                if (headers != null) merged.putAll(headers);
                return merged;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                15_000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        queue.add(request);
    }

    private static ApiError toApiError(@NonNull VolleyError error) {
        NetworkResponse nr = error.networkResponse;
        if (nr == null) {
            return new ApiError(-1, null, error.toString());
        }

        String body = null;
        if (nr.data != null) {
            body = new String(nr.data, StandardCharsets.UTF_8);
        }

        return new ApiError(nr.statusCode, body, error.toString());
    }
}

