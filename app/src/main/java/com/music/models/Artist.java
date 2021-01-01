package com.music.models;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.DocumentReference;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Artist {
    @NonNull
    @DocumentId
    private String id = StringUtils.EMPTY;

    @NonNull
    private String name = StringUtils.EMPTY;

    @NonNull
    private List<DocumentReference> songs = new ArrayList<>();
}
