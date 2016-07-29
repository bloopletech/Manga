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

/**
 * Created by i on 8/07/2016.
 */
public class BooksLoader {
    public ArrayList<Book> load() throws IOException, JSONException {
        Uri dataUri = MangaApplication.root().buildUpon().appendPath("data.json").build();

        ArrayList<Book> books = new ArrayList<>();

        JSONArray bookObjects = new JSONArray(getContent(dataUri));

        for(int i = 0; i < bookObjects.length(); i++) {
            JSONObject bookObject = (JSONObject) bookObjects.get(i);
            books.add(toBook(bookObject));
        }

        return books;
    }

    private Book toBook(JSONObject object) throws JSONException {
        String url = object.getString("url");
        String title = object.getString("title");
        int publishedOn = object.getInt("publishedOn");
        String thumbnailUrl = object.getString("thumbnailUrl");
        String key = object.getString("key");

        JSONArray pageUrlObjects = object.getJSONArray("pageUrls");
        ArrayList<String> pageUrls = new ArrayList<>();
        for(int i = 0; i < pageUrlObjects.length(); i++) pageUrls.add(pageUrlObjects.getString(i));

        return new Book(url, pageUrls, thumbnailUrl, title, publishedOn, key);
    }

    private String getContent(Uri uri) throws IOException {
        URLConnection connection = new URL(uri.toString()).openConnection();

        int contentLength = connection.getContentLength();
        if(contentLength == -1) contentLength = 10000000;

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder sb = new StringBuilder(contentLength);
        String line;

        while((line = reader.readLine()) != null) sb.append(line).append("\n");

        reader.close();

        return sb.toString();
    }
}
