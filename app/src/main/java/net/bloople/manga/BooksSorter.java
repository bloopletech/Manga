package net.bloople.manga;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

class BooksSorter {
    static final int SORT_ALPHABETIC = 0;
    static final int SORT_AGE = 1;
    static final int SORT_LENGTH = 2;
    static final int SORT_LAST_OPENED = 3;
    static final int SORT_OPENED_COUNT = 4;
    static final int SORT_RANDOM = 5;

    private int sortMethod = SORT_AGE;
    private boolean sortDirectionAsc = false;

    BooksSorter() {
    }

    int getSortMethod() {
        return sortMethod;
    }

    void setSortMethod(int sortMethod) {
        this.sortMethod = sortMethod;
    }

    boolean getSortDirectionAsc() {
        return sortDirectionAsc;
    }

    void setSortDirectionAsc(boolean sortDirectionAsc) {
        this.sortDirectionAsc = sortDirectionAsc;
    }

    void flipSortDirection() {
        sortDirectionAsc = !sortDirectionAsc;
    }

    String description() {
        return "Sorted by " + sortMethodDescription().toLowerCase() + " " + sortDirectionDescription().toLowerCase();
    }

    String sortMethodDescription() {
        switch(sortMethod) {
            case SORT_ALPHABETIC: return "Title";
            case SORT_AGE: return "Published Date";
            case SORT_LENGTH: return "Page Count";
            case SORT_LAST_OPENED: return "Last Opened At";
            case SORT_OPENED_COUNT: return "Opened Count";
            case SORT_RANDOM: return "Random";
            default: throw new IllegalStateException("sort_method not in valid range");
        }
    }

    String sortDirectionDescription() {
        return sortDirectionAsc ? "Ascending" : "Descending";
    }

    void sort(Context context, ArrayList<Book> books) {
        if(books.isEmpty()) return;

        if(sortMethod == SORT_LAST_OPENED) {
            sortLastOpened(context, books);
        }
        else if(sortMethod == SORT_OPENED_COUNT) {
            sortOpenedCount(context, books);
        }
        else if(sortMethod == SORT_RANDOM) {
            sortRandom(books);
        }
        else {
            Collections.sort(books, (a, b) -> {
                switch(sortMethod) {
                    case SORT_ALPHABETIC:
                        return a.getNormalisedTitle().compareTo(b.getNormalisedTitle());
                    case SORT_AGE:
                        return Integer.compare(a.getPublishedOn(), b.getPublishedOn());
                    case SORT_LENGTH:
                        return Integer.compare(a.getPages(), b.getPages());
                }

                return 0;
            });

            if(!sortDirectionAsc) Collections.reverse(books);
        }
    }

    private void sortLastOpened(Context context, ArrayList<Book> books) {
        final HashMap<Long, BookMetadata> booksMetadata = metadataForBooks(context, books);

        Collections.sort(books, (a, b) -> {
            BookMetadata abm = booksMetadata.get(a.getId());
            BookMetadata bbm = booksMetadata.get(b.getId());

            if(abm == null && bbm == null) return 0;
            if(abm == null) return 1;
            if(bbm == null) return -1;

            if(sortDirectionAsc) return Long.compare(abm.lastOpenedAt(), bbm.lastOpenedAt());
            else return Long.compare(bbm.lastOpenedAt(), abm.lastOpenedAt());
        });
    }

    private void sortOpenedCount(Context context, ArrayList<Book> books) {
        final HashMap<Long, BookMetadata> booksMetadata = metadataForBooks(context, books);

        Collections.sort(books, (a, b) -> {
            BookMetadata abm = booksMetadata.get(a.getId());
            BookMetadata bbm = booksMetadata.get(b.getId());

            if(abm == null && bbm == null) return 0;
            if(abm == null) return 1;
            if(bbm == null) return -1;

            if(sortDirectionAsc) return Integer.compare(abm.openedCount(), bbm.openedCount());
            else return Integer.compare(bbm.openedCount(), abm.openedCount());
        });
    }

    private void sortRandom(ArrayList<Book> books) {
        long seed = System.currentTimeMillis() / (1000L * 60L * 30L);
        Collections.shuffle(books, new Random(seed));
        if(!sortDirectionAsc) Collections.reverse(books);
    }

    private HashMap<Long, BookMetadata> metadataForBooks(Context context, ArrayList<Book> books) {
        SQLiteDatabase db = DatabaseHelper.instance(context);

        StringBuilder sb = new StringBuilder();
        for(Book b : books) {
            if(sb.length() != 0) sb.append(",");
            sb.append(b.getId());
        }

        Cursor result = db.rawQuery("SELECT * FROM books_metadata WHERE book_id IN (" + sb.toString() + ")", new String[] {});

        HashMap<Long, BookMetadata> booksMetadata = new HashMap<>();

        while(result.moveToNext()) {
            BookMetadata bookMetadata = new BookMetadata(result);
            booksMetadata.put(bookMetadata.bookId(), bookMetadata);
        }

        result.close();

        return booksMetadata;
    }
}
