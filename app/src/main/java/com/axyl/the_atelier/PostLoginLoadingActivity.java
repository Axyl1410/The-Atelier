package com.axyl.the_atelier;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class PostLoginLoadingActivity extends AppCompatActivity {

    private static final long REVEAL_DURATION_MS = 5000L;
    /** Giữ nguyên chữ đầy đủ màu đen trước khi sang Home */
    private static final long HOLD_AFTER_REVEAL_MS = 450L;

    /** Trái gần màu nền kem, phải về đen — sóng reveal trái → phải */
    private static final int GRADIENT_START = 0xFFF7F4ED;
    private static final int GRADIENT_END = 0xFF000000;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private ValueAnimator textAnimator;
    private ObjectAnimator cursorBlink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_post_login_loading);

        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(true);
        controller.setAppearanceLightNavigationBars(true);

        View root = findViewById(R.id.postLoginRoot);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView textReveal = findViewById(R.id.textReveal);
        View cursor = findViewById(R.id.cursor);

        String full = getString(R.string.post_login_brand);
        int fullLen = full.length();
        textReveal.setText("");

        cursorBlink = ObjectAnimator.ofFloat(cursor, View.ALPHA, 1f, 0.12f);
        cursorBlink.setDuration(420);
        cursorBlink.setRepeatCount(ValueAnimator.INFINITE);
        cursorBlink.setRepeatMode(ValueAnimator.REVERSE);
        cursorBlink.setInterpolator(new LinearInterpolator());
        cursorBlink.start();

        textAnimator = ValueAnimator.ofFloat(0f, 1f);
        textAnimator.setDuration(REVEAL_DURATION_MS);
        textAnimator.setInterpolator(new DecelerateInterpolator(1.35f));
        textAnimator.addUpdateListener(
                animation -> {
                    float p = ((ValueAnimator) animation).getAnimatedFraction();
                    int len = Math.round(p * fullLen);
                    applyGradientReveal(textReveal, full, len);
                });
        textAnimator.addListener(
                new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        applySolidBlack(textReveal, full);
                        if (cursorBlink != null) {
                            cursorBlink.cancel();
                        }
                        cursor.animate().alpha(0f).setDuration(220).setInterpolator(new DecelerateInterpolator()).start();
                        mainHandler.postDelayed(
                                () -> goHomeWithFade(),
                                HOLD_AFTER_REVEAL_MS);
                    }
                });
        textAnimator.start();
    }

    /**
     * Từng ký tự xuất hiện thêm; trong phần đã lộ, gradient nhạt (trái) → đậm (phải) như sóng reveal.
     */
    private void applyGradientReveal(TextView textView, String full, int len) {
        if (len <= 0) {
            textView.setText("");
            return;
        }
        int n = full.length();
        len = Math.min(len, n);
        String visible = full.substring(0, len);
        SpannableString ss = new SpannableString(visible);
        for (int i = 0; i < len; i++) {
            float t = (i + 1f) / n;
            int charColor = ColorUtils.blendARGB(GRADIENT_START, GRADIENT_END, t);
            ss.setSpan(
                    new ForegroundColorSpan(charColor),
                    i,
                    i + 1,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textView.setText(ss);
    }

    private void applySolidBlack(TextView textView, String full) {
        textView.setText(full);
        textView.setTextColor(getColor(R.color.black));
    }

    private void goHomeWithFade() {
        startActivity(new Intent(this, HomeActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainHandler.removeCallbacksAndMessages(null);
        if (textAnimator != null) {
            textAnimator.cancel();
            textAnimator = null;
        }
        if (cursorBlink != null) {
            cursorBlink.cancel();
            cursorBlink = null;
        }
    }
}
