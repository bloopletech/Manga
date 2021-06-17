package net.bloople.manga;

import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaderFactory;
import com.bumptech.glide.load.model.LazyHeaders;
import com.dslplatform.json.CompiledJson;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Credentials;

@CompiledJson(onUnknown = CompiledJson.Behavior.FAIL)
public class Book {
    public String path;
    public String pagePaths;
    public int pages; //We are trusting the server that pages matches pagePathsList().size()
    public int publishedOn;
    public String key;
    public List<String> tags;

    private Library library;
    private String title;
    private String normalisedTitle;
    private long _id;
    private ArrayList<String> pagePathsList;

    public Library library() {
        return library;
    }

    public MangosUrl thumbnailUrl() {
        return library.mangos().withAppendedPath("/img/thumbnails/" + key + ".jpg");
    }

    public String title() {
        return title;
    }

    String normalisedTitle() {
        return normalisedTitle;
    }

    public long id() {
        return _id;
    }

    private ArrayList<String> pagePaths() {
        if(pagePathsList == null) pagePathsList = new PagesInflater(pagePaths).inflate();
        return pagePathsList;
    }

    MangosUrl pageUrl(int index) {
        return library.rootUrl().withAppendedPath("/" + path + "/" + pagePaths().get(index));
    }

    void inflate(Library library) {
        this.library = library;
        _id = Long.parseLong(key.substring(0, 15), 16); //Using substring of key would be dangerous for large N
        normalisedTitle = path.replaceAll("[^A-Za-z0-9]+", "").toLowerCase();
        title = path.replaceAll("\\s+", " ");
        this.library.books().put(_id, this);
    }
}
