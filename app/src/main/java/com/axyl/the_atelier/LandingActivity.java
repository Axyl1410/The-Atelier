package com.axyl.the_atelier;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.button.MaterialButton;

public class LandingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_landing);

        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(true);
        controller.setAppearanceLightNavigationBars(true);

        View root = findViewById(R.id.landingRoot);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MaterialButton btnHeaderStart = findViewById(R.id.btnHeaderStart);
        MaterialButton btnStartReading = findViewById(R.id.btnStartReading);

        View.OnClickListener goJoin =
                v -> startActivity(new Intent(this, JoinActivity.class));
        btnHeaderStart.setOnClickListener(goJoin);
        btnStartReading.setOnClickListener(goJoin);

        View.OnClickListener noop = v -> { };
        findViewById(R.id.linkAbout).setOnClickListener(noop);
        findViewById(R.id.linkHelp).setOnClickListener(noop);
        findViewById(R.id.linkTerms).setOnClickListener(noop);
        findViewById(R.id.linkPrivacy).setOnClickListener(noop);
    }
}
