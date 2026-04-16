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
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.axyl.the_atelier.data.remote.ApiError;
import com.axyl.the_atelier.data.remote.ApiResultCallback;
import com.axyl.the_atelier.data.remote.VolleyClient;
import com.axyl.the_atelier.data.repository.AuthRepository;
import com.axyl.the_atelier.data.session.EncryptedTokenStore;
import com.axyl.the_atelier.domain.model.SignUpResult;
import com.axyl.the_atelier.domain.usecase.SignUpWithEmailUseCase;
import com.google.android.material.button.MaterialButton;

public class SignUpActivity extends AppCompatActivity {
    private static final String BASE_URL = "https://the-atelier-backend.truonggiang-axyl.workers.dev";
    private SignUpWithEmailUseCase signUpWithEmailUseCase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(true);
        controller.setAppearanceLightNavigationBars(true);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signUpRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(v -> finish());

        TextView title = findViewById(R.id.title);
        String titleStr = getString(R.string.sign_up_title);
        SpannableString titleText = new SpannableString(titleStr);
        int last = titleStr.length() - 1;
        titleText.setSpan(
                new ForegroundColorSpan(getColor(R.color.landing_green)),
                last,
                last + 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setText(titleText);

        EditText nameInput = findViewById(R.id.nameInput);
        EditText emailInput = findViewById(R.id.emailInput);
        EditText passwordInput = findViewById(R.id.passwordInput);
        MaterialButton signUpButton = findViewById(R.id.signUpButton);
        TextView loginLink = findViewById(R.id.loginLink);

        EncryptedTokenStore tokenStore = new EncryptedTokenStore(this);
        VolleyClient client = new VolleyClient(this, BASE_URL, tokenStore);
        AuthRepository authRepository = new AuthRepository(client, tokenStore);
        signUpWithEmailUseCase = new SignUpWithEmailUseCase(authRepository);

        signUpButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString();
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Name, email and password are required", Toast.LENGTH_SHORT).show();
                return;
            }

            setSigningUp(signUpButton, true);
            signUpWithEmailUseCase.execute(
                    name,
                    email,
                    password,
                    null,
                    null,
                    true,
                    new ApiResultCallback<SignUpResult>() {
                        @Override
                        public void onSuccess(@androidx.annotation.NonNull SignUpResult data) {
                            setSigningUp(signUpButton, false);
                            showCheckMailDialog();
                        }

                        @Override
                        public void onError(@androidx.annotation.NonNull ApiError error) {
                            setSigningUp(signUpButton, false);
                            Toast.makeText(SignUpActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
            );
        });

        loginLink.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
            finish();
        });
    }

    private void setSigningUp(MaterialButton signUpButton, boolean isSigningUp) {
        signUpButton.setEnabled(!isSigningUp);
        signUpButton.setText(isSigningUp ? "Signing up..." : getString(R.string.sign_up_submit));
    }

    private void showCheckMailDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Kiểm tra email")
                .setMessage("Tài khoản đã tạo thành công. Vui lòng xác thực email trước khi đăng nhập.")
                .setCancelable(false)
                .setPositiveButton("Đã hiểu", (dialog, which) -> {
                    startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                    finish();
                })
                .show();
    }
}

