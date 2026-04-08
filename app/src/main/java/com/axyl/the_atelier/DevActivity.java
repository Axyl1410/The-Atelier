package com.axyl.the_atelier;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.axyl.the_atelier.data.remote.ApiError;
import com.axyl.the_atelier.data.remote.VolleyClient;
import com.axyl.the_atelier.data.repository.DummyRepository;
import com.axyl.the_atelier.domain.model.DummyRequest;
import com.axyl.the_atelier.domain.usecase.PostDummyUseCase;

import org.json.JSONObject;

public class DevActivity extends AppCompatActivity {
    private static final String BASE_URL = "";

    private PostDummyUseCase postDummyUseCase;

    private EditText editSlug;
    private EditText editName;
    private Button btnPost;
    private Button btnGoSignIn;
    private ProgressBar progress;
    private TextView textResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dev);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        VolleyClient client = new VolleyClient(this, BASE_URL);
        DummyRepository repository = new DummyRepository(client);
        postDummyUseCase = new PostDummyUseCase(repository);

        editSlug = findViewById(R.id.editSlug);
        editName = findViewById(R.id.editName);
        btnPost = findViewById(R.id.btnPost);
        btnGoSignIn = findViewById(R.id.btnGoSignIn);
        progress = findViewById(R.id.progress);
        textResult = findViewById(R.id.textResult);

        editSlug.setText("slug");
        editName.setText("string");

        btnPost.setOnClickListener(v -> postDummy());
        btnGoSignIn.setOnClickListener(v -> startActivity(new Intent(this, SignInActivity.class)));
    }

    private void postDummy() {
        String slug = editSlug.getText().toString().trim();
        String name = editName.getText().toString().trim();

        if (slug.isEmpty()) {
            textResult.setText("Missing slug");
            return;
        }
        if (name.isEmpty()) {
            textResult.setText("Missing name");
            return;
        }

        setLoading(true);
        textResult.setText("Sending...");
        postDummyUseCase.execute(
                new DummyRequest(slug, name),
                new com.axyl.the_atelier.data.remote.ApiResultCallback<JSONObject>() {
                    @Override
                    public void onSuccess(JSONObject data) {
                        setLoading(false);
                        textResult.setText("200 OK\n\n" + data.toString());
                    }

                    @Override
                    public void onError(ApiError error) {
                        setLoading(false);
                        textResult.setText(error.toString());
                    }
                }
        );
    }

    private void setLoading(boolean loading) {
        progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnPost.setEnabled(!loading);
    }
}
