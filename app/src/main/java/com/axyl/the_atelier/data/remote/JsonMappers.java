package com.axyl.the_atelier.data.remote;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.axyl.the_atelier.domain.model.Account;
import com.axyl.the_atelier.domain.model.Bookmark;
import com.axyl.the_atelier.domain.model.Comment;
import com.axyl.the_atelier.domain.model.Follow;
import com.axyl.the_atelier.domain.model.Post;
import com.axyl.the_atelier.domain.model.PostLike;
import com.axyl.the_atelier.domain.model.PostTag;
import com.axyl.the_atelier.domain.model.Session;
import com.axyl.the_atelier.domain.model.Tag;
import com.axyl.the_atelier.domain.model.User;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Centralized JSON -> domain model mapping helpers.
 */
public final class JsonMappers {
    private JsonMappers() {
    }

    @NonNull
    public static User toUser(@NonNull JSONObject o) throws JSONException {
        return new User(
                reqString(o, "id"),
                reqString(o, "name"),
                reqString(o, "email"),
                reqBoolean(o, "emailVerified"),
                optString(o, "image"),
                reqLong(o, "createdAt"),
                reqLong(o, "updatedAt"),
                optString(o, "username"),
                optString(o, "displayUsername"),
                optString(o, "role"),
                optBoolean(o, "banned"),
                optString(o, "banReason"),
                optLong(o, "banExpires")
        );
    }

    @NonNull
    public static Session toSession(@NonNull JSONObject o) throws JSONException {
        return new Session(
                reqString(o, "id"),
                reqLong(o, "expiresAt"),
                reqString(o, "token"),
                reqLong(o, "createdAt"),
                reqLong(o, "updatedAt"),
                optString(o, "ipAddress"),
                optString(o, "userAgent"),
                reqString(o, "userId"),
                optString(o, "impersonatedBy")
        );
    }

    @NonNull
    public static Account toAccount(@NonNull JSONObject o) throws JSONException {
        return new Account(
                reqString(o, "id"),
                reqString(o, "accountId"),
                reqString(o, "providerId"),
                reqString(o, "userId"),
                optString(o, "accessToken"),
                optString(o, "refreshToken"),
                optString(o, "idToken"),
                optLong(o, "accessTokenExpiresAt"),
                optLong(o, "refreshTokenExpiresAt"),
                optString(o, "scope"),
                optString(o, "password"),
                reqLong(o, "createdAt"),
                reqLong(o, "updatedAt")
        );
    }

    @NonNull
    public static Post toPost(@NonNull JSONObject o) throws JSONException {
        return new Post(
                reqString(o, "id"),
                reqString(o, "slug"),
                reqString(o, "title"),
                reqString(o, "content"),
                optString(o, "summary"),
                optString(o, "coverImage"),
                reqBoolean(o, "isPublished"),
                reqString(o, "authorId"),
                reqLong(o, "createdAt"),
                reqLong(o, "updatedAt")
        );
    }

    @NonNull
    public static Tag toTag(@NonNull JSONObject o) throws JSONException {
        return new Tag(
                reqString(o, "id"),
                reqString(o, "name"),
                reqString(o, "slug")
        );
    }

    @NonNull
    public static PostTag toPostTag(@NonNull JSONObject o) throws JSONException {
        return new PostTag(
                reqString(o, "postId"),
                reqString(o, "tagId")
        );
    }

    @NonNull
    public static Comment toComment(@NonNull JSONObject o) throws JSONException {
        return new Comment(
                reqString(o, "id"),
                reqString(o, "content"),
                reqString(o, "postId"),
                reqString(o, "authorId"),
                optString(o, "parentId"),
                reqLong(o, "createdAt"),
                reqLong(o, "updatedAt")
        );
    }

    @NonNull
    public static PostLike toPostLike(@NonNull JSONObject o) throws JSONException {
        return new PostLike(
                reqString(o, "userId"),
                reqString(o, "postId"),
                reqLong(o, "createdAt")
        );
    }

    @NonNull
    public static Bookmark toBookmark(@NonNull JSONObject o) throws JSONException {
        return new Bookmark(
                reqString(o, "userId"),
                reqString(o, "postId"),
                reqLong(o, "createdAt")
        );
    }

    @NonNull
    public static Follow toFollow(@NonNull JSONObject o) throws JSONException {
        return new Follow(
                reqString(o, "followerId"),
                reqString(o, "followingId"),
                reqLong(o, "createdAt")
        );
    }

    @NonNull
    private static String reqString(@NonNull JSONObject o, @NonNull String key) throws JSONException {
        return o.getString(key);
    }

    private static boolean reqBoolean(@NonNull JSONObject o, @NonNull String key) throws JSONException {
        return o.getBoolean(key);
    }

    private static long reqLong(@NonNull JSONObject o, @NonNull String key) throws JSONException {
        return o.getLong(key);
    }

    @Nullable
    private static String optString(@NonNull JSONObject o, @NonNull String key) {
        if (o.isNull(key) || !o.has(key)) return null;
        return o.optString(key, null);
    }

    @Nullable
    private static Boolean optBoolean(@NonNull JSONObject o, @NonNull String key) {
        if (o.isNull(key) || !o.has(key)) return null;
        return o.optBoolean(key);
    }

    @Nullable
    private static Long optLong(@NonNull JSONObject o, @NonNull String key) {
        if (o.isNull(key) || !o.has(key)) return null;
        return o.optLong(key);
    }
}

