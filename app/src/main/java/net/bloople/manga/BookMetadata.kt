package net.bloople.manga

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import java.lang.StringBuilder
import java.util.ArrayList
import java.util.HashMap

class BookMetadata {
    var _id = -1L
    var bookId: Long = 0
    var lastOpenedAt: Long = 0
    var lastReadPosition = 0
    var openedCount = 0

    constructor()
    constructor(result: Cursor) {
        _id = result["_id"]
        bookId = result["book_id"]
        lastOpenedAt = result["last_opened_at"]
        lastReadPosition = result["last_read_position"]
        openedCount = result["opened_count"]
    }

    fun save(context: Context) {
        val values = ContentValues()
        values.put("book_id", bookId)
        values.put("last_opened_at", lastOpenedAt)
        values.put("last_read_position", lastReadPosition)
        values.put("opened_count", openedCount)
        val db = DatabaseHelper.instance(context)
        if(_id == -1L) {
            _id = db.insertOrThrow("books_metadata", null, values)
        }
        else {
            db.update("books_metadata", values, "_id=?", arrayOf(_id.toString()))
        }
    }

    companion object {
        fun findById(context: Context, id: Long): BookMetadata? {
            val db = DatabaseHelper.instance(context)
            db.rawQuery("SELECT * FROM books_metadata WHERE _id=?", arrayOf(id.toString())).use {
                it.moveToFirst()
                return if (it.count > 0) BookMetadata(it) else null
            }
        }

        fun findAllByBookIds(context: Context, books: ArrayList<Book>): HashMap<Long, BookMetadata> {
            val db = DatabaseHelper.instance(context)
            val sb = StringBuilder()
            for(b in books) {
                if(sb.isNotEmpty()) sb.append(",")
                sb.append(b.id)
            }

            db.rawQuery("SELECT * FROM books_metadata WHERE book_id IN ($sb)", arrayOf()).use {
                it.moveToFirst()
                val booksMetadata = HashMap<Long, BookMetadata>()
                while(it.moveToNext()) {
                    val bookMetadata = BookMetadata(it)
                    booksMetadata[bookMetadata.bookId] = bookMetadata
                }
                return booksMetadata
            }
        }

        fun findOrCreateByBookId(context: Context, bookId: Long): BookMetadata {
            val db = DatabaseHelper.instance(context)
            db.rawQuery("SELECT * FROM books_metadata WHERE book_id=?", arrayOf(bookId.toString())).use {
                it.moveToFirst()
                return if (it.count > 0) {
                    BookMetadata(it)
                }
                else {
                    val bookMetadata = BookMetadata()
                    bookMetadata.bookId = bookId
                    bookMetadata.save(context)
                    bookMetadata
                }
            }
        }
    }
}