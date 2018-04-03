package com.example.android.guardiannews;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by SAMO on 3/20/2018.
 */

public class News implements Parcelable {
    private String title, contributer, date, section, url;
    private String imageSmallThumbLink;

    public News(String title, String date, String section, String url, String imageSmallThumbLink, String contributer) {
        this.title = title;
        this.contributer = contributer;
        this.date = date;
        this.imageSmallThumbLink = imageSmallThumbLink;
        this.section = section;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public String getContributer() {
        return contributer;
    }

    public String getDate() {
        return date;
    }

    public String getSection() {
        return section;
    }

    public String getUrl() {
        return url;
    }

    public String getImageSmallThumbLink() {
        return imageSmallThumbLink;
    }

    public News(Parcel in) {
        title = in.readString();
        contributer = in.readString();
        date = in.readString();
        section = in.readString();
        url = in.readString();
        imageSmallThumbLink = in.readString();
    }

    public static final Creator<News> CREATOR = new Creator<News>() {
        @Override
        public News createFromParcel(Parcel in) {
            return new News(in);
        }

        @Override
        public News[] newArray(int size) {
            return new News[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(contributer);
        dest.writeString(date);
        dest.writeString(section);
        dest.writeString(url);
        dest.writeString(imageSmallThumbLink);
    }
}
