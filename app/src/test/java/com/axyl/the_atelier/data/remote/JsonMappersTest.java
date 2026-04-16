package com.axyl.the_atelier.data.remote;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
import org.junit.Test;

public class JsonMappersTest {

    @Test
    public void toUser_mapsAllFields() throws Exception {
        JSONObject o = new JSONObject()
                .put("id", "u_1")
                .put("name", "Axyl")
                .put("email", "axyl@example.com")
                .put("emailVerified", true)
                .put("image", "https://img.test/u_1.png")
                .put("createdAt", 1711111111000L)
                .put("updatedAt", 1711111112000L)
                .put("username", "axyl")
                .put("displayUsername", "AXYL")
                .put("role", "admin")
                .put("banned", false)
                .put("banReason", JSONObject.NULL)
                .put("banExpires", JSONObject.NULL);

        User user = JsonMappers.toUser(o);

        assertEquals("u_1", user.id);
        assertEquals("Axyl", user.name);
        assertEquals("axyl@example.com", user.email);
        assertTrue(user.emailVerified);
        assertEquals("https://img.test/u_1.png", user.image);
        assertEquals(1711111111000L, user.createdAt);
        assertEquals(1711111112000L, user.updatedAt);
        assertEquals("axyl", user.username);
        assertEquals("AXYL", user.displayUsername);
        assertEquals("admin", user.role);
        assertEquals(Boolean.FALSE, user.banned);
        assertNull(user.banReason);
        assertNull(user.banExpires);
    }

    @Test
    public void toUser_handlesMissingNullableFieldsAsNull() throws Exception {
        JSONObject o = new JSONObject()
                .put("id", "u_2")
                .put("name", "No Extras")
                .put("email", "noextras@example.com")
                .put("emailVerified", false)
                .put("createdAt", 1L)
                .put("updatedAt", 2L);

        User user = JsonMappers.toUser(o);

        assertNull(user.image);
        assertNull(user.username);
        assertNull(user.displayUsername);
        assertNull(user.role);
        assertNull(user.banned);
        assertNull(user.banReason);
        assertNull(user.banExpires);
        assertFalse(user.emailVerified);
    }

    @Test
    public void toPost_mapsRequiredAndNullableFields() throws Exception {
        JSONObject o = new JSONObject()
                .put("id", "p_1")
                .put("slug", "hello-world")
                .put("title", "Hello")
                .put("content", "World")
                .put("summary", "short")
                .put("coverImage", JSONObject.NULL)
                .put("isPublished", true)
                .put("authorId", "u_1")
                .put("createdAt", 3L)
                .put("updatedAt", 4L);

        Post post = JsonMappers.toPost(o);

        assertEquals("p_1", post.id);
        assertEquals("hello-world", post.slug);
        assertEquals("Hello", post.title);
        assertEquals("World", post.content);
        assertEquals("short", post.summary);
        assertNull(post.coverImage);
        assertTrue(post.isPublished);
        assertEquals("u_1", post.authorId);
        assertEquals(3L, post.createdAt);
        assertEquals(4L, post.updatedAt);
    }

    @Test
    public void toComment_mapsParentIdNullable() throws Exception {
        JSONObject o = new JSONObject()
                .put("id", "c_1")
                .put("content", "Nice post")
                .put("postId", "p_1")
                .put("authorId", "u_1")
                .put("createdAt", 5L)
                .put("updatedAt", 6L);

        Comment comment = JsonMappers.toComment(o);

        assertEquals("c_1", comment.id);
        assertNull(comment.parentId);
    }

    @Test
    public void toSession_mapsAllFields() throws Exception {
        JSONObject o = new JSONObject()
                .put("id", "s_1")
                .put("expiresAt", 2000L)
                .put("token", "jwt_token")
                .put("createdAt", 1000L)
                .put("updatedAt", 1500L)
                .put("ipAddress", "127.0.0.1")
                .put("userAgent", "Android")
                .put("userId", "u_1")
                .put("impersonatedBy", JSONObject.NULL);

        Session session = JsonMappers.toSession(o);

        assertEquals("s_1", session.id);
        assertEquals(2000L, session.expiresAt);
        assertEquals("jwt_token", session.token);
        assertEquals("127.0.0.1", session.ipAddress);
        assertEquals("Android", session.userAgent);
        assertEquals("u_1", session.userId);
        assertNull(session.impersonatedBy);
    }

    @Test
    public void toAccount_mapsAllFields() throws Exception {
        JSONObject o = new JSONObject()
                .put("id", "a_1")
                .put("accountId", "acc_1")
                .put("providerId", "google")
                .put("userId", "u_1")
                .put("accessToken", "at")
                .put("refreshToken", "rt")
                .put("idToken", "id")
                .put("accessTokenExpiresAt", 100L)
                .put("refreshTokenExpiresAt", 200L)
                .put("scope", "profile email")
                .put("password", JSONObject.NULL)
                .put("createdAt", 1L)
                .put("updatedAt", 2L);

        Account account = JsonMappers.toAccount(o);

        assertEquals("a_1", account.id);
        assertEquals("acc_1", account.accountId);
        assertEquals("google", account.providerId);
        assertEquals("u_1", account.userId);
        assertEquals("at", account.accessToken);
        assertEquals("rt", account.refreshToken);
        assertEquals("id", account.idToken);
        assertEquals(Long.valueOf(100L), account.accessTokenExpiresAt);
        assertEquals(Long.valueOf(200L), account.refreshTokenExpiresAt);
        assertEquals("profile email", account.scope);
        assertNull(account.password);
    }

