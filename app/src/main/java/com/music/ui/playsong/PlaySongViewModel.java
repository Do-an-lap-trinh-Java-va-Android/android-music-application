package com.music.ui.playsong;

import androidx.annotation.NonNull;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.music.models.Song;
import com.music.network.Resource;
import com.music.repositories.SongRepository;

public class PlaySongViewModel extends ViewModel {
    @NonNull
    private final SongRepository songRepository;

    @NonNull
    private MutableLiveData<Resource<Song>> songMutableLiveData = new MutableLiveData<>();

    @ViewModelInject
    public PlaySongViewModel(@NonNull SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    public void getInfoOfSong(@NonNull String songId) {
        songMutableLiveData = (MutableLiveData<Resource<Song>>) songRepository.getInfoOfSong(songId);
    }

    @NonNull
    public MutableLiveData<Resource<Song>> getSongMutableLiveData() {
        return songMutableLiveData;
    }
}