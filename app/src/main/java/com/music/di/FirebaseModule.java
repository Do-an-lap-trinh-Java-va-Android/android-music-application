package com.music.di;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class FirebaseModule {
    @Provides
    @Singleton
    public FirebaseFirestore provideFirebaseFirestore() {
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)   // Không cho phép Firebase truy cập offline
                .build();

        FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
        fireStore.setFirestoreSettings(settings);

        return fireStore;
    }

    @Provides
    @Singleton
    public FirebaseAuth provideFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }
}
