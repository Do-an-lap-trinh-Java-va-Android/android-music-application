package com.music.repositories;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class AlbumRepository {
    private final CollectionReference mCollection;

    public AlbumRepository() {
        FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
        mCollection = mDatabase.collection("albums");
    }

    public Task<QuerySnapshot> getRecommendAlbums() {
        return mCollection.whereEqualTo("recommend", true).get();
    }
}
