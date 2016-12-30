package net.bloople.manga;

import android.net.Uri;
import java.util.List;

/**
 * Created by i on 8/07/2016.
 */
public class Book {
    private String mUrl;
    private String mPagesDeflated;
    private List<String> mPageUrls;
    private int mPagesCount;
    private String mThumbnailUrl;
    private String mTitle;
    private int mPublishedOn;
    private long _id;

    public Book(String url, String pagesDeflated, int pagesCount, String thumbnailUrl, String title, int publishedOn, long _id) {
        mUrl = url;
        mPagesDeflated = pagesDeflated;
        mPagesCount = pagesCount;
        mThumbnailUrl = thumbnailUrl;
        mTitle = title;
        mPublishedOn = publishedOn;
        this._id = _id;
    }

    public String url() {
        return mUrl;
    }

    public String pagesDeflated() {
        return mPagesDeflated;
    }

    public List<String> pageUrls() {
        if(mPageUrls == null) mPageUrls = new PagesInflater(mPagesDeflated).inflate();
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

    public long id() {
        return _id;
    }

    public Uri pageUrl(int index) {
        return MangaApplication.root().buildUpon().appendEncodedPath(url())
                .appendEncodedPath(pageUrls().get(index)).build();
    }

    public int pages() {
        //We are trusting the server that mPagesCount matches pageUrls().size()
        return mPagesCount;
    }
}
