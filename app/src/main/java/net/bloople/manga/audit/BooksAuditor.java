package net.bloople.manga.audit;

import android.content.Context;

import net.bloople.manga.Book;
import net.bloople.manga.Library;

public class BooksAuditor {
    private Context context;

    public BooksAuditor(Context context) {
        this.context = context;
    }

    public void opened(Library library, Book book, int page) {
        AuditEvent event = new AuditEvent(
            System.currentTimeMillis(),
            Action.BOOK_OPENED,
            ResourceType.LIBRARY,
            library.id(),
            ResourceType.BOOK,
            book.id(),
            book.title(),
            "Page " + page
        );
        event.save(context);
    }

    public void closed(Library library, Book book, int page) {
        AuditEvent event = new AuditEvent(
            System.currentTimeMillis(),
            Action.BOOK_CLOSED,
            ResourceType.LIBRARY,
            library.id(),
            ResourceType.BOOK,
            book.id(),
            book.title(),
            "Page " + page
        );
        event.save(context);
    }
}
