package net.bloople.manga;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    ArrayList<Book> search(Library library) {
        ArrayList<Book> books = new ArrayList<>();

        ArrayList<String> searchTerms = parseSearchTerms();

        bookLoop:
        for(Map.Entry<Long, Book> entry : library.books().entrySet()) {
            if(filterIds != null && !filterIds.isEmpty() && !filterIds.contains(entry.getKey())) continue;

            Book b = entry.getValue();

            String compareTitle = b.getTitle().toLowerCase();

            for(String searchTerm : searchTerms) {
                if(searchTerm.startsWith("-")) {
                    String realSearchTerm = searchTerm.substring(1);

                    if(realSearchTerm.equals(SPECIAL_LONG_BOOK)) {
                        if(b.getPages() >= LONG_BOOK_PAGES) continue bookLoop;
                    }
                    else if(compareTitle.contains(realSearchTerm)) continue bookLoop;
                }
                else {
                    if(searchTerm.equals(SPECIAL_LONG_BOOK)) {
                        if(b.getPages() < LONG_BOOK_PAGES) continue bookLoop;
                    }
                    else if(!compareTitle.contains(searchTerm)) continue bookLoop;
                }
            }

            books.add(b);
        }

        return books;
    }

    private ArrayList<String> parseSearchTerms() {
        ArrayList<String> terms = new ArrayList<>();

        Pattern searchPattern = Pattern.compile("\"[^\"]*\"|[^ ]+");
        Matcher matcher = searchPattern.matcher(searchText.toLowerCase());

        while(matcher.find()) terms.add(matcher.group().replace("\"", ""));

        return terms;
    }
}
