package com.music.activities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.music.adapters.SongAdapter;
import com.music.databinding.ActivityMainBinding;
import com.music.models.Song;
import com.music.repositories.SongRepository;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    @SuppressWarnings({"FieldCanBeLocal", "NotNullFieldNotInitialized"})
    @NonNull
    private ActivityMainBinding binding;

    @NonNull
    private final SongRepository songRepository = new SongRepository();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        List<Song> songs = new ArrayList<>();

        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        binding.rvRandomSong.setHasFixedSize(true);
        binding.rvRandomSong.setLayoutManager(linearLayoutManager);
        binding.rvRandomSong.setAdapter(new SongAdapter(songs));
        binding.rvRandomSong.addItemDecoration(new SongAdapter.SongItemDecoration());

        songRepository.getTopSongs().addOnSuccessListener(_songs -> {
            songs.addAll(_songs);
            binding.rvRandomSong.getAdapter().notifyDataSetChanged();
        });
    }
}