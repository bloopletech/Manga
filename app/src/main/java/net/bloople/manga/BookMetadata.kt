package net.bloople.manga

import android.content.ContentValues
import android.database.Cursor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
        val db = DatabaseHelper.instance()
        if(id == -1L) {
            id = db.insertOrThrow("books_metadata", null, values)
        }
        else {
            db.update("books_metadata", values, "_id=?", arrayOf(id.toString()))
        }
    }

    companion object {
        fun findById(id: Long): BookMetadata? {
            val db = DatabaseHelper.instance()
            db.rawQuery("SELECT * FROM books_metadata WHERE _id=?", arrayOf(id.toString())).use {
                it.moveToFirst()
                return if (it.count > 0) BookMetadata(it) else null
            }
        }

        suspend fun findAllByBookIds(books: ArrayList<Book>): Map<Long, BookMetadata> {
            if(books.isEmpty()) return emptyMap()

            return withContext(Dispatchers.IO) {
                val db = DatabaseHelper.instance()
                val sb = StringBuilder()
                for(b in books) {
                    if(sb.isNotEmpty()) sb.append(",")
                    sb.append(b.id)
                }

                db.rawQuery("SELECT * FROM books_metadata WHERE book_id IN ($sb)", arrayOf()).use {
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
            val db = DatabaseHelper.instance()
            db.rawQuery("SELECT * FROM books_metadata WHERE book_id=?", arrayOf(bookId.toString())).use {
                it.moveToFirst()
                return if (it.count > 0) {
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