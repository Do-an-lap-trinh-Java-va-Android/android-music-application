package com.music.ui.chart;

import androidx.annotation.NonNull;
import androidx.hilt.Assisted;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.music.models.Song;
import com.music.network.Resource;
import com.music.repositories.SongRepository;

import java.util.List;

public class ChartViewModel extends ViewModel {
    private final int limit = 10;

    @NonNull
    private SongRepository songRepository;

    @NonNull
    private MutableLiveData<Resource<List<Song>>> topSongList = new MutableLiveData<>();

    @ViewModelInject
    public ChartViewModel(@Assisted SavedStateHandle savedStateHandle,
                          @NonNull SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    @NonNull
    public MutableLiveData<Resource<List<Song>>> getTopSongList() {
        return topSongList;
    }

    public void fetchTopSongList() {
        topSongList = (MutableLiveData<Resource<List<Song>>>) songRepository.getTopSongs(limit);
    }
}