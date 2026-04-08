package com.axyl.the_atelier;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class ComposePostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_compose_post);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.composeRoot),
                (v, insets) -> {
                    Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
                    return insets;
                });

        ImageButton btnBack = findViewById(R.id.btnBack);
        MaterialButton btnPublish = findViewById(R.id.btnPublish);
        EditText inputBody = findViewById(R.id.inputBody);

        btnBack.setOnClickListener(v -> finish());

        btnPublish.setOnClickListener(
                v -> {
                    String body = inputBody.getText() != null ? inputBody.getText().toString().trim() : "";
                    if (TextUtils.isEmpty(body)) {
                        Toast.makeText(
                                        this,
                                        R.string.compose_error_empty_body,
                                        Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }
                    Toast.makeText(this, R.string.compose_published_toast, Toast.LENGTH_SHORT).show();
                    finish();
                });
    }
}
