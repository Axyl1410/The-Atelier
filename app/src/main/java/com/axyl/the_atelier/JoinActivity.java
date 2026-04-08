package com.axyl.the_atelier;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.button.MaterialButton;

public class JoinActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_join);

        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(true);
        controller.setAppearanceLightNavigationBars(true);

        View root = findViewById(R.id.joinRoot);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v -> finish());

        MaterialButton btnGoogle = findViewById(R.id.btnGoogle);
        MaterialButton btnFacebook = findViewById(R.id.btnFacebook);
        MaterialButton btnEmail = findViewById(R.id.btnEmail);
        View.OnClickListener noop = v -> { };
        btnGoogle.setOnClickListener(noop);
        btnFacebook.setOnClickListener(noop);
        btnEmail.setOnClickListener(noop);

        TextView loginPrompt = findViewById(R.id.textLoginPrompt);
        bindLoginPrompt(loginPrompt);

        TextView disclaimer = findViewById(R.id.textDisclaimer);
        bindDisclaimer(disclaimer);
    }

    private void bindLoginPrompt(TextView loginPrompt) {
        String prefix = getString(R.string.join_login_plain);
        String action = getString(R.string.join_login_action);
        SpannableString ss = new SpannableString(prefix + action);
        int start = prefix.length();
        int end = start + action.length();
        int linkColor = getColor(R.color.black);
        ss.setSpan(
                new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        startActivity(new Intent(JoinActivity.this, SignInActivity.class));
                    }

                    @Override
                    public void updateDrawState(@NonNull TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(true);
                        ds.setColor(linkColor);
                    }
                },
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        loginPrompt.setText(ss);
        loginPrompt.setMovementMethod(LinkMovementMethod.getInstance());
        loginPrompt.setHighlightColor(Color.TRANSPARENT);
    }

    private void bindDisclaimer(TextView disclaimer) {
        String full = getString(R.string.join_disclaimer);
        String terms = getString(R.string.join_disclaimer_terms);
        String privacy = getString(R.string.join_disclaimer_privacy);
        SpannableString ss = new SpannableString(full);
        int termsStart = full.indexOf(terms);
        int privacyStart = full.indexOf(privacy);
        int linkColor = getColor(R.color.join_disclaimer_gray);

        if (termsStart >= 0) {
            int termsEnd = termsStart + terms.length();
            ss.setSpan(
                    disclaimerLink(linkColor, v -> { }),
                    termsStart,
                    termsEnd,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (privacyStart >= 0) {
            int privacyEnd = privacyStart + privacy.length();
            ss.setSpan(
                    disclaimerLink(linkColor, v -> { }),
                    privacyStart,
                    privacyEnd,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        disclaimer.setText(ss);
        disclaimer.setMovementMethod(LinkMovementMethod.getInstance());
        disclaimer.setHighlightColor(Color.TRANSPARENT);
    }

    private ClickableSpan disclaimerLink(int color, View.OnClickListener onClick) {
        return new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                onClick.onClick(widget);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
                ds.setColor(color);
            }
        };
    }
}
