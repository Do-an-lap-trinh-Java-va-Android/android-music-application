package com.music.ui.chart;

import androidx.annotation.NonNull;
import androidx.hilt.Assisted;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.Query;
import com.music.repositories.SongRepository;

public class ChartViewModel extends ViewModel {
    @NonNull
    private final SongRepository songRepository;

    @ViewModelInject
    public ChartViewModel(@Assisted SavedStateHandle savedStateHandle,
                          @NonNull SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    @NonNull
    public Query getQueryFetchTopSongs() {
        return songRepository.getQueryFetchTopSongs();
    }
}