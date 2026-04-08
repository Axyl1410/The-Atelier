package com.axyl.the_atelier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

final class FeedDemoData {

    private static final String[] AUTHORS = {
        "Minh An",
        "Lan Chi",
        "Gia Huy",
        "Quỳnh Mai",
        "Tuấn Khôi",
        "Đặng Huỳnh Đức",
        "Hải Yến"
    };

    private static final String[] ACTIONS = {
        " đã đăng bài mới",
        " đã chia sẻ một ghi chú",
        " đã cập nhật ảnh bìa",
        " đổi ảnh đại diện",
        " đã xuất bản một câu chuyện",
        " đăng ảnh mới",
        " đã lưu một bài vào Thư viện"
    };

    private static final String[] TIME_AGO = {
        "2 giờ",
        "6 giờ",
        "Hôm qua",
        "3 ngày",
        "1 tuần",
        "12 phút"
    };

    private static final String[] BODIES = {
        "Không phải lúc nào im lặng cũng là đồng ý. Đôi khi đó là khoảng trống để bạn lắng nghe chính mình…",
        "Viết không cần hoàn hảo ngay từ câu đầu. Bạn chỉ cần bắt đầu, rồi chỉnh sau.",
        "Thành phố thức dậy với những âm thanh quen thuộc. Bài viết là một trong số đó.",
        "Đọc kỹ hơn một chút, hiểu sâu hơn một chút — đủ để đời dịu lại.",
        "Ghi lại để không phải nhớ bằng đầu. Để trái tim còn chỗ cho điều mới.",
        "Công cụ thay đổi, nhưng giọng văn vẫn là thứ chỉ bạn có.",
        "Màn hình nhỏ không có nghĩa là ý tưởng nhỏ."
    };

    private static final String[] LIKES = {
        "892",
        "1,3 nghìn",
        "156",
        "3,7 nghìn",
        "45",
        "12"
    };

    private static final String[] COMMENTS = {
        "319",
        "450",
        "32",
        "12",
        "89",
        "201"
    };

    private static final String[] SHARES = {
        "32",
        "12",
        "5",
        "108",
        "0",
        "24"
    };

    private static final int[] MEDIA_COLORS = {
        0xFFE8E4E1,
        0xFFDDE5F0,
        0xFFF2E6E0,
        0xFFE6E8F5,
        0xFFE3F0E8,
        0xFFF5F0E3
    };

    private static final int[] AVATAR_COLORS = {
        0xFF456556,
        0xFF5D4E37,
        0xFF2C5282,
        0xFF744210,
        0xFF553C9A,
        0xFFC05621
    };

    /** Quét id cho tab thường khi tìm kiếm. */
    private static final int SEARCH_SCAN_REGULAR = 1400;

    /** Khối id bắt đầu cho bài nổi bật (khớp offset trong page()). */
    private static final int FEATURED_OFFSET = 1000;

    private static final int SEARCH_SCAN_FEATURED_SPAN = 800;

    private FeedDemoData() {}

    static List<FeedArticle> page(int pageIndex, int pageSize, boolean featured) {
        int offset = featured ? FEATURED_OFFSET : 0;
        List<FeedArticle> list = new ArrayList<>(pageSize);
        int base = pageIndex * pageSize;
        for (int i = 0; i < pageSize; i++) {
            long id = offset + base + i;
            list.add(articleForId(id, featured));
        }
        return list;
    }

    /**
     * Tìm trong dữ liệu demo: tên tác giả, dòng hành động, nội dung, thời gian.
     *
     * @param maxResults tối đa số bài trả về
     */
    static List<FeedArticle> search(String rawQuery, int maxResults) {
        String q = rawQuery.trim().toLowerCase(Locale.forLanguageTag("vi"));
        if (q.isEmpty()) {
            return Collections.emptyList();
        }
        List<FeedArticle> out = new ArrayList<>();
        for (long id = 0; id < SEARCH_SCAN_REGULAR && out.size() < maxResults; id++) {
            FeedArticle a = articleForId(id, false);
            if (matches(a, q)) {
                out.add(a);
            }
        }
        for (long i = 0; i < SEARCH_SCAN_FEATURED_SPAN && out.size() < maxResults; i++) {
            long id = FEATURED_OFFSET + i;
            FeedArticle a = articleForId(id, true);
            if (matches(a, q)) {
                out.add(a);
            }
        }
        return out;
    }

    private static boolean matches(FeedArticle a, String q) {
        return contains(a.authorName, q)
                || contains(a.actionText, q)
                || contains(a.bodyText, q)
                || contains(a.timeAgo, q);
    }

    private static boolean contains(String s, String q) {
        if (s == null || s.isEmpty()) {
            return false;
        }
        return s.toLowerCase(Locale.forLanguageTag("vi")).contains(q);
    }

    private static FeedArticle articleForId(long id, boolean featured) {
        String action = ACTIONS[(int) (id % ACTIONS.length)];
        if (featured) {
            action = action + " · Nổi bật";
        }
        return new FeedArticle(
                id,
                AUTHORS[(int) (id % AUTHORS.length)],
                action,
                TIME_AGO[(int) (id % TIME_AGO.length)],
                BODIES[(int) (id % BODIES.length)],
                LIKES[(int) (id % LIKES.length)],
                COMMENTS[(int) (id % COMMENTS.length)],
                SHARES[(int) (id % SHARES.length)],
                id % 5 == 0,
                MEDIA_COLORS[(int) (id % MEDIA_COLORS.length)] | 0xFF000000,
                AVATAR_COLORS[(int) (id % AVATAR_COLORS.length)] | 0xFF000000);
    }
}
