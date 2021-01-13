package com.music.ui.playsong;

import android.annotation.SuppressLint;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.music.R;
import com.music.databinding.FragmentPlaySongBinding;
import com.music.models.Song;

import java.net.URI;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PlaySongFragment extends Fragment {
    @SuppressWarnings("FieldCanBeLocal")
    private PlaySongViewModel viewModel;

    @SuppressWarnings({"NotNullFieldNotInitialized", "FieldCanBeLocal"})
    @NonNull
    private PlaySongFragmentArgs args;

    @Nullable
    private FragmentPlaySongBinding binding;

    @NonNull
    public FragmentPlaySongBinding getBinding() {
        return Objects.requireNonNull(binding);
    }

    @NonNull
    private final Handler handler = new Handler();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        args = PlaySongFragmentArgs.fromBundle(requireArguments());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPlaySongBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        requireActivity().findViewById(R.id.bottom_navigation_view).setVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        super.onStop();
        requireActivity().findViewById(R.id.bottom_navigation_view).setVisibility(View.VISIBLE);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(PlaySongViewModel.class);

        viewModel.getInfoOfSong(args.getSongUid());

        viewModel.getSongMutableLiveData().observe(getViewLifecycleOwner(), response -> {
            switch (response.status) {
                case SUCCESS:
                    Song song = Objects.requireNonNull(response.data);
                    onSongInfoLoaded(song);
                    break;
                case LOADING:
                    break;
                case ERROR:
                    break;
            }
        });
    }

    private void onSongInfoLoaded(@NonNull Song song) {
        final FragmentPlaySongBinding binding = getBinding();

        Glide.with(binding.ivThumbnail.getContext()).load(song.getThumbnail()).into(binding.ivThumbnail);
        binding.tvSongName.setText(song.getName());
        binding.tvSongArtists.setText(TextUtils.join(", ", song.getArtists()));

        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity(), Uri.parse(song.getMp3()));

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                binding.seekBar.setProgress(mediaPlayer.getCurrentPosition());
                handler.postDelayed(this, 500);
            }
        };

        binding.btnPlay.setOnClickListener(view -> {
            if (!mediaPlayer.isPlaying()) {
                binding.btnPlay.setImageResource(R.drawable.ic_outline_pause_circle_light_64);
                mediaPlayer.start();
                binding.seekBar.setMax(mediaPlayer.getDuration());
                handler.postDelayed(runnable, 0);
            } else {
                binding.btnPlay.setImageResource(R.drawable.ic_outline_play_circle_light_64);
                mediaPlayer.pause();
                handler.removeCallbacks(runnable);
            }
        });

        mediaPlayer.setOnCompletionListener(mp -> mp.seekTo(0));

        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }

                binding.currentPosition.setText(formatDuration(mediaPlayer.getCurrentPosition()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        binding.tvLengthOfSong.setText(formatDuration(mediaPlayer.getDuration()));
    }

    @NonNull
    @SuppressLint("DefaultLocale")
    private String formatDuration(long duration) {
        long minutues = TimeUnit.MILLISECONDS.toMinutes(duration);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(minutues);

        return String.format("%02d:%02d", minutues, seconds);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}