package net.bloople.manga;

import android.net.Uri;

import java.util.ArrayList;
import java.util.HashMap;

class Mango {
    public static Mango current;

    private Uri root;
    private HashMap<Long, Book> books;
    private ArrayList<Tag> tags;

    Mango(Uri root, HashMap<Long, Book> books, ArrayList<Tag> tags) {
        this.root = root;
        this.books = books;
        this.tags = tags;
    }

    public Uri root() {
        return root;
    }

    public HashMap<Long, Book> books() {
        return books;
    }

    public ArrayList<Tag> tags() {
        return tags;
    }

    public static void ensureCurrent(Uri root, BooksLoadedListener listener) {
        if(current != null && current.root().equals(root)) {
            listener.onBooksLoaded();
        }
        else {
            LoadBooksTask loader = new LoadBooksTask(listener);
            loader.execute(root);
        }
    }
}