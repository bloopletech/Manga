package net.bloople.manga;

import android.os.Parcel;
import android.os.Parcelable;

import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import okhttp3.Credentials;

public class MangosUrl implements Parcelable {
    private String url;
    private String username;
    private String password;

    public MangosUrl(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public MangosUrl withAppendedPath(String pathSegment) {
        return new MangosUrl(url + pathSegment, username, password);
    }

    public GlideUrl toGlideUrl() {
        if(username != null && password != null) {
            String credential = Credentials.basic(username, password);
            return new GlideUrl(url, new LazyHeaders.Builder().addHeader("Authorization", credential).build());
        }

        return new GlideUrl(url);
    }

    public URLConnection toUrlConnection() throws IOException {
        if(username != null && password != null) {
            String credential = Credentials.basic(username, password);
            URLConnection connection = new URL(url).openConnection();
            connection.addRequestProperty("Authorization", credential);
            return connection;
        }

        return new URL(url).openConnection();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeString(this.username);
        dest.writeString(this.password);
    }

    public void readFromParcel(Parcel source) {
        this.url = source.readString();
        this.username = source.readString();
        this.password = source.readString();
    }

    protected MangosUrl(Parcel in) {
        this.url = in.readString();
        this.username = in.readString();
        this.password = in.readString();
    }

    public static final Parcelable.Creator<MangosUrl> CREATOR = new Parcelable.Creator<MangosUrl>() {
        @Override
        public MangosUrl createFromParcel(Parcel source) {
            return new MangosUrl(source);
        }

        @Override
        public MangosUrl[] newArray(int size) {
            return new MangosUrl[size];
        }
    };
}
