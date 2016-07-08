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

    public String url() {
        return mUrl;
    }

    public List<String> pageUrls() {
        return mPageUrls;
    }

    public String thumbnailUrl() {
        return mThumbnailUrl;
    }

    public String title() {
        return mTitle;
    }

    public int publishedOn() {
        return mPublishedOn;
    }

    public String key() {
        return mKey;
    }

    public Uri pageUrl(int index) {
        return MangaApplication.root().buildUpon().appendEncodedPath(url())
                .appendEncodedPath(mPageUrls.get(index)).build();
    }

    public int pages() {
        return mPageUrls.size();
    }
}
