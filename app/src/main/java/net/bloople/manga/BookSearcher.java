package net.bloople.manga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

/**
 * Created by i on 30/07/2016.
 */
public class BookSearcher {
    public static final int LONG_BOOK_PAGES = 100;
    public static final String SPECIAL_LONG_BOOK = "s.long";
    public static final int SORT_ALPHABETIC = 0;
    public static final int SORT_AGE = 1;
    public static final int SORT_LENGTH = 2;
    public static final int SORT_LAST_OPENED = 3;

    private int sortMethod = SORT_AGE;
    private boolean sortDirectionAsc = false;
    private String searchText = "";
    private ArrayList<Long> filterIds;

    public void setSearchText(String inSearchText) {
        searchText = inSearchText;
    }

    public int getSortMethod() {
        return sortMethod;
    }

    public void setSortMethod(int sortMethod) {
        this.sortMethod = sortMethod;
    }

    public void setSortDirectionAsc(boolean sortDirectionAsc) {
        this.sortDirectionAsc = sortDirectionAsc;
    }

    public void flipSortDirection() {
        sortDirectionAsc = !sortDirectionAsc;
    }

    public void setFilterIds(ArrayList<Long> filterIds) {
        this.filterIds = filterIds;
    }

    public ArrayList<Book> filter() {
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

    public ArrayList<Long> resolve() {
        ArrayList<Book> books = filter();

        sort(books);

        ArrayList<Long> bookIds = new ArrayList<>();
        for(Book b : books) bookIds.add(b.id());

        return bookIds;
    }

    private void sort(ArrayList<Book> books) {
        Collections.sort(books, new Comparator<Book>() {
            @Override
            public int compare(Book a, Book b)
            {
                switch(sortMethod) {
                    case SORT_ALPHABETIC:
                        return a.title().compareTo(b.title());
                    case SORT_AGE:
                        return Integer.compare(a.publishedOn(), b.publishedOn());
                    case SORT_LENGTH:
                        return Integer.compare(a.pages(), b.pages());
                    case SORT_LAST_OPENED:
                        break;
                }

                return 0;
            }
        });

        if(!sortDirectionAsc) Collections.reverse(books);
    }
}