    @Test
    public void toTag_andPostTag_mapFields() throws Exception {
        JSONObject tagJson = new JSONObject()
                .put("id", "t_1")
                .put("name", "Backend")
                .put("slug", "backend");
        JSONObject postTagJson = new JSONObject()
                .put("postId", "p_1")
                .put("tagId", "t_1");

        Tag tag = JsonMappers.toTag(tagJson);
        PostTag postTag = JsonMappers.toPostTag(postTagJson);

        assertEquals("t_1", tag.id);
        assertEquals("Backend", tag.name);
        assertEquals("backend", tag.slug);
        assertEquals("p_1", postTag.postId);
        assertEquals("t_1", postTag.tagId);
    }

    @Test
    public void toPostLike_bookmark_follow_mapFields() throws Exception {
        JSONObject likeJson = new JSONObject()
                .put("userId", "u_1")
                .put("postId", "p_1")
                .put("createdAt", 11L);
        JSONObject bookmarkJson = new JSONObject()
                .put("userId", "u_1")
                .put("postId", "p_1")
                .put("createdAt", 12L);
        JSONObject followJson = new JSONObject()
                .put("followerId", "u_1")
                .put("followingId", "u_2")
                .put("createdAt", 13L);

        PostLike like = JsonMappers.toPostLike(likeJson);
        Bookmark bookmark = JsonMappers.toBookmark(bookmarkJson);
        Follow follow = JsonMappers.toFollow(followJson);

        assertEquals("u_1", like.userId);
        assertEquals("p_1", like.postId);
        assertEquals(11L, like.createdAt);

        assertEquals("u_1", bookmark.userId);
        assertEquals("p_1", bookmark.postId);
        assertEquals(12L, bookmark.createdAt);

        assertEquals("u_1", follow.followerId);
        assertEquals("u_2", follow.followingId);
        assertEquals(13L, follow.createdAt);
    }

    @Test(expected = JSONException.class)
    public void toPost_throwsWhenMissingRequiredField() throws Exception {
        JSONObject invalid = new JSONObject()
                .put("id", "p_2")
                .put("slug", "missing-title")
                .put("content", "No title")
                .put("isPublished", false)
                .put("authorId", "u_1")
                .put("createdAt", 1L)
                .put("updatedAt", 2L);

        JsonMappers.toPost(invalid);
    }

    @Test(expected = JSONException.class)
    public void toSession_throwsWhenMissingRequiredField() throws Exception {
        JSONObject invalid = new JSONObject()
                .put("id", "s_2")
                .put("token", "missing_expires")
                .put("createdAt", 1L)
                .put("updatedAt", 2L)
                .put("userId", "u_1");

        JsonMappers.toSession(invalid);
    }

    @Test(expected = JSONException.class)
    public void toUser_throwsWhenMissingRequiredField() throws Exception {
        JSONObject invalid = new JSONObject()
                .put("id", "u_missing")
                .put("name", "No Email")
                .put("emailVerified", true)
                .put("createdAt", 1L)
                .put("updatedAt", 2L);

        JsonMappers.toUser(invalid);
    }

    @Test(expected = JSONException.class)
    public void toAccount_throwsWhenMissingRequiredField() throws Exception {
        JSONObject invalid = new JSONObject()
                .put("id", "a_missing")
                .put("accountId", "acc_x")
                .put("providerId", "google")
                .put("userId", "u_1")
                .put("createdAt", 1L);

        JsonMappers.toAccount(invalid);
    }

    @Test(expected = JSONException.class)
    public void toComment_throwsWhenMissingRequiredField() throws Exception {
        JSONObject invalid = new JSONObject()
                .put("id", "c_missing")
                .put("content", "x")
                .put("authorId", "u_1")
                .put("createdAt", 1L)
                .put("updatedAt", 2L);

        JsonMappers.toComment(invalid);
    }

    @Test(expected = JSONException.class)
    public void toTag_throwsWhenMissingRequiredField() throws Exception {
        JSONObject invalid = new JSONObject()
                .put("id", "t_missing")
                .put("name", "Tag no slug");

        JsonMappers.toTag(invalid);
    }

    @Test(expected = JSONException.class)
    public void toPostTag_throwsWhenMissingRequiredField() throws Exception {
        JSONObject invalid = new JSONObject()
                .put("postId", "p_1");

        JsonMappers.toPostTag(invalid);
    }

    @Test(expected = JSONException.class)
    public void toPostLike_throwsWhenMissingRequiredField() throws Exception {
        JSONObject invalid = new JSONObject()
                .put("userId", "u_1")
                .put("postId", "p_1");

        JsonMappers.toPostLike(invalid);
    }

    @Test(expected = JSONException.class)
    public void toBookmark_throwsWhenMissingRequiredField() throws Exception {
        JSONObject invalid = new JSONObject()
                .put("userId", "u_1")
                .put("postId", "p_1");

        JsonMappers.toBookmark(invalid);
    }

    @Test(expected = JSONException.class)
    public void toFollow_throwsWhenMissingRequiredField() throws Exception {
        JSONObject invalid = new JSONObject()
                .put("followerId", "u_1")
                .put("followingId", "u_2");

        JsonMappers.toFollow(invalid);
    }
}

