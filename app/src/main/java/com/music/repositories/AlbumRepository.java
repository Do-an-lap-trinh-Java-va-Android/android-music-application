package com.music.repositories;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class AlbumRepository {
    private final CollectionReference collection;

    public AlbumRepository() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        collection = database.collection("collections");
    }

    public Task<QuerySnapshot> getRecommendAlbums() {
        return collection.whereEqualTo("name", "Album Slider").get();
    }
}
