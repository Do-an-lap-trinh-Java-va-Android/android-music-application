package com.music.models;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.DocumentReference;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Collection {
    /**
     * ID bộ sưu tập
     */
    @DocumentId
    private String id;

    /**
     * Tên bộ sưu tập
     */
    @NonNull
    private String name;

    /**
     * Danh sách albums của bộ sưu tập
     */
    @NonNull
    private List<DocumentReference> albums;
}
