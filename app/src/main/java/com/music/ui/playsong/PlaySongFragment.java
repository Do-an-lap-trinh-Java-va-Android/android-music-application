package com.music.ui.playsong;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.music.R;
import com.music.databinding.FragmentPlaySongBinding;
import com.music.models.Song;
import com.music.ui.playsong.background.MediaPlayerReceiver;
import com.music.ui.playsong.background.MediaPlayerService;
import com.music.ui.playsong.notification.MediaPlayerNotification;
import com.music.ui.playsong.notification.MediaPlayerNotificationReceiver;
import com.music.ui.playsong.notification.OnClearFromRecentService;

import org.apache.commons.lang3.time.DurationFormatUtils;

import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import dagger.hilt.android.qualifiers.ApplicationContext;

@AndroidEntryPoint
public class PlaySongFragment extends Fragment {
    @Nullable
    private FragmentPlaySongBinding binding;

    @SuppressWarnings("NotNullFieldNotInitialized")
    @NonNull
    private PlaySongFragmentArgs args;

    @SuppressWarnings("FieldCanBeLocal")
    private PlaySongViewModel viewModel;

    @NonNull
    private final Handler handler = new Handler(Looper.myLooper());

    @SuppressWarnings("FieldCanBeLocal")
    @Nullable
    private NotificationManager notificationManager;

    private Song song;

    @Nullable
    private MediaPlayerService mediaPlayerService;

    @Inject
    @ApplicationContext
    Context applicationContext;

    /**
     * Lắng nghe các hành động từ thanh trình phát nhạc trên thanh thông báo
     */
    @NonNull
    final BroadcastReceiver musicPlayerOnNotificationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getStringExtra("actionName");

