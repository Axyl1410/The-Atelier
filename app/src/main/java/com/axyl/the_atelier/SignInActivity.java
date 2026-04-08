package com.axyl.the_atelier;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.button.MaterialButton;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);

        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(true);
        controller.setAppearanceLightNavigationBars(true);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signInRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(v -> finish());

        TextView title = findViewById(R.id.title);
        String titleStr = getString(R.string.sign_in_title);
        SpannableString titleText = new SpannableString(titleStr);
        int last = titleStr.length() - 1;
        titleText.setSpan(
                new ForegroundColorSpan(getColor(R.color.landing_green)),
                last,
                last + 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setText(titleText);

        EditText emailInput = findViewById(R.id.emailInput);
        EditText passwordInput = findViewById(R.id.passwordInput);

        MaterialButton signInButton = findViewById(R.id.signInButton);
        MaterialButton googleButton = findViewById(R.id.googleButton);
        MaterialButton appleButton = findViewById(R.id.appleButton);

        TextView forgotPassword = findViewById(R.id.forgotPassword);
        TextView createAccount = findViewById(R.id.createAccount);

        View.OnClickListener noop = v -> {};

        signInButton.setOnClickListener(
                v -> {
                    startActivity(new Intent(SignInActivity.this, PostLoginLoadingActivity.class));
                    finish();
                });
        googleButton.setOnClickListener(noop);
        appleButton.setOnClickListener(noop);
        forgotPassword.setOnClickListener(noop);
        createAccount.setOnClickListener(
                v -> startActivity(new Intent(SignInActivity.this, JoinActivity.class)));

        emailInput.setText("");
        passwordInput.setText("");
    }
}
