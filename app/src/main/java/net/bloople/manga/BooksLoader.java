package net.bloople.manga;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by i on 8/07/2016.
 */
public class BooksLoader {
    private URL mRoot;

    public BooksLoader(URL root) {
        mRoot = root;
    }

    public ArrayList<Book> load() throws IOException {
        URL dataUrl = new URL(mRoot, "data.json");
        System.out.println(dataUrl);
        Object content = dataUrl.getContent();
        System.out.println(content);
        return new ArrayList<Book>();

    }
}
