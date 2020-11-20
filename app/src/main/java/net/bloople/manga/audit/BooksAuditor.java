package net.bloople.manga.audit;

import android.content.Context;

import net.bloople.manga.Book;
import net.bloople.manga.Library;

public class BooksAuditor {
    private Context context;

    public BooksAuditor(Context context) {
        this.context = context;
    }

    public void opened(Library library, Book book) {
        AuditEvent event = new AuditEvent(
            System.currentTimeMillis(),
            Action.BOOK_OPENED,
            ResourceType.LIBRARY,
            library.id(),
            ResourceType.BOOK,
            book.id(),
            book.title(),
            ""
        );
        event.save(context);
    }

    public void bookmarked(Library library, Book book, int page) {
        AuditEvent event = new AuditEvent(
            System.currentTimeMillis(),
            Action.BOOK_BOOKMARKED,
            ResourceType.LIBRARY,
            library.id(),
            ResourceType.BOOK,
            book.id(),
            book.title(),
            "Bookmarked at page " + page
        );
        event.save(context);
    }

    public void resumed(Library library, Book book, int page) {
        AuditEvent event = new AuditEvent(
            System.currentTimeMillis(),
            Action.BOOK_RESUMED,
            ResourceType.LIBRARY,
            library.id(),
            ResourceType.BOOK,
            book.id(),
            book.title(),
            "Resumed at page " + page
        );
        event.save(context);
    }

    public void closed(Library library, Book book) {
        AuditEvent event = new AuditEvent(
            System.currentTimeMillis(),
            Action.BOOK_CLOSED,
            ResourceType.LIBRARY,
            library.id(),
            ResourceType.BOOK,
            book.id(),
            book.title(),
            ""
        );
        event.save(context);
    }
}
