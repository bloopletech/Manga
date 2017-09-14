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

class BooksLoader {
    public static final String CACHE_FILE_NAME = "cached-data.json";
    static final int DEFAULT_CONTENT_LENGTH = 10000000;

    ArrayList<Book> load() throws IOException, JSONException {

        ArrayList<Book> books = new ArrayList<>();

        JSONArray bookObjects = new JSONArray(getContentFromUri());

        for(int i = 0; i < bookObjects.length(); i++) {
            JSONObject bookObject = (JSONObject) bookObjects.get(i);
            books.add(toBook(bookObject));
        }

        return books;
    }

    private Book toBook(JSONObject object) throws JSONException {
        String url = object.getString("url");
        String title = object.getString("title");
        String normalisedTitle = normalise(title);
        int publishedOn = object.getInt("publishedOn");
        String thumbnailUrl = object.getString("thumbnailUrl");
        String key = object.getString("key");
        long _id = Long.parseLong(key.substring(0, 15), 16); //Using substring of key would be dangerous for large N
        String pagesDeflated = object.getString("pageUrls");
        int pagesCount = object.getInt("pages");

        return new Book(url, pagesDeflated, pagesCount, thumbnailUrl, title, normalisedTitle, publishedOn, _id);
    }

    private String getContentFromUri() throws IOException {
        Uri dataUri = MangaApplication.root().buildUpon().appendPath("data.json").build();

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
