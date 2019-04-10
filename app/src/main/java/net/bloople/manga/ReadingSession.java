package net.bloople.manga;

import android.content.Context;
import android.net.Uri;

class ReadingSession {
    private Context context;
    private Book book;
    private int currentPage;

    ReadingSession(Context context, Book book) {
        this.context = context;
        this.book = book;
    }

    private int lastPage() {
        return book.pages() - 1;
    }

    private boolean validPage(int page) {
        return page >= 0 && page <= lastPage();
    }

    int page() {
        return currentPage;
    }

    void page(int page) {
        if(validPage(page)) currentPage = page;
    }

    int nextPage() {
        return Math.min(currentPage + 1, lastPage());
    }

    boolean go(int change) {
        int page = currentPage + change;
        if(!validPage(page)) return false;
        if(page == currentPage) return false;
        currentPage = page;
        return true;
    }

    void bookmark() {
        BookMetadata bookMetadata = BookMetadata.findOrCreateByBookId(context, book.id());
        bookMetadata.lastReadPosition(currentPage);
        bookMetadata.save(context);
    }

    void resume() {
        BookMetadata bookMetadata = BookMetadata.findOrCreateByBookId(context, book.id());
        page(bookMetadata.lastReadPosition());
    }

    Uri url() {
        return book.pageUrl(currentPage);
    }

    Uri url(int page) {
        if(!validPage(page)) return null;
        return book.pageUrl(page);
    }
}
