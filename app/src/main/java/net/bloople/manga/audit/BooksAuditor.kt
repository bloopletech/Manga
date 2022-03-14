package net.bloople.manga.audit

import android.content.Context
import net.bloople.manga.Book
import net.bloople.manga.Library

class BooksAuditor(private val context: Context) {
    fun opened(library: Library, book: Book, page: Int) {
        val event = AuditEvent(
            System.currentTimeMillis(),
            Action.BOOK_OPENED,
            ResourceType.LIBRARY,
            library.id,
            ResourceType.BOOK,
            book.id,
            book.title,
            "Page $page"
        )
        event.save(context)
    }

    fun closed(library: Library, book: Book, page: Int) {
        val event = AuditEvent(
            System.currentTimeMillis(),
            Action.BOOK_CLOSED,
            ResourceType.LIBRARY,
            library.id,
            ResourceType.BOOK,
            book.id,
            book.title,
            "Page $page"
        )
        event.save(context)
    }
}