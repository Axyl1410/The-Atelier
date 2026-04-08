package com.axyl.the_atelier;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private static final int PAGE_SIZE = 8;
    private static final int PRELOAD_THRESHOLD = 5;
    private static final int MAX_PAGES = 80;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private DrawerLayout drawerRoot;
    private PopupWindow profilePopup;
    private FeedAdapter adapter;
    private ProgressBar loadMoreProgress;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private int pageIndex;
    private boolean loading;
    private boolean featuredTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        drawerRoot = findViewById(R.id.drawerRoot);
        ViewCompat.setOnApplyWindowInsetsListener(drawerRoot, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.home_tab_for_you));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.home_tab_featured));

        recyclerView = findViewById(R.id.feedRecycler);
        loadMoreProgress = findViewById(R.id.loadMoreProgress);
        adapter = new FeedAdapter(this);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(
                new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                        if (dy <= 0) {
                            return;
                        }
                        int lastVisible = layoutManager.findLastVisibleItemPosition();
                        int total = adapter.getItemCount();
                        if (total == 0 || loading) {
                            return;
                        }
                        if (lastVisible >= total - PRELOAD_THRESHOLD) {
                            loadNextPage();
                        }
                    }
                });

        tabLayout.addOnTabSelectedListener(
                new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        boolean nextFeatured = tab.getPosition() == 1;
                        if (nextFeatured != featuredTab) {
                            featuredTab = nextFeatured;
                            resetFeed();
                        }
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {}

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {}
                });

        ImageButton btnMenu = findViewById(R.id.btnMenu);
        ImageButton btnSearch = findViewById(R.id.btnSearch);
        ImageView avatar = findViewById(R.id.avatar);
        btnMenu.setOnClickListener(v -> drawerRoot.openDrawer(GravityCompat.START));
        btnSearch.setOnClickListener(
                v -> startActivity(new Intent(HomeActivity.this, SearchActivity.class)));
        avatar.setOnClickListener(
                v -> {
                    if (drawerRoot.isDrawerOpen(GravityCompat.START)) {
                        drawerRoot.closeDrawer(GravityCompat.START);
                    }
                    toggleProfileMenu();
                });

        View.OnClickListener closeDrawer =
                v -> drawerRoot.closeDrawer(GravityCompat.START);
        findViewById(R.id.drawerBtnClose).setOnClickListener(closeDrawer);
        findViewById(R.id.drawerRowHome).setOnClickListener(closeDrawer);
        findViewById(R.id.drawerRowLibrary)
                .setOnClickListener(
                        v -> {
                            drawerRoot.closeDrawer(GravityCompat.START);
                            StaticPageActivity.start(
                                    this, R.string.static_library_title, R.string.static_library_body);
                        });
        findViewById(R.id.drawerRowProfile)
                .setOnClickListener(
                        v -> {
                            drawerRoot.closeDrawer(GravityCompat.START);
                            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                        });
        findViewById(R.id.drawerRowStories)
                .setOnClickListener(
                        v -> {
                            drawerRoot.closeDrawer(GravityCompat.START);
                            StaticPageActivity.start(
                                    this, R.string.static_stories_title, R.string.static_stories_body);
                        });
        findViewById(R.id.drawerRowStats)
                .setOnClickListener(
                        v -> {
                            drawerRoot.closeDrawer(GravityCompat.START);
                            StaticPageActivity.start(
                                    this, R.string.static_stats_title, R.string.static_stats_body);
                        });
        findViewById(R.id.drawerRowFollowing)
                .setOnClickListener(
                        v -> {
                            drawerRoot.closeDrawer(GravityCompat.START);
                            StaticPageActivity.start(
                                    this,
                                    R.string.static_following_title,
                                    R.string.static_following_body);
                        });
        findViewById(R.id.drawerRowFindFollow)
                .setOnClickListener(
                        v -> {
                            drawerRoot.closeDrawer(GravityCompat.START);
                            StaticPageActivity.start(
                                    this,
                                    R.string.static_find_follow_title,
                                    R.string.static_find_follow_body);
                        });
        findViewById(R.id.drawerLinkSuggestions)
                .setOnClickListener(
                        v -> {
                            drawerRoot.closeDrawer(GravityCompat.START);
                            StaticPageActivity.start(
                                    this,
                                    R.string.static_suggestions_title,
                                    R.string.static_suggestions_body);
                        });

        resetFeed();
    }

    @Override
    protected void onDestroy() {
        mainHandler.removeCallbacksAndMessages(null);
        if (profilePopup != null && profilePopup.isShowing()) {
            profilePopup.dismiss();
        }
        profilePopup = null;
        super.onDestroy();
    }

    private void toggleProfileMenu() {
        if (profilePopup != null && profilePopup.isShowing()) {
            profilePopup.dismiss();
            return;
        }

        View content = LayoutInflater.from(this).inflate(R.layout.layout_profile_menu, null);
        float density = getResources().getDisplayMetrics().density;
        int screenW = getResources().getDisplayMetrics().widthPixels;
        int margin = (int) (12 * density);
        int gap = (int) (6 * density);
        int w = (int) (screenW * 0.72f);

        PopupWindow popup =
                new PopupWindow(
                        content, w, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.setOutsideTouchable(true);
        popup.setElevation(24f);
        popup.setOnDismissListener(() -> profilePopup = null);

        ImageView anchor = findViewById(R.id.avatar);
        int[] loc = new int[2];
        anchor.getLocationOnScreen(loc);
        View decor = getWindow().getDecorView();
        int x = screenW - margin - w;
        int y = loc[1] + anchor.getHeight() + gap;
        popup.showAtLocation(decor, Gravity.NO_GRAVITY, x, y);
        profilePopup = popup;

        content.findViewById(R.id.profileMenuViewProfile)
                .setOnClickListener(
                        v -> {
                            popup.dismiss();
                            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                        });
        content.findViewById(R.id.profileRowWrite)
                .setOnClickListener(
                        v -> {
                            popup.dismiss();
                            startActivity(new Intent(HomeActivity.this, ComposePostActivity.class));
                        });
        content.findViewById(R.id.profileRowNotifications)
                .setOnClickListener(
                        v -> {
                            popup.dismiss();
                            StaticPageActivity.start(
                                    this,
                                    R.string.static_notifications_title,
                                    R.string.static_notifications_body);
                        });
        content.findViewById(R.id.profileRowSettings)
                .setOnClickListener(
                        v -> {
                            popup.dismiss();
                            StaticPageActivity.start(
                                    this,
                                    R.string.static_settings_title,
                                    R.string.static_settings_body);
                        });
        content.findViewById(R.id.profileRowHelp)
                .setOnClickListener(
                        v -> {
                            popup.dismiss();
                            StaticPageActivity.start(
                                    this, R.string.static_help_title, R.string.static_help_body);
                        });
        content.findViewById(R.id.profileRowMember)
                .setOnClickListener(
                        v -> {
                            popup.dismiss();
                            StaticPageActivity.start(
                                    this, R.string.static_member_title, R.string.static_member_body);
                        });
        content.findViewById(R.id.profileRowPartner)
                .setOnClickListener(
                        v -> {
                            popup.dismiss();
                            StaticPageActivity.start(
                                    this,
                                    R.string.static_partner_title,
                                    R.string.static_partner_body);
                        });
        content.findViewById(R.id.profileFooterLinks)
                .setOnClickListener(
                        v -> {
                            popup.dismiss();
                            StaticPageActivity.start(
                                    this, R.string.static_footer_title, R.string.static_footer_body);
                        });
        content.findViewById(R.id.profileSignOut)
                .setOnClickListener(
                        v -> {
                            popup.dismiss();
                            Intent i = new Intent(HomeActivity.this, LandingActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();
                        });
    }

    private void resetFeed() {
        pageIndex = 0;
        loading = true;
        adapter.clear();
        loadMoreProgress.setVisibility(View.VISIBLE);
        mainHandler.postDelayed(
                () -> {
                    List<FeedArticle> first = FeedDemoData.page(pageIndex, PAGE_SIZE, featuredTab);
                    pageIndex++;
                    adapter.addItems(first);
                    onPageLoaded();
                },
                200);
    }

    private void loadNextPage() {
        if (loading || pageIndex >= MAX_PAGES) {
            return;
        }
        loading = true;
        loadMoreProgress.setVisibility(View.VISIBLE);
        mainHandler.postDelayed(
                () -> {
                    List<FeedArticle> next = FeedDemoData.page(pageIndex, PAGE_SIZE, featuredTab);
                    pageIndex++;
                    adapter.addItems(next);
                    onPageLoaded();
                },
                450);
    }

    private void onPageLoaded() {
        loading = false;
        loadMoreProgress.setVisibility(View.GONE);
        recyclerView.post(
                () -> {
                    if (loading || pageIndex >= MAX_PAGES) {
                        return;
                    }
                    if (!layoutManager.canScrollVertically()) {
                        loadNextPage();
                    }
                });
    }
}
