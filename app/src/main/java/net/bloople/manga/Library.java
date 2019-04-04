package net.bloople.manga;

import android.net.Uri;

import java.util.ArrayList;
import java.util.HashMap;

class Library {
    private Uri root;
    private Uri mangos;
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

    public Uri mangos() {
        if(mangos == null) mangos = root.buildUpon().appendEncodedPath(".mangos").build();
        return mangos;
    }

    public HashMap<Long, Book> books() {
        return books;
    }

    public ArrayList<Tag> tags() {
        return tags;
    }
}
