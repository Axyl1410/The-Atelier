package com.axyl.the_atelier;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class StaticPageActivity extends AppCompatActivity {

    public static final String EXTRA_TITLE_RES = "extra_title_res";
    public static final String EXTRA_BODY_RES = "extra_body_res";

    public static void start(Context context, int titleRes, int bodyRes) {
        Intent i = new Intent(context, StaticPageActivity.class);
        i.putExtra(EXTRA_TITLE_RES, titleRes);
        i.putExtra(EXTRA_BODY_RES, bodyRes);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_static_page);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.staticPageRoot),
                (v, insets) -> {
                    Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
                    return insets;
                });

        int titleRes = getIntent().getIntExtra(EXTRA_TITLE_RES, 0);
        int bodyRes = getIntent().getIntExtra(EXTRA_BODY_RES, 0);

        TextView textToolbarTitle = findViewById(R.id.textToolbarTitle);
        TextView textBody = findViewById(R.id.textBody);
        ImageButton btnBack = findViewById(R.id.btnBack);

        if (titleRes != 0) {
            textToolbarTitle.setText(titleRes);
        }
        if (bodyRes != 0) {
            textBody.setText(bodyRes);
        }

        btnBack.setOnClickListener(v -> finish());
    }
}
