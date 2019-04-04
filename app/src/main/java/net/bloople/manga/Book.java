package net.bloople.manga;

import android.net.Uri;
import java.util.ArrayList;

class Book {
    private Library library;
    private String path;
    private String pagesDeflated;
    private ArrayList<String> pagePaths;
    private int pagesCount;
    private String normalisedTitle;
    private int publishedOn;
    private String key;
    private ArrayList<Tag> tags;
    private long _id;

    Book(Library library, String path, String pagesDeflated, int pagesCount, String normalisedTitle,
         int publishedOn, String key, ArrayList<Tag> tags, long _id) {
        this.library = library;
        this.path = path;
        this.pagesDeflated = pagesDeflated;
        this.pagesCount = pagesCount;
        this.normalisedTitle = normalisedTitle;
        this.publishedOn = publishedOn;
        this.key = key;
        this.tags = tags;
        this._id = _id;
    }

    Uri thumbnailUrl() {
        return library.mangos().buildUpon().appendEncodedPath("img/thumbnails/" + key + ".jpg").build();
    }

    String title() {
      return path.replaceAll("\\s+", " ");
    }

    String normalisedTitle() {
        return normalisedTitle;
    }

    int publishedOn() {
        return publishedOn;
    }

    public String key() {
      return key;
    }

    public ArrayList<Tag> tags() {
        return tags;
    }

    public long id() {
        return _id;
    }

    private ArrayList<String> pagePaths() {
        if(pagePaths == null) pagePaths = new PagesInflater(pagesDeflated).inflate();
        return pagePaths;
    }

    private String relativePageUrl(int index) {
        return Uri.encode(path) + "/" + Uri.encode(pagePaths().get(index));
    }

    Uri pageUrl(int index) {
        return library.root().buildUpon().appendEncodedPath(relativePageUrl(index)).build();
    }

    int pages() {
        //We are trusting the server that mPagesCount matches pagePaths().size()
        return pagesCount;
    }
}
