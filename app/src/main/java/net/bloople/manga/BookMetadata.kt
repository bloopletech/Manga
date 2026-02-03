package net.bloople.manga

import android.content.ContentValues
import android.database.Cursor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.bloople.manga.db.DatabaseAdapter
import net.bloople.manga.db.DatabaseHelper
import java.lang.StringBuilder
import java.util.ArrayList
import java.util.HashMap

class BookMetadata {
    var id = -1L
    var bookId: Long = 0
    var lastOpenedAt: Long = 0
    var lastReadPosition = 0
    var openedCount = 0

    constructor()
    constructor(result: Cursor) {
        id = result["_id"]
        bookId = result["book_id"]
        lastOpenedAt = result["last_opened_at"]
        lastReadPosition = result["last_read_position"]
        openedCount = result["opened_count"]
    }

    fun save() {
        val values = ContentValues()
        values.put("book_id", bookId)
        values.put("last_opened_at", lastOpenedAt)
        values.put("last_read_position", lastReadPosition)
        values.put("opened_count", openedCount)
        if(id == -1L) {
            id = dba.insert(values)
        }
        else {
            dba.update(values, id)
        }
    }

    fun destroy() {
        dba.delete(id)
    }

    companion object {
        private val dba: DatabaseAdapter
            get() = DatabaseAdapter(DatabaseHelper.instance(), "books_metadata")

        fun find(id: Long) = dba.find(id) { BookMetadata(it) }

        suspend fun findAllByBookIds(books: ArrayList<Book>): Map<Long, BookMetadata> {
            if(books.isEmpty()) return emptyMap()

            return withContext(Dispatchers.IO) {
                val sb = StringBuilder()
                for(b in books) {
                    if(sb.isNotEmpty()) sb.append(",")
                    sb.append(b.id)
                }

                dba.query("SELECT * FROM books_metadata WHERE book_id IN ($sb)") {
                    val booksMetadata = HashMap<Long, BookMetadata>()
                    while(it.moveToNext()) {
                        val bookMetadata = BookMetadata(it)
                        booksMetadata[bookMetadata.bookId] = bookMetadata
                    }
                    booksMetadata
                }
            }
        }

        fun findOrCreateByBookId(bookId: Long): BookMetadata {
            return dba.query("SELECT * FROM books_metadata WHERE book_id=?", bookId.toString()) {
                it.moveToFirst()
                if (it.count > 0) {
                    BookMetadata(it)
                }
                else {
                    val bookMetadata = BookMetadata()
                    bookMetadata.bookId = bookId
                    bookMetadata.save()
                    bookMetadata
                }
            }
        }

        val EMPTY = BookMetadata()
    }
}