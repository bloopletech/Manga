package net.bloople.manga;

import android.content.Context;
import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by i on 8/07/2016.
 */
public class BooksLoader {
    public static final String CACHE_FILE_NAME = "cached-data.json";
    public static final int DEFAULT_CONTENT_LENGTH = 10000000;

    private Context context;

    public BooksLoader(Context inContext) {
        context = inContext;
    }

    public ArrayList<Book> load() throws IOException, JSONException {

        ArrayList<Book> books = new ArrayList<>();

        JSONArray bookObjects = new JSONArray(getContent());

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
        String pagesDeflated = object.getString("pageUrls");
        int pagesCount = object.getInt("pages");

        return new Book(url, pagesDeflated, pagesCount, thumbnailUrl, title, publishedOn, key);
    }

    private String getContent() throws IOException {
        String content = getContentFromFile();
        if(content == null) {
            content = getContentFromUri();
            saveContent(content);
        }
        return content;
    }

    private String getContentFromFile() throws IOException {
        try {
            return readStream(context.openFileInput(CACHE_FILE_NAME), DEFAULT_CONTENT_LENGTH);
        }
        catch(FileNotFoundException e) {
            return null;
        }
    }

    private void saveContent(String content) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(context.openFileOutput(CACHE_FILE_NAME,
                Context.MODE_PRIVATE)));

        writer.write(content);
        writer.close();
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
}
