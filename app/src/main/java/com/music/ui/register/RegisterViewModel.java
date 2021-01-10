package com.music.ui.register;

import androidx.annotation.NonNull;
import androidx.hilt.Assisted;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;
import com.music.network.Resource;
import com.music.repositories.UserRepository;

public class RegisterViewModel extends ViewModel {
    @NonNull
    private final UserRepository userRepository;

    @ViewModelInject
    public RegisterViewModel(@Assisted SavedStateHandle savedStateHandle, @NonNull UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<Resource<FirebaseUser>> login(@NonNull String email, @NonNull String password) {
        return userRepository.createUserWithEmailAndPassword(email, password);
    }

    public LiveData<Resource<Void>> updateProfile(@NonNull FirebaseUser user, @NonNull String displayName) {
        return userRepository.updateProfileUser(user, displayName);
    }
}
