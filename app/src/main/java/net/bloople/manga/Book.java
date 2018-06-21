package net.bloople.manga;

import android.net.Uri;
import java.util.ArrayList;

class Book {
    private String path;
    private String pagesDeflated;
    private ArrayList<String> pagePaths;
    private int pagesCount;
    private String normalisedTitle;
    private int publishedOn;
    private String key;
    private ArrayList<Tag> tags;
    private long _id;

    Book(String path, String pagesDeflated, int pagesCount, String normalisedTitle, int publishedOn, String key,
         ArrayList<Tag> tags, long _id) {
        this.path = path;
        this.pagesDeflated = pagesDeflated;
        this.pagesCount = pagesCount;
        this.normalisedTitle = normalisedTitle;
        this.publishedOn = publishedOn;
        this.key = key;
        this.tags = tags;
        this._id = _id;
    }

    private String url() {
        return "../" + Uri.encode(path);
    }
    private ArrayList<String> pagePaths() {
        if(pagePaths == null) pagePaths = new PagesInflater(pagesDeflated).inflate();
        return pagePaths;
    }

    String thumbnailUrl() {
      return "img/thumbnails/" + key + ".jpg";
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

    String pageUrl(int index) {
        return url() + "/" + Uri.encode(pagePaths().get(index));
    }

    int pages() {
        //We are trusting the server that mPagesCount matches pagePaths().size()
        return pagesCount;
    }
}
