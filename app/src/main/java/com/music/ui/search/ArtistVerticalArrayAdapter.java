package com.music.ui.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.music.R;
import com.music.models.Artist;
import com.music.models.Song;

import java.util.List;

public class ArtistVerticalArrayAdapter extends ArrayAdapter<Artist> {
    public ArtistVerticalArrayAdapter(@NonNull Context context, int resource, @NonNull List<Artist> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View row, ViewGroup parent) {
        if (row == null) {
            row = LayoutInflater.from(getContext()).inflate(R.layout.search_item_layout, parent, false);
        }

        ImageView ivThumbnail = row.findViewById(R.id.thumbnail);
        TextView tvSongName = row.findViewById(R.id.name);

        Glide.with(getContext()).load(getItem(position).getThumbnail()).into(ivThumbnail);
        tvSongName.setText(getItem(position).getName());

        return row;
    }
}
