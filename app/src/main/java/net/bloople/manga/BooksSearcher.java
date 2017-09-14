package net.bloople.manga;

import java.util.ArrayList;
import java.util.Map;

class BooksSearcher {
    static final int LONG_BOOK_PAGES = 100;
    static final String SPECIAL_LONG_BOOK = "s.long";

    private String searchText = "";
    private ArrayList<Long> filterIds;

    BooksSearcher() {
    }

    void setSearchText(String inSearchText) {
        searchText = inSearchText;
    }

    void setFilterIds(ArrayList<Long> filterIds) {
        this.filterIds = filterIds;
    }

    ArrayList<Book> search() {
        ArrayList<Book> books = new ArrayList<>();

        String[] searchTerms = searchText.toLowerCase().split("\\s+");

        bookLoop:
        for(Map.Entry<Long, Book> entry : MangaApplication.allBooks.entrySet()) {
            if(filterIds != null && !filterIds.isEmpty() && !filterIds.contains(entry.getKey())) continue;

            Book b = entry.getValue();

            String compareTitle = b.title().toLowerCase();

            for(String searchTerm : searchTerms) {
                if(searchTerm.startsWith("-")) {
                    String realSearchTerm = searchTerm.substring(1);

                    if(realSearchTerm.equals(SPECIAL_LONG_BOOK)) {
                        if(b.pages() >= LONG_BOOK_PAGES) continue bookLoop;
                    }
                    else if(compareTitle.contains(realSearchTerm)) continue bookLoop;
                }
                else {
                    if(searchTerm.equals(SPECIAL_LONG_BOOK)) {
                        if(b.pages() < LONG_BOOK_PAGES) continue bookLoop;
                    }
                    else if(!compareTitle.contains(searchTerm)) continue bookLoop;
                }
            }

            books.add(b);
        }

        return books;
    }
}
