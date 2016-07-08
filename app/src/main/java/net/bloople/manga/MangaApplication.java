package net.bloople.manga;

import android.app.Application;
import android.net.Uri;
import java.util.List;

/**
 * Created by i on 8/07/2016.
 */
public class MangaApplication extends Application {
    public static List<Book> books;

    public static Uri root() {
        return Uri.parse("http://192.168.1.2/Manga-OG/.mangos/");
    }
}
