package com.music.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.PropertyName;
import com.music.utils.NumberUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;

import java.util.Collections;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Song implements Parcelable {
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
    private long listens;

    /**
     * Năm phát hành
     */
    private int year;

    /**
     * Lượt thích của bài hát
     */
    private long like;

    /**
     * Độ dài bài hát
     */
    private int duration;

    /**
     * Đường dẫn bài hát
     */
    @NonNull
    private String mp3 = StringUtils.EMPTY;

    /**
     * Danh sách nghệ sĩ
     */
    @NonNull
    private List<String> artists = Collections.emptyList();

    /**
     * Danh sách album mà bài hát này thuộc về
     */
    @NonNull
    private List<String> albums = Collections.emptyList();

    protected Song(Parcel in) {
        id = in.readString();
        name = in.readString();
        otherName = in.readString();
        thumbnail = in.readString();
        listens = in.readLong();
        year = in.readInt();
        like = in.readLong();
        duration = in.readInt();
        mp3 = in.readString();
        artists = in.createStringArrayList();
        albums = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(otherName);
        dest.writeString(thumbnail);
        dest.writeLong(listens);
        dest.writeInt(year);
        dest.writeLong(like);
        dest.writeInt(duration);
        dest.writeString(mp3);
        dest.writeStringList(artists);
        dest.writeStringList(albums);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

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

    /**
     * Nối tên các nghệ sĩ lại và cách nhau bởi dấu phẩy
     *
     * @return Danh sách tên nghệ sĩ
     */
    @NonNull
    public String getArtistsNames() {
        return TextUtils.join(", ", artists);
    }

    /**
     * Định dạng lại lượt thích, phân cách nhau bởi dấu chấm
     *
     * @return Lượt thích đã format
     */
    @NonNull
    public String getFormatLike() {
        return NumberUtils.formatWithSuffix(like);
    }

    /**
     * Định dạng lại lượt nghe, phân cách nhau bởi dấu chấm
     *
     * @return Lượt nghe đã format
     */
    @NonNull
    public String getFormatListens() {
        return NumberUtils.formatWithSuffix(listens);
    }

    /**
     * Trả về thời lượng bài hát ở dạng mili giây
     *
     * @return Thời lượng bài hát ở dạng mili giây
     */
    public int getDuration() {
        return duration * 1000;
    }
}
