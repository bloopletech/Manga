package net.bloople.manga;

import android.net.Uri;

import java.util.ArrayList;
import java.util.HashMap;

class Library {
    public static Library current;

    private Uri root;
    private HashMap<Long, Book> books;
    private ArrayList<Tag> tags;

    Library(Uri root, HashMap<Long, Book> books, ArrayList<Tag> tags) {
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

    public static void ensureCurrent(Uri root, LibraryLoadedListener listener) {
        if(current != null && current.root().equals(root)) {
            listener.onLibraryLoaded();
        }
        else {
            LoadBooksTask loader = new LoadBooksTask(listener);
            loader.execute(root);
        }
    }
}
