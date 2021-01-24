package com.music.repositories;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.music.R;
import com.music.network.Resource;

import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class UserRepository {
    @NonNull
    private final Context application;

    @NonNull
    private final FirebaseAuth firebaseAuth;

    @Inject
    public UserRepository(@NonNull @ApplicationContext Context application,
                          @NonNull FirebaseAuth firebaseAuth) {
        this.application = application;
        this.firebaseAuth = firebaseAuth;
    }

    /**
     * Tạo tài khoản Firebase
     *
     * @param email    Địa chỉ email
     * @param password Mật khẩu
     */
    @NonNull
    public LiveData<Resource<FirebaseUser>> createUserWithEmailAndPassword(@NonNull String email,
                                                                           @NonNull String password) {
        final MutableLiveData<Resource<FirebaseUser>> resultMutableLiveData =
                new MutableLiveData<>(Resource.loading("Đang đăng ký"));

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    resultMutableLiveData.setValue(Resource.success(authResult.getUser()));
                })
                .addOnFailureListener(e -> {
                    if (e instanceof FirebaseAuthUserCollisionException) {
                        String exceptionMessage = application.getString(R.string.the_email_address_is_already_in_use_by_another_account);
                        resultMutableLiveData.setValue(Resource.error(exceptionMessage, null));
                    } else {
                        resultMutableLiveData.setValue(Resource.error(Objects.requireNonNull(e.getMessage()), null));
                    }
                });

        return resultMutableLiveData;
    }

    /**
     * Cập nhật thông tin tài khoản
     *
     * @param firebaseUser Tài khoản firebase
     * @param displayName  Tên hiển thị
     */
    @NonNull
    public LiveData<Resource<Void>> updateProfileUser(@NonNull FirebaseUser firebaseUser,
                                                      @NonNull String displayName) {
        final MutableLiveData<Resource<Void>> resultMutableLiveData =
                new MutableLiveData<>(Resource.loading("Đang cập nhật thông tin"));

        UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build();

        firebaseUser.updateProfile(profile).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                resultMutableLiveData.setValue(Resource.success(null));
            } else {
                if (task.getException() != null && task.getException().getMessage() != null) {
                    resultMutableLiveData.setValue(Resource.error(task.getException().getMessage(), null));
                } else {
                    resultMutableLiveData.setValue(Resource.error("Đã có lỗi không rõ xảy ra", null));
                }
            }
        });

        return resultMutableLiveData;
    }
}
