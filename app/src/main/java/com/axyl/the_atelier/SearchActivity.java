package com.axyl.the_atelier;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private static final int MAX_RESULTS = 80;
    private static final int DEBOUNCE_MS = 320;

    private final Handler debounceHandler = new Handler(Looper.getMainLooper());
    private Runnable pendingSearch;

    private EditText inputQuery;
    private TextView searchEmpty;
    private RecyclerView searchRecycler;
    private FeedAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.searchRoot),
                (v, insets) -> {
                    Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
                    return insets;
                });

        ImageButton btnBack = findViewById(R.id.btnBack);
        inputQuery = findViewById(R.id.inputQuery);
        searchEmpty = findViewById(R.id.searchEmpty);
        searchRecycler = findViewById(R.id.searchRecycler);

        adapter = new FeedAdapter(this);
        searchRecycler.setLayoutManager(new LinearLayoutManager(this));
        searchRecycler.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        inputQuery.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}

                    @Override
                    public void afterTextChanged(Editable s) {
                        scheduleSearch(s != null ? s.toString() : "");
                    }
                });

        inputQuery.setOnEditorActionListener(
                (v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        if (pendingSearch != null) {
                            debounceHandler.removeCallbacks(pendingSearch);
                        }
                        runSearch(inputQuery.getText() != null ? inputQuery.getText().toString() : "");
                        return true;
                    }
                    return false;
                });

        inputQuery.post(
                () -> {
                    inputQuery.requestFocus();
                    showKeyboard(inputQuery);
                });

        showPromptEmpty();
    }

    private void scheduleSearch(String query) {
        if (pendingSearch != null) {
            debounceHandler.removeCallbacks(pendingSearch);
        }
        pendingSearch = () -> runSearch(query);
        debounceHandler.postDelayed(pendingSearch, DEBOUNCE_MS);
    }

    private void runSearch(String query) {
        String trimmed = query.trim();
        if (trimmed.isEmpty()) {
            adapter.setItems(Collections.emptyList());
            showPromptEmpty();
            return;
        }

        List<FeedArticle> results = FeedDemoData.search(trimmed, MAX_RESULTS);
        adapter.setItems(results);
        if (results.isEmpty()) {
            searchRecycler.setVisibility(View.GONE);
            searchEmpty.setVisibility(View.VISIBLE);
            searchEmpty.setText(R.string.search_empty_no_results);
        } else {
            searchEmpty.setVisibility(View.GONE);
            searchRecycler.setVisibility(View.VISIBLE);
        }
    }

    private void showPromptEmpty() {
        searchRecycler.setVisibility(View.GONE);
        searchEmpty.setVisibility(View.VISIBLE);
        searchEmpty.setText(R.string.search_empty_prompt);
    }

    private void showKeyboard(EditText editText) {
        editText.postDelayed(
                () -> {
                    InputMethodManager imm =
                            (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                    }
                },
                100);
    }

    @Override
    protected void onDestroy() {
        if (pendingSearch != null) {
            debounceHandler.removeCallbacks(pendingSearch);
        }
        debounceHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
