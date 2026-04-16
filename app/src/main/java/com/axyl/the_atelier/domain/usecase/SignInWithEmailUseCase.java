package com.axyl.the_atelier.domain.usecase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.axyl.the_atelier.data.remote.ApiResultCallback;
import com.axyl.the_atelier.data.repository.AuthRepository;
import com.axyl.the_atelier.domain.model.SignInResult;

public final class SignInWithEmailUseCase {
    private final AuthRepository authRepository;

    public SignInWithEmailUseCase(@NonNull AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void execute(
            @NonNull String email,
            @NonNull String password,
            boolean rememberMe,
            @Nullable String callbackUrl,
            @NonNull ApiResultCallback<SignInResult> callback
    ) {
        authRepository.signInEmail(email, password, rememberMe, callbackUrl, callback);
    }
}

