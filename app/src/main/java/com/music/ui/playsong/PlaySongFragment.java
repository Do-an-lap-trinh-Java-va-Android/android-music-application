package com.music.ui.playsong;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
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
import com.music.ui.playsong.playback.MediaPlayBackService;

import org.apache.commons.lang3.time.DurationFormatUtils;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PlaySongFragment extends Fragment {
    @Nullable
    private FragmentPlaySongBinding binding;

    @SuppressWarnings("NotNullFieldNotInitialized")
    @NonNull
    private PlaySongFragmentArgs args;

    @SuppressWarnings("FieldCanBeLocal")
    private PlaySongViewModel viewModel;

    private MediaBrowserCompat mediaBrowser;

    private MediaControllerCompat mediaController;

    @Nullable
    private MediaPlayer mediaPlayer;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mediaPlayer = ((MediaPlayBackService.LocalBinder) service).getMediaPlayer();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mediaPlayer = null;
        }
    };

    private final MediaBrowserCompat.ConnectionCallback connectionCallbacks = new MediaBrowserCompat.ConnectionCallback() {
        @Override
        public void onConnected() {
            MediaSessionCompat.Token token = mediaBrowser.getSessionToken();
            mediaController = new MediaControllerCompat(requireContext(), token);
            MediaControllerCompat.setMediaController(requireActivity(), mediaController);
            mediaController.registerCallback(controllerCallback);
        }
    };

    private final MediaControllerCompat.Callback controllerCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            switch (state.getState()) {
                case PlaybackStateCompat.STATE_PAUSED:
                    binding.btnPlay.setImageResource(R.drawable.ic_outline_play_circle_light_64);
                    break;
                case PlaybackStateCompat.STATE_PLAYING:
                    binding.btnPlay.setImageResource(R.drawable.ic_outline_pause_circle_light_64);
                    break;
            }
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            binding.btnPlay.setOnClickListener(v -> {
                int playBackState = MediaControllerCompat.getMediaController(requireActivity()).getPlaybackState().getState();

                if (playBackState == PlaybackStateCompat.STATE_PLAYING) {
                    mediaController.getTransportControls().pause();
                    handler.removeCallbacks(runnable);
                } else {
                    mediaController.getTransportControls().play();
                    handler.post(runnable);
                }
            });

            binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int msec, boolean fromUser) {
                    binding.tvCurrentPosition.setText(DurationFormatUtils.formatDuration(
                            mediaPlayer.getCurrentPosition(), "mm:ss"
                    ));

                    if (fromUser) {
                        mediaPlayer.seekTo(msec);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) { }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) { }
            });

            binding.seekBar.setMax((int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
            binding.tvLengthOfSong.setText(DurationFormatUtils.formatDuration(
                    metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION),
                    "mm:ss"
            ));
        }
    };

    private final Handler handler = new Handler(Looper.myLooper());
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null) {
                binding.seekBar.setProgress(mediaPlayer.getCurrentPosition());
            }
            handler.postDelayed(this, 500);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        args = PlaySongFragmentArgs.fromBundle(requireArguments());

        mediaBrowser = new MediaBrowserCompat(requireActivity(),
                new ComponentName(requireActivity(), MediaPlayBackService.class),
                connectionCallbacks,
                null
        );

        Intent mediaPlayBackService = new Intent(requireContext(), MediaPlayBackService.class);
        requireContext().stopService(mediaPlayBackService);

        requireContext().bindService(
                new Intent(requireContext(), MediaPlayBackService.class),
                serviceConnection,
                Context.BIND_AUTO_CREATE
        );
    }

    @Override
    public void onStart() {
        super.onStart();

        requireActivity().findViewById(R.id.bottom_navigation_view).setVisibility(View.GONE);

        mediaBrowser.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.post(runnable);
    }

    @Override
    public void onStop() {
        super.onStop();

        requireActivity().findViewById(R.id.bottom_navigation_view).setVisibility(View.VISIBLE);

        if (MediaControllerCompat.getMediaController(requireActivity()) != null) {
            MediaControllerCompat.getMediaController(requireActivity()).unregisterCallback(controllerCallback);
        }

        handler.removeCallbacks(runnable);

        mediaBrowser.disconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        requireContext().unbindService(serviceConnection);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPlaySongBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(PlaySongViewModel.class);
        viewModel.getInfoOfSong(args.getSongUid());
        viewModel.getSongMutableLiveData().observe(getViewLifecycleOwner(), response -> {
            if (binding == null) return;

            switch (response.status) {
                case SUCCESS:
                    Song song = Objects.requireNonNull(response.data);
                    Glide.with(binding.ivThumbnail.getContext()).load(song.getThumbnail()).circleCrop().into(binding.ivThumbnail);
                    binding.tvSongName.setText(song.getName());
                    binding.tvSongArtists.setText(song.getArtistsNames());

                    Intent mediaPlayBackService = new Intent(requireContext(), MediaPlayBackService.class);
                    mediaPlayBackService.putExtra("song", song);
                    requireContext().startService(mediaPlayBackService);

                    mediaController.getTransportControls().playFromUri(Uri.parse(song.getMp3()), null);

                    binding.frmLoading.setVisibility(View.GONE);
                    break;
                case LOADING:
                    binding.frmLoading.setVisibility(View.VISIBLE);
                    break;
                case ERROR:
                    binding.frmLoading.setVisibility(View.GONE);
                    break;
            }
        });
    }
}