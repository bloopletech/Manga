package net.bloople.manga;

import android.net.Uri;
import java.util.List;

class Book {
    private String mUrl;
    private String mPagesDeflated;
    private List<String> mPageUrls;
    private int mPagesCount;
    private String mThumbnailUrl;
    private String mTitle;
    private String mNormalisedTitle;
    private int mPublishedOn;
    private long _id;

    Book(String url, String pagesDeflated, int pagesCount, String thumbnailUrl, String title,
                String normalisedTitle, int publishedOn, long _id) {
        mUrl = url;
        mPagesDeflated = pagesDeflated;
        mPagesCount = pagesCount;
        mThumbnailUrl = thumbnailUrl;
        mTitle = title;
        mNormalisedTitle = normalisedTitle;
        mPublishedOn = publishedOn;
        this._id = _id;
    }

    String url() {
        return mUrl;
    }

    public String pagesDeflated() {
        return mPagesDeflated;
    }

    List<String> pageUrls() {
        if(mPageUrls == null) mPageUrls = new PagesInflater(mPagesDeflated).inflate();
        return mPageUrls;
    }

    String thumbnailUrl() {
        return mThumbnailUrl;
    }

    String title() {
        return mTitle;
    }

    String normalisedTitle() {
        return mNormalisedTitle;
    }

    int publishedOn() {
        return mPublishedOn;
    }

    public long id() {
        return _id;
    }

    Uri pageUrl(int index) {
        return MangaApplication.root().buildUpon().appendEncodedPath(url())
                .appendEncodedPath(pageUrls().get(index)).build();
    }

    int pages() {
        //We are trusting the server that mPagesCount matches pageUrls().size()
        return mPagesCount;
    }
}
