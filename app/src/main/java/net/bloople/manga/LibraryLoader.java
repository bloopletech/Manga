package net.bloople.manga;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

class LibraryLoader {
    public static final String CACHE_FILE_NAME = "cached-data.json";
    static final int DEFAULT_CONTENT_LENGTH = 10000000;

    private Uri root;
    private HashMap<Long, Book> books = new HashMap<>();
    private ArrayList<Tag> tags = new ArrayList<>();

    LibraryLoader(Uri root) {
        this.root = root;
    }

    Uri root() { return root; }

    HashMap<Long, Book> books() {
        return books;
    }

    ArrayList<Tag> tags() {
        return tags;
    }

    void load() throws IOException, JSONException {
        JSONArray bookObjects = new JSONArray(getContentFromUri());

        for(int i = 0; i < bookObjects.length(); i++) {
            JSONObject bookObject = (JSONObject) bookObjects.get(i);
            Book book = toBook(bookObject);
            books.put(book.id(), book);
        }
    }

    private Book toBook(JSONObject object) throws JSONException {
        String path = object.getString("path");
        String normalisedTitle = normalise(path);
        int publishedOn = object.getInt("publishedOn");
        String key = object.getString("key");
        long _id = Long.parseLong(key.substring(0, 15), 16); //Using substring of key would be dangerous for large N
        String pagesDeflated = object.getString("pagePaths");
        int pagesCount = object.getInt("pages");

        ArrayList<Tag> bookTags = new ArrayList<>();
        JSONArray tagObjects = object.getJSONArray("tags");
        for(int i = 0; i < tagObjects.length(); i++) bookTags.add(addTag(tagObjects.getString(i)));

        return new Book(root, path, pagesDeflated, pagesCount, normalisedTitle, publishedOn, key, bookTags, _id);
    }

    private Tag addTag(String tagString) {
        for(Tag tag : tags) {
            if(tag.tag().equals(tagString)) {
                tag.popularity(tag.popularity() + 1);
                return tag;
            }
        }

        Tag tag = new Tag(tags.size(), tagString);
        tags.add(tag);
        return tag;
    }

    private String getContentFromUri() throws IOException {
        Uri dataUri = root.buildUpon().appendPath("data.json").build();

        URLConnection connection = new URL(dataUri.toString()).openConnection();

        int contentLength = connection.getContentLength();
        if(contentLength == -1) contentLength = DEFAULT_CONTENT_LENGTH;

        return readStream(connection.getInputStream(), contentLength);
    }

    private String readStream(InputStream stream, int probableContentLength) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder sb = new StringBuilder(probableContentLength);
        String line;

        while((line = reader.readLine()) != null) sb.append(line).append("\n");

        reader.close();

        return sb.toString();
    }

    private String normalise(String title) {
        return title.replaceAll("[^A-Za-z0-9]+", "").toLowerCase();
    }
}
