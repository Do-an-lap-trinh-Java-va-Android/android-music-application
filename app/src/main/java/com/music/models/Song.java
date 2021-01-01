package com.music.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.PropertyName;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Song {
    /**
     * ID bài hát
     */
    @NonNull
    @DocumentId
    private String id = StringUtils.EMPTY;

    /**
     * Tên bài hát
     */
    @NonNull
    private String name = StringUtils.EMPTY;

    /**
     * Tên khác của bài hát
     */
    @Nullable
    private String otherName;

    /**
     * Đường dẫn hình ảnh
     */
    @NonNull
    private String thumbnail = StringUtils.EMPTY;

    /**
     * Lượt nghe của bài hát
     */
    private long views;

    /**
     * Danh sách nghệ sĩ
     */
    @NonNull
    private List<String> artists = new ArrayList<>();

    /**
     * Viết hoa ký tự đầu của mỗi từ trong tên bài hát
     */
    public void setName(@NonNull String name) {
        this.name = WordUtils.capitalize(name);
    }

    /**
     * Lấy tên khác của bài hát
     *
     * @return Tên khác của bài hát
     */
    @Nullable
    @SuppressWarnings("unused")
    @PropertyName("other_name")
    public String getOtherName() {
        return otherName;
    }

    /**
     * Gán tên khác cho bài hát
     *
     * @param otherName Tên khác của bài hát
     */
    @SuppressWarnings("unused")
    @PropertyName("other_name")
    public void setOtherName(@Nullable String otherName) {
        this.otherName = otherName;
    }
}
