package com.music.ui.playsong;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
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
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.Fragment;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.music.R;
import com.music.databinding.FragmentPlaySongBinding;
import com.music.models.Song;
import com.music.ui.playsong.playback.MediaPlayBackService;
import com.music.utils.UiModeUtils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PlaySongFragment extends Fragment {
    @Nullable
    private FragmentPlaySongBinding binding;

    @NonNull
    public FragmentPlaySongBinding getBinding() {
        return Objects.requireNonNull(binding);
    }

    @SuppressWarnings("NotNullFieldNotInitialized")
    @NonNull
    private PlaySongFragmentArgs args;

    @Nullable
    private MediaBrowserCompat mediaBrowser;

    @Nullable
    private MediaControllerCompat mediaController;

    @Nullable
    private MediaPlayer mediaPlayer;

    @NonNull
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

    @NonNull
    private final MediaBrowserCompat.ConnectionCallback connectionCallbacks = new MediaBrowserCompat.ConnectionCallback() {
        @Override
        public void onConnected() {
            MediaSessionCompat.Token token = mediaBrowser.getSessionToken();
            mediaController = new MediaControllerCompat(requireContext(), token);
            MediaControllerCompat.setMediaController(requireActivity(), mediaController);
            mediaController.registerCallback(controllerCallback);
        }
    };

    @NonNull
    private final MediaControllerCompat.Callback controllerCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onSessionReady() {
            final Song playNowSong = args.getPlayNowSong();

            final List<Song> playList = Arrays.stream(
                    ArrayUtils.add(args.getPlayList(), playNowSong)
            ).distinct().collect(Collectors.toList());

            // Khởi động trình phát nhạc
            requireContext().startService(new Intent(requireContext(), MediaPlayBackService.class));

            // Thêm các bài hát vào hàng chờ
            for (Song song : playList) {
                mediaController.addQueueItem(new MediaDescriptionCompat.Builder()
                        .setMediaId(song.getId())
                        .setTitle(song.getName())
                        .setSubtitle(song.getArtistsNames())
                        .setDescription(song.getFormatListens() + " lượt nghe")
                        .setMediaUri(Uri.parse(song.getMp3()))
                        .setIconUri(Uri.parse(song.getThumbnail()))
                        .build()
                );
            }

            // Phát bài hát
            mediaController.getTransportControls().playFromMediaId(playNowSong.getId(), null);
        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat playbackStateCompat) {
            handleUpdateImageSourceBtnTogglePlayPause(playbackStateCompat);
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            updateUI(metadata.getDescription());

            binding.btnTogglePlayPause.setOnClickListener(v -> {
                int playBackState = MediaControllerCompat.getMediaController(requireActivity()).getPlaybackState().getState();

                if (playBackState == PlaybackStateCompat.STATE_PLAYING) {
                    mediaController.getTransportControls().pause();
                    handler.removeCallbacks(runnable);
                } else {
                    mediaController.getTransportControls().play();
                    handler.postDelayed(runnable, 0);
                }
            });

            binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int msec, boolean fromUser) {
                    try {
                        binding.tvCurrentPosition.setText(DurationFormatUtils.formatDuration(
                                mediaPlayer.getCurrentPosition(), "mm:ss"
                        ));
                    } catch (IllegalStateException ignored) {

                    }

                    if (fromUser) {
                        mediaController.getTransportControls().seekTo(msec);
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

    @NonNull
    private final Handler handler = new Handler(Looper.myLooper());

    @NonNull
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    binding.seekBar.setProgress(mediaPlayer.getCurrentPosition());
                }
            } catch (IllegalStateException ignored) {

            }

            handler.postDelayed(this, 100);
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPlaySongBinding.inflate(inflater, container, false);

        args = PlaySongFragmentArgs.fromBundle(requireArguments());

        mediaBrowser = new MediaBrowserCompat(requireActivity(),
                new ComponentName(requireActivity(), MediaPlayBackService.class),
                connectionCallbacks,
                null
        );

        requireContext().bindService(
                new Intent(requireContext(), MediaPlayBackService.class),
                serviceConnection,
                Context.BIND_AUTO_CREATE
        );

        binding.btnSkipToNext.setOnClickListener(view -> {
            if (mediaController != null && mediaController.getTransportControls() != null) {
                mediaController.getTransportControls().skipToNext();
            }
        });

        binding.btnSkipToPrevious.setOnClickListener(vieww -> {
            if (mediaController != null && mediaController.getTransportControls() != null) {
                mediaController.getTransportControls().skipToPrevious();
            }
        });

        updateUI(args.getPlayNowSong());

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        requireActivity().findViewById(R.id.bottom_navigation_view).setVisibility(View.GONE);

        if (mediaBrowser != null) {
            mediaBrowser.connect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mediaController != null) {
            handleUpdateImageSourceBtnTogglePlayPause(mediaController);
        }

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

        if (mediaBrowser != null) {
            mediaBrowser.disconnect();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void handleUpdateImageSourceBtnTogglePlayPause(@NonNull MediaControllerCompat mediaController) {
        handleUpdateImageSourceBtnTogglePlayPause(mediaController.getPlaybackState());
    }

    @SuppressWarnings("DuplicateBranchesInSwitch")
    private void handleUpdateImageSourceBtnTogglePlayPause(@NonNull PlaybackStateCompat playbackStateCompat) {
        switch (playbackStateCompat.getState()) {
            case PlaybackStateCompat.STATE_BUFFERING:
                getBinding().prbBuffering.setVisibility(View.VISIBLE);
                break;
            case PlaybackStateCompat.STATE_CONNECTING:
                getBinding().prbBuffering.setVisibility(View.VISIBLE);
                break;
            case PlaybackStateCompat.STATE_ERROR:
                break;
            case PlaybackStateCompat.STATE_FAST_FORWARDING:
                break;
            case PlaybackStateCompat.STATE_NONE:
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                getBinding().prbBuffering.setVisibility(View.GONE);
                getBinding().btnTogglePlayPause.setVisibility(View.VISIBLE);
                getBinding().btnTogglePlayPause.setImageResource(R.drawable.ic_round_play_circle_64);
                break;
            case PlaybackStateCompat.STATE_PLAYING:
                getBinding().prbBuffering.setVisibility(View.GONE);
                getBinding().btnTogglePlayPause.setVisibility(View.VISIBLE);
                getBinding().btnTogglePlayPause.setImageResource(R.drawable.ic_round_pause_circle_64);
                break;
            case PlaybackStateCompat.STATE_REWINDING:
                break;
            case PlaybackStateCompat.STATE_SKIPPING_TO_NEXT:
                getBinding().btnTogglePlayPause.setVisibility(View.GONE);
                getBinding().prbBuffering.setVisibility(View.VISIBLE);
                break;
            case PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS:
                getBinding().btnTogglePlayPause.setVisibility(View.GONE);
                getBinding().prbBuffering.setVisibility(View.VISIBLE);
                break;
            case PlaybackStateCompat.STATE_SKIPPING_TO_QUEUE_ITEM:
                break;
            case PlaybackStateCompat.STATE_STOPPED:
                break;
        }
    }

    private void updateUI(@NonNull Song song) {
        getBinding().tvSongName.setText(song.getName());
        getBinding().tvSongArtists.setText(song.getArtistsNames());
        setBackgroundView(getBinding().frmLayout, song.getThumbnail());
        Glide.with(this)
                .load(song.getThumbnail())
                .circleCrop()
                .into(getBinding().ivThumbnail);
        getBinding().frmLoading.setVisibility(View.GONE);
    }

    private void updateUI(@NonNull MediaDescriptionCompat mediaDescriptionCompat) {
        getBinding().tvSongName.setText(mediaDescriptionCompat.getTitle());
        getBinding().tvSongArtists.setText(mediaDescriptionCompat.getSubtitle());
        Glide.with(this)
                .load(mediaDescriptionCompat.getIconUri())
                .circleCrop()
                .into(getBinding().ivThumbnail);
        setBackgroundView(getBinding().frmLayout, String.valueOf(mediaDescriptionCompat.getIconUri()));
    }

    private void setBackgroundView(@NonNull View view, @NonNull String imageUrl) {
        binding = getBinding();

        Glide.with(this).asBitmap().override(100).load(imageUrl).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                Palette.from(resource).generate(palette -> {
                    if (palette != null) {
                        int defaultColor = getResources().getColor(R.color.blue_700);
                        int[] gradientColors = new int[2];

                        gradientColors[0] = palette.getDominantColor(defaultColor);

                        if (ColorUtils.calculateLuminance(gradientColors[0]) < 0.25) {
                            gradientColors[0] = palette.getDarkVibrantColor(defaultColor);
                        }

                        if (UiModeUtils.isDarkMode(requireContext())) {
                            gradientColors[1] = getResources().getColor(R.color.black_800);
                        } else {
                            gradientColors[1] = Color.WHITE;
                        }

                        GradientDrawable gradientDrawable = new GradientDrawable(
                                GradientDrawable.Orientation.BOTTOM_TOP,
                                gradientColors
                        );

                        view.setBackground(gradientDrawable);

                        // Nếu màu gradientColors[0] quá tối thì sẽ chỉnh màu chữ thành trắng và ngược lại thành đen
                        if (ColorUtils.calculateLuminance(gradientColors[0]) < 0.3) {
                            binding.tvSongName.setTextColor(Color.WHITE);
                            binding.tvSongArtists.setTextColor(Color.WHITE);
                            binding.tvCurrentPosition.setTextColor(Color.WHITE);
                            binding.tvLengthOfSong.setTextColor(Color.WHITE);
                            binding.btnTogglePlayPause.setColorFilter(getResources().getColor(R.color.gray_300));
                            binding.btnRepeat.setColorFilter(getResources().getColor(R.color.gray_300));
                            binding.btnShuffle.setColorFilter(getResources().getColor(R.color.gray_300));
                            binding.btnSkipToNext.setColorFilter(getResources().getColor(R.color.gray_300));
                            binding.btnSkipToPrevious.setColorFilter(getResources().getColor(R.color.gray_300));
                        } else {
                            binding.tvSongName.setTextColor(Color.BLACK);
                            binding.tvSongArtists.setTextColor(Color.BLACK);
                            binding.tvCurrentPosition.setTextColor(Color.BLACK);
                            binding.tvLengthOfSong.setTextColor(Color.BLACK);
                            binding.btnTogglePlayPause.setColorFilter(getResources().getColor(R.color.black_800));
                            binding.btnRepeat.setColorFilter(getResources().getColor(R.color.black_800));
                            binding.btnShuffle.setColorFilter(getResources().getColor(R.color.black_800));
                            binding.btnSkipToNext.setColorFilter(getResources().getColor(R.color.black_800));
                            binding.btnSkipToPrevious.setColorFilter(getResources().getColor(R.color.black_800));
                        }
                    }
                });
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) { }
        });
    }
}