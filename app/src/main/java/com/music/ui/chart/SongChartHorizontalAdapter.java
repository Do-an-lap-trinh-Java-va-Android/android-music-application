package com.music.ui.chart;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.music.databinding.ChartSongContainerBinding;
import com.music.models.Song;

public class SongChartHorizontalAdapter extends FirestorePagingAdapter<Song, SongChartHorizontalAdapter.SongChartHorizontalViewHolder> {
    private static final String TAG = "SongChartHorizontalAdap";

    public SongChartHorizontalAdapter(@NonNull FirestorePagingOptions<Song> options) {
        super(options);
    }

    @NonNull
    @Override
    public SongChartHorizontalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SongChartHorizontalViewHolder(
                ChartSongContainerBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    protected void onBindViewHolder(@NonNull SongChartHorizontalViewHolder holder, int position, @NonNull Song model) {
        holder.bindData(model);
    }

    @Override
    protected void onLoadingStateChanged(@NonNull LoadingState state) {
        switch (state) {
            case LOADING_INITIAL:
                Log.i(TAG, "onLoadingStateChanged: Khởi tạo dữ liệu");
                break;
            case LOADING_MORE:
                Log.i(TAG, "onLoadingStateChanged: Đang tải thêm dữ liệu");
                break;
            case LOADED:
                Log.i(TAG, "onLoadingStateChanged: Đã tải: " + getItemCount() + " bài hát");
                break;
            case FINISHED:
                Log.i(TAG, "onLoadingStateChanged: Đã tải tất cả bài hát");
                break;
            case ERROR:
                Log.i(TAG, "onLoadingStateChanged: Đã xảy ra lỗi khi tải bài hát");
                break;
        }
    }

    static class SongChartHorizontalViewHolder extends RecyclerView.ViewHolder {
        @NonNull
        private final ChartSongContainerBinding binding;

        public SongChartHorizontalViewHolder(@NonNull ChartSongContainerBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }

        public void bindData(@NonNull Song song) {
            binding.name.setText(song.getName());
            binding.artists.setText(TextUtils.join(", ", song.getArtists()));
            Glide.with(itemView).load(song.getThumbnail()).into(binding.thumbnail);
        }
    }
}
