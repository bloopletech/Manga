package net.bloople.manga;

import android.app.Application;
import android.net.Uri;

import java.util.HashMap;

public class MangaApplication extends Application {
    public static HashMap<Long, Book> allBooks;

    public static void ensureAllBooks(BooksLoadedListener listener) {
        if(allBooks != null) {
            listener.onBooksLoaded();
        }
        else {
            LoadBooksTask loader = new LoadBooksTask(listener);
            loader.execute();
        }
    }

    public static Uri root() {
        return Uri.parse("http://192.168.1.100:82/Manga-OG/.mangos/");
    }
}
