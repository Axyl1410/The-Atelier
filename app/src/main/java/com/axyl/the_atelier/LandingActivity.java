package com.axyl.the_atelier;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.axyl.the_atelier.data.session.EncryptedTokenStore;

public class LandingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EncryptedTokenStore tokenStore = new EncryptedTokenStore(this);
        String token = tokenStore.getAccessToken();
        if (token == null || token.trim().isEmpty()) {
            startActivity(new Intent(this, SignInActivity.class));
        } else {
            startActivity(new Intent(this, HomeActivity.class));
        }
        finish();
    }
}
