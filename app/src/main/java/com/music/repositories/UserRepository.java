package com.music.repositories;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.music.R;
import com.music.exceptions.NotLoginException;
import com.music.models.CustomUser;
import com.music.models.Song;
import com.music.network.Resource;

import java.util.HashMap;
import java.util.Map;
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

    @NonNull
    private final FirebaseFirestore db;

    @Inject
    public UserRepository(@NonNull @ApplicationContext Context application,
                          @NonNull FirebaseAuth firebaseAuth,
                          @NonNull FirebaseFirestore firebaseFirestore) {
        this.application = application;
        this.firebaseAuth = firebaseAuth;
        this.db = firebaseFirestore;
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

    /**
     * Cập nhật lịch sử nghe nhạc
     *
     * @param songId Mã bài hát
     */
    public void updateHistory(@NonNull String songId) {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user == null) {
            throw new NotLoginException();
        }

        /*
            HACK: Hiện tại cần phải lấy lịch sử nghe nhạc cũ rồi thêm vào bài hát đang nghe mới cập nhật lên,
            tốn 2 truy vấn chỉ để cập nhật lịch sử nghe nhạc
         */
        db.collection("users").document(user.getUid()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        final CustomUser customUser = task.getResult().toObject(CustomUser.class);

                        if (customUser != null) {
                            Map<String, Object> historyDetail = new HashMap<>();
                            historyDetail.put("song", db.collection("songs").document(songId));
                            historyDetail.put("listened_at", Timestamp.now().getSeconds());

                            customUser.getSongs().put(songId, historyDetail);

                            db.collection("users")
                                    .document(user.getUid())
                                    .set(customUser, SetOptions.merge());
                        }
                    }
                });
    }

    /**
     * @see UserRepository#updateHistory(String)
     */
    public void updateHistory(@NonNull Song song) {
        updateHistory(song.getId());
    }
}
