package com.music.models;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.PropertyName;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Artist {
    /**
     * ID nghệ sĩ
     */
    @NonNull
    @DocumentId
    private String id = StringUtils.EMPTY;

    /**
     * Nghệ danh
     */
    @NonNull
    private String name = StringUtils.EMPTY;

    /**
     * Tên thật của nghệ sĩ
     */
    @NonNull
    private String realName = StringUtils.EMPTY;

    /**
     * Giới thiệu
     */
    @NonNull
    private String biography = StringUtils.EMPTY;

    /**
     * Ảnh cover
     */
    @NonNull
    private Uri cover = Uri.EMPTY;

    /**
     * Ảnh đại diện
     */
    @NonNull
    private Uri thumbnail = Uri.EMPTY;

    /**
     * Quốc gia của nghệ sĩ
     */
    @NonNull
    private String national = StringUtils.EMPTY;

    @NonNull
    @PropertyName("real_name")
    public String getRealName() {
        return realName;
    }

    @PropertyName("real_name")
    public void setRealName(@NonNull String realName) {
        this.realName = realName;
    }

    public void setCover(@NonNull String cover) {
        this.cover = Uri.parse(cover);
    }

    public void setThumbnail(@NonNull String thumbnail) {
        this.thumbnail = Uri.parse(thumbnail);
    }
}
