package net.bloople.manga;

import com.dslplatform.json.CompiledJson;

import java.util.ArrayList;
import java.util.List;

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

    public String thumbnailUrl() {
        return library.mangos() + "/img/thumbnails/" + key + ".jpg";
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

    String pageUrl(int index) {
        return library.root() + "/" + path + "/" + pagePaths().get(index);
    }

    void inflate(Library library) {
        this.library = library;
        _id = Long.parseLong(key.substring(0, 15), 16); //Using substring of key would be dangerous for large N
        normalisedTitle = path.replaceAll("[^A-Za-z0-9]+", "").toLowerCase();
        title = path.replaceAll("\\s+", " ");
        this.library.books().put(_id, this);
    }
}
