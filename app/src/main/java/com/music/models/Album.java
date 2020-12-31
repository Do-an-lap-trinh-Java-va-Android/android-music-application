package com.music.models;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.DocumentReference;

import org.apache.commons.text.WordUtils;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Album {
    /**
     * ID album
     */
    @DocumentId
    private String id;

    /**
     * Tên album
     */
    @NonNull
    private String name;

    /**
     * Mô tả ngắn gọn của album
     */
    @NonNull
    private String description;

    /**
     * Đường dẫn hình ảnh album
     */
    @NonNull
    private String cover;

    /**
     * Danh sách nghệ sĩ
     */
    @NonNull
    private List<DocumentReference> songs;

    /**
     * Viết hoa ký tự đầu của mỗi từ trong tên album
     */
    public void setName(@NonNull String name) {
        this.name = WordUtils.capitalize(name);
    }
}
