package net.bloople.manga;

import java.util.ArrayList;

public class SearchResults {
    private Library library;
    private ArrayList<Long> bookIds;

    public SearchResults(Library library, ArrayList<Long> bookIds) {
        this.library = library;
        this.bookIds = bookIds;
    }

    public Library library() {
        return library;
    }

    public ArrayList<Long> bookIds() {
        return bookIds;
    }
}
