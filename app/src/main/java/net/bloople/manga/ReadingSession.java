package net.bloople.manga;

import android.content.Context;

class ReadingSession {
    private Context context;
    private Book book;
    private int currentPage;

    ReadingSession(Context context, Book book) {
        this.context = context;
        this.book = book;
    }

    private int lastPage() {
        return book.pages - 1;
    }

    private int clamp(int page) {
        if(page < 0) return 0;
        if(page > lastPage()) return lastPage();
        return page;
    }

    int page() {
        return currentPage;
    }

    void page(int page) {
        currentPage = clamp(page);
    }

    boolean isBeginning() {
        return currentPage == 0;
    }

    int nextPage() {
        return clamp(currentPage + 1);
    }

    void go(int change) {
        currentPage = clamp(currentPage + change);
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

    void finish() {
        if(currentPage == lastPage()) {
            currentPage = 0;
            bookmark();
        }
    }

    String url() {
        return book.pageUrl(currentPage);
    }

    String url(int page) {
        return book.pageUrl(clamp(page));
    }
}
