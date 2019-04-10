package net.bloople.manga;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

class BooksSorter {
    static final int SORT_ALPHABETIC = 0;
    static final int SORT_AGE = 1;
    static final int SORT_LENGTH = 2;
    static final int SORT_LAST_OPENED = 3;

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

    public void setSortDirectionAsc(boolean sortDirectionAsc) {
        this.sortDirectionAsc = sortDirectionAsc;
    }

    void flipSortDirection() {
        sortDirectionAsc = !sortDirectionAsc;
    }

    void sort(Context context, ArrayList<Book> books) {
        if(books.isEmpty()) return;

        if(sortMethod == SORT_LAST_OPENED) {
            sortLastOpened(context, books);
        }
        else {
            Collections.sort(books, (a, b) -> {
                switch(sortMethod) {
                    case SORT_ALPHABETIC:
                        return a.normalisedTitle().compareTo(b.normalisedTitle());
                    case SORT_AGE:
                        return Integer.compare(a.publishedOn(), b.publishedOn());
                    case SORT_LENGTH:
                        return Integer.compare(a.pages(), b.pages());
                }

                return 0;
            });

            if(!sortDirectionAsc) Collections.reverse(books);
        }
    }

    private void sortLastOpened(Context context, ArrayList<Book> books) {
        final HashMap<Long, BookMetadata> booksMetadata = metadataForBooks(context, books);

        Collections.sort(books, (a, b) -> {
            BookMetadata abm = booksMetadata.get(a.id());
            BookMetadata bbm = booksMetadata.get(b.id());

            if(abm == null && bbm == null) return 0;
            if(abm == null) return 1;
            if(bbm == null) return -1;

            if(sortDirectionAsc) return Long.compare(abm.lastOpenedAt(), bbm.lastOpenedAt());
            else return Long.compare(bbm.lastOpenedAt(), abm.lastOpenedAt());
        });
    }

    private HashMap<Long, BookMetadata> metadataForBooks(Context context, ArrayList<Book> books) {
        SQLiteDatabase db = DatabaseHelper.instance(context);

        StringBuilder sb = new StringBuilder();
        for(Book b : books) {
            if(sb.length() != 0) sb.append(",");
            sb.append(b.id());
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
