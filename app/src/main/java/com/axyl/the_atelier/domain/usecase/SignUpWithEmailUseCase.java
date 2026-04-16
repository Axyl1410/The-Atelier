package com.axyl.the_atelier.domain.usecase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.axyl.the_atelier.data.remote.ApiResultCallback;
import com.axyl.the_atelier.data.repository.AuthRepository;
import com.axyl.the_atelier.domain.model.SignUpResult;

public final class SignUpWithEmailUseCase {
    private final AuthRepository authRepository;

    public SignUpWithEmailUseCase(@NonNull AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void execute(
            @NonNull String name,
            @NonNull String email,
            @NonNull String password,
            @Nullable String image,
            @Nullable String callbackUrl,
            boolean rememberMe,
            @NonNull ApiResultCallback<SignUpResult> callback
    ) {
        authRepository.signUpEmail(name, email, password, image, callbackUrl, rememberMe, callback);
    }
}

