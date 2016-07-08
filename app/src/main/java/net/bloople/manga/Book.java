package net.bloople.manga;

import android.net.Uri;

import java.util.List;

/**
 * Created by i on 8/07/2016.
 */
public class Book {
    private String mUrl;
    private List<String> mPageUrls;
    private String mThumbnailUrl;
    private String mTitle;
    private int mPublishedOn;
    private String mKey;

    public Book(String url, List<String> pageUrls, String thumbnailUrl, String title, int publishedOn, String key) {
        mUrl = url;
        mPageUrls = pageUrls;
        mThumbnailUrl = thumbnailUrl;
        mTitle = title;
        mPublishedOn = publishedOn;
        mKey = key;
    }

    public String getUrl() {
        return mUrl;
    }

    public List<String> getPageUrls() {
        return mPageUrls;
    }

    public String getThumbnailUrl() {
        return mThumbnailUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getPublishedOn() {
        return mPublishedOn;
    }

    public String getKey() {
        return mKey;
    }

    public Uri pageUrl(int index) {
        return MangaApplication.root().buildUpon().appendEncodedPath(getUrl())
                .appendEncodedPath(mPageUrls.get(index)).build();
    }

    public int pages() {
        return mPageUrls.size();
    }
}
