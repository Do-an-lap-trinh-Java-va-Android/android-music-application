package com.music.adapters;

import android.graphics.Rect;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.music.databinding.CardBinding;
import com.music.models.Artist;
import com.music.models.Song;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    private final List<Song> songs;

    public SongAdapter(List<Song> songs) {
        this.songs = songs;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SongViewHolder(
                CardBinding.inflate(
                        LayoutInflater.from(parent.getContext()), parent, false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        holder.setSong(songs.get(position));
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    static class SongViewHolder extends RecyclerView.ViewHolder {
        @NonNull
        private final CardBinding binding;

        public SongViewHolder(@NonNull CardBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }

        public void setSong(@NonNull Song song) {
            binding.tvTitle.setText(song.getName());

            List<Artist> artists = new ArrayList<>();

            for (DocumentReference artist : song.getArtists()) {
                artist.get().addOnSuccessListener(documentSnapshot -> {
                    artists.add(documentSnapshot.toObject(Artist.class));

                    binding.tvDescription.setText(StringUtils.join(
                            artists.stream().map(Artist::getName).collect(Collectors.toList()),
                            ", "
                    ));
                });
            }

            Glide.with(itemView).load(song.getImage()).into(binding.ivImage);
        }
    }

    public static class SongItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            final int position = parent.getChildAdapterPosition(view);

            if (position == RecyclerView.NO_POSITION) {
                return;
            }

            if (position == 0) {
                outRect.left = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16,
                        parent.getContext().getResources().getDisplayMetrics());
            }

            outRect.right = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16,
                    parent.getContext().getResources().getDisplayMetrics());
        }
    }
}
