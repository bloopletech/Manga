package net.bloople.manga.audit;

import android.content.Context;

import net.bloople.manga.Book;

public class BooksAuditor {
    private Context context;

    public BooksAuditor(Context context) {
        this.context = context;
    }

    public void opened(Book book) {
        AuditEvent event = new AuditEvent(
            System.currentTimeMillis(),
            Action.BOOK_OPENED,
            ResourceType.BOOK,
            book.id(),
            book.title(),
            ""
        );
        event.save(context);
    }

    public void bookmarked(Book book, int page) {
        AuditEvent event = new AuditEvent(
            System.currentTimeMillis(),
            Action.BOOK_BOOKMARKED,
            ResourceType.BOOK,
            book.id(),
            book.title(),
            "Bookmarked at page " + page
        );
        event.save(context);
    }

    public void resumed(Book book, int page) {
        AuditEvent event = new AuditEvent(
            System.currentTimeMillis(),
            Action.BOOK_RESUMED,
            ResourceType.BOOK,
            book.id(),
            book.title(),
            "Resumed at page " + page
        );
        event.save(context);
    }

    public void closed(Book book) {
        AuditEvent event = new AuditEvent(
            System.currentTimeMillis(),
            Action.BOOK_CLOSED,
            ResourceType.BOOK,
            book.id(),
            book.title(),
            ""
        );
        event.save(context);
    }
}