            if (MediaPlayerNotification.ACTION_PLAY.equals(action)) {
                binding.btnPlay.performClick();
            }
        }
    };

    /**
     * Lắng nghe các hành động từ trình phát nhạc đang chạy ở nền, từ đó sẽ cập nhật giao diện phù hợp
     */
    @NonNull
    private final BroadcastReceiver mediaPlayerInBackgroundBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getStringExtra("actionName");

            if (MediaPlayerService.ACTION_PREPARED.equals(action)) {
                // Độ dài của bài hát được gửi lên từ trình phát nhạc chạy dưới nền
                int duration = intent.getIntExtra("duration", 0);

                // Đặt max cho seekBar bằng với độ dài của bài hát
                binding.seekBar.setMax(duration);

                // Cập nhật độ dài của bài hát lên giao diện
                binding.tvLengthOfSong.setText(DurationFormatUtils.formatDuration(
                        duration, "mm:ss", true
                ));

                // Giả lập thao tác click chuột vào nút btnPlay thay vì viết lại code
                binding.btnPlay.performClick();
            }
        }
    };

    /**
     * Mỗi 500ms sẽ tiến hành cập nhật thanh SeekBar
     */
    @NonNull
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayerService != null) {
                binding.seekBar.setProgress(mediaPlayerService.getMediaPlayer().getCurrentPosition());
            }

            handler.postDelayed(this, 500);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        args = PlaySongFragmentArgs.fromBundle(requireArguments());

        // Khởi tạo kênh thông báo riêng cho trình phát nhạc
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    MediaPlayerNotification.CHANNEL_ID,
                    "Trình phát nhạc",
                    NotificationManager.IMPORTANCE_HIGH
            );

            notificationManager = applicationContext.getSystemService(NotificationManager.class);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        requireActivity().registerReceiver(musicPlayerOnNotificationBroadcastReceiver,
                new IntentFilter(MediaPlayerNotificationReceiver.INTENT_FILTER_NAME));

        requireActivity().registerReceiver(mediaPlayerInBackgroundBroadcastReceiver,
                new IntentFilter(MediaPlayerReceiver.INTENT_FILTER_NAME));

        requireActivity().bindService(new Intent(requireActivity(), MediaPlayerService.class),
                serviceConnection, Context.BIND_AUTO_CREATE);

        requireActivity().startService(
                new Intent(applicationContext, OnClearFromRecentService.class)
        );
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPlaySongBinding.inflate(inflater, container, false);

        /*
            - Đăng ký sự kiện dừng/phát cho nút btnPlay
            - Nếu bài hát đang phát thì khi nhấn vào sẽ dừng lại, ngược lại nếu đang dừng phát thì sẽ tiếp tục
            phát tiếp bài hát
        */
        binding.btnPlay.setOnClickListener(view -> {
            if (mediaPlayerService == null) return;

            if (mediaPlayerService.getMediaPlayer().isPlaying()) {
                // Dừng phát
                mediaPlayerService.getMediaPlayer().pause();

                // Cập nhật lại trình phát nhạc trên thanh thông báo
                MediaPlayerNotification.createNotification(
                        applicationContext, song, R.drawable.ic_outline_play_circle_light_64
                );

                // Hiển thị icon phát
                binding.btnPlay.setImageResource(R.drawable.ic_outline_play_circle_light_64);

                // Dừng cập nhật thời gian nghe của bài hát trên thanh SeekBar
                handler.removeCallbacks(runnable);
            } else {
                // Phát nhạc
                mediaPlayerService.getMediaPlayer().start();

                // Cập nhật lại trình phát nhạc trên thanh thông báo
                MediaPlayerNotification.createNotification(
                        applicationContext, song, R.drawable.ic_outline_pause_circle_light_64
                );

                // Hiển thị icon dừng
                binding.btnPlay.setImageResource(R.drawable.ic_outline_pause_circle_light_64);

                // Bắt đầu cập nhật thời gian đã nghe của bài hát trên thanh SeekBar
                handler.postDelayed(runnable, 0);
            }
        });

        // Cập nhật thanh thời gian phát bài hát
        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayerService == null) return;

                // Cập nhật thời gian đã phát của bài hát
                binding.tvCurrentPosition.setText(
                        DurationFormatUtils.formatDuration(
                                mediaPlayerService.getMediaPlayer().getCurrentPosition(),
                                "mm:ss",
                                true
                        )
                );

                // Nếu phát hiện người dùng tua nhạc thì sẽ nhảy đến đoạn người dùng đã tua
                if (fromUser) {
                    mediaPlayerService.getMediaPlayer().seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        return binding.getRoot();
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mediaPlayerService = ((MediaPlayerService.LocalBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) { }
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(PlaySongViewModel.class);

        // Lấy thông tin bài hát
        viewModel.getInfoOfSong(args.getSongUid());

        viewModel.getSongMutableLiveData().observe(getViewLifecycleOwner(), response -> {
            if (binding == null) return;

            switch (response.status) {
                case SUCCESS:
                    this.song = response.data;
                    Song song = Objects.requireNonNull(response.data);

                    Glide.with(binding.ivThumbnail.getContext()).load(song.getThumbnail()).circleCrop().into(binding.ivThumbnail);
                    binding.tvSongName.setText(song.getName());
                    binding.tvSongArtists.setText(song.getArtistsNames());

                    Intent intentPlaySongInBackground = new Intent(requireActivity(), MediaPlayerService.class);
                    intentPlaySongInBackground.setAction(MediaPlayerService.ACTION_PLAY);
                    intentPlaySongInBackground.putExtra("song", song);
                    requireActivity().startService(intentPlaySongInBackground);

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
        handler.removeCallbacks(runnable);
        requireActivity().unbindService(serviceConnection);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;

        requireActivity().unregisterReceiver(musicPlayerOnNotificationBroadcastReceiver);
        requireActivity().unregisterReceiver(mediaPlayerInBackgroundBroadcastReceiver);

        if (notificationManager != null) {
            notificationManager.cancelAll();
            notificationManager = null;
        }

        mediaPlayerService = null;
    }
}