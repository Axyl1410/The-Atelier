package com.axyl.the_atelier;

public final class FeedArticle {

    public final long id;
    public final String authorName;
    /** Phần sau tên (thường), ví dụ " đã đăng bài mới" */
    public final String actionText;
    public final String timeAgo;
    public final String bodyText;
    public final String likeCount;
    public final String commentCount;
    public final String shareCount;
    public final boolean premium;
    /** Màu placeholder khối media */
    public final int mediaColor;
    public final int avatarColor;

    public FeedArticle(
            long id,
            String authorName,
            String actionText,
            String timeAgo,
            String bodyText,
            String likeCount,
            String commentCount,
            String shareCount,
            boolean premium,
            int mediaColor,
            int avatarColor) {
        this.id = id;
        this.authorName = authorName;
        this.actionText = actionText;
        this.timeAgo = timeAgo;
        this.bodyText = bodyText;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.shareCount = shareCount;
        this.premium = premium;
        this.mediaColor = mediaColor;
        this.avatarColor = avatarColor;
    }
}
