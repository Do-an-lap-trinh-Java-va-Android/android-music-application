package com.music.ui.playsong;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.music.R;
import com.music.databinding.FragmentPlaySongBinding;
import com.music.models.Song;

import org.apache.commons.lang3.time.DurationFormatUtils;

import java.io.IOException;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PlaySongFragment extends Fragment {
    @Nullable
    private FragmentPlaySongBinding binding;

    @SuppressWarnings({"NotNullFieldNotInitialized", "FieldCanBeLocal"})
    @NonNull
    private PlaySongFragmentArgs args;

    @SuppressWarnings("FieldCanBeLocal")
    private PlaySongViewModel viewModel;

    @NonNull
    private final MediaPlayer mediaPlayer = new MediaPlayer();

    @NonNull
    private final Handler handler = new Handler(Looper.myLooper());

    private NotificationManager notificationManager;

    private Song song;

    @NonNull
    final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("actionName");

            if (MusicPlayerNotification.ACTION_PLAY.equals(action)) {
                binding.btnPlay.performClick();
            }
        }
    };

    @Nullable
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            binding.seekBar.setProgress(mediaPlayer.getCurrentPosition());
            handler.postDelayed(this, 500);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        args = PlaySongFragmentArgs.fromBundle(requireArguments());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
        requireActivity().registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACKS"));
        Intent intent =
                new Intent(requireActivity().getBaseContext(), OnClearFromRecentService.class);

        requireActivity().startService(intent);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(MusicPlayerNotification.CHANNEL_ID,
                "Trình phát nhạc", NotificationManager.IMPORTANCE_HIGH);

        notificationManager = requireActivity().getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPlaySongBinding.inflate(inflater, container, false);

        setUpMediaPlayer();
        setUpSeekBar();
        setUpBtnPlay();

        return binding.getRoot();
    }

    private void setUpMediaPlayer() {
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );

        mediaPlayer.setOnBufferingUpdateListener((mediaPlayer, percent) -> {
            binding.seekBar.setSecondaryProgress(percent);
        });

        mediaPlayer.setOnPreparedListener(mediaPlayer -> {
            System.out.println(song);
            binding.seekBar.setMax(song.getDuration());
            binding.tvLengthOfSong.setText(DurationFormatUtils.formatDuration(song.getDuration(), "mm:ss",
                    true));
            binding.btnPlay.performClick();
        });
    }

    private void setUpSeekBar() {
        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                binding.tvCurrentPosition.setText(DurationFormatUtils.formatDuration(mediaPlayer.getCurrentPosition(),
                        "mm:ss", true));
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }

    private void setUpBtnPlay() {
        binding.btnPlay.setOnClickListener(v -> {

            if (mediaPlayer.isPlaying()) {
                MusicPlayerNotification.createNotification(requireActivity(), song,
                        R.drawable.ic_outline_play_circle_light_64);
                mediaPlayer.pause();
                binding.btnPlay.setImageResource(R.drawable.ic_outline_play_circle_light_64);
                handler.removeCallbacks(runnable);
            } else {
                MusicPlayerNotification.createNotification(requireActivity(), song,
                        R.drawable.ic_outline_pause_circle_light_64);
                mediaPlayer.start();
                binding.btnPlay.setImageResource(R.drawable.ic_outline_pause_circle_light_64);
                handler.postDelayed(runnable, 0);
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(PlaySongViewModel.class);

        viewModel.getInfoOfSong(args.getSongUid());

        viewModel.getSongMutableLiveData().observe(getViewLifecycleOwner(), response -> {
            switch (response.status) {
                case SUCCESS:
                    this.song = response.data;
                    Song song = Objects.requireNonNull(response.data);
                    Glide.with(binding.ivThumbnail.getContext()).load(song.getThumbnail()).circleCrop().into(binding.ivThumbnail);
                    binding.tvSongName.setText(song.getName());
                    binding.tvSongArtists.setText(song.getArtistsNames());
                    try {
                        mediaPlayer.setDataSource(requireActivity().getApplicationContext(), Uri.parse(song.getMp3()));
                        mediaPlayer.prepareAsync();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    binding.frmLoading.setVisibility(View.GONE);
                    break;
                case LOADING:
                    binding.frmLoading.setVisibility(View.VISIBLE);
                    break;
                case ERROR:
                    Toast.makeText(requireActivity(), "Tải bài hát thất bại", Toast.LENGTH_SHORT).show();
                    binding.frmLoading.setVisibility(View.GONE);
                    break;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        requireActivity().findViewById(R.id.bottom_navigation_view).setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 0);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onStop() {
        super.onStop();
        requireActivity().findViewById(R.id.bottom_navigation_view).setVisibility(View.VISIBLE);
        mediaPlayer.setOnBufferingUpdateListener(null);
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;

        if (notificationManager != null) {
            notificationManager.cancelAll();
        }

        mediaPlayer.reset();
        mediaPlayer.release();

        getContext().unregisterReceiver(broadcastReceiver);
    }
}