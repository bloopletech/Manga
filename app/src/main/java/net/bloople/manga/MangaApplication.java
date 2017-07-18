package net.bloople.manga;

import android.app.Application;
import android.content.Context;
import android.net.Uri;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by i on 8/07/2016.
 */
public class MangaApplication extends Application {
    public static HashMap<Long, Book> allBooks;

    public static void ensureAllBooks(Context context, BooksLoadedListener listener) {
        if(allBooks != null) {
            listener.onBooksLoaded();
        }
        else {
            LoadBooksTask loader = new LoadBooksTask(context, listener);
            loader.execute();
        }
    }

    public static Uri root() {
        return Uri.parse("http://192.168.1.2/Manga-OG/.mangos/");
    }
}
